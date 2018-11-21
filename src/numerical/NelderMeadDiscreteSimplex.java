package numerical;

import function.IFunction;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.solution.IntegerArraySolution;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.OptimizationAlgorithmsUtility;

public class NelderMeadDiscreteSimplex implements IOptimizationAlgorithm<IntegerArraySolution> {

	private final IFunction<int[]> function;
	private final IStartPopulationGenerator<IntegerArraySolution> startPopulationGenerator;
	private final double alfa;
	private final double beta;
	private final double gama;
	private final IDecoder<IntegerArraySolution, int[]> decoder;
	private final IFitnessEvaluator<IntegerArraySolution> evaluator;
	private final IOptimisationStopper<IntegerArraySolution> stopper;
	
	public NelderMeadDiscreteSimplex(IFunction<int[]> function, 
			IStartPopulationGenerator<IntegerArraySolution> startPopulationGenerator,
			IDecoder<IntegerArraySolution, int[]> decoder,
			IFitnessEvaluator<IntegerArraySolution> evaluator,
			IOptimisationStopper<IntegerArraySolution> stopper) {
		this(function, startPopulationGenerator, 1.0, 2.0, 0.5, decoder, evaluator, stopper);
	}
	
	public NelderMeadDiscreteSimplex(IFunction<int[]> function, 
			IStartPopulationGenerator<IntegerArraySolution> startPopulationGenerator,
			double alfa, double beta, double gama,
			IDecoder<IntegerArraySolution, int[]> decoder,
			IFitnessEvaluator<IntegerArraySolution> evaluator,
			IOptimisationStopper<IntegerArraySolution> stopper) {
		

		this.function = function;
		this.alfa = alfa;
		this.beta = beta;
		this.gama = gama;
		this.decoder = decoder;
		this.evaluator = evaluator;
		this.startPopulationGenerator = startPopulationGenerator;
		this.stopper = stopper;

	}

	@Override
	public IntegerArraySolution run() {
		
		IntegerArraySolution[] simplex = startPopulationGenerator.generate();
		double[] functionValues = new double[simplex.length];
		double[] functionFitness = new double[simplex.length];
		
		calculateFunctionValues(simplex,functionValues);
		calculateFunctionFitness(simplex, functionValues, functionFitness);
		
		final IntegerArraySolution centroid = new IntegerArraySolution(new int[simplex[0].values.length]);
		int bestId, worstId;
		
		final IntegerArraySolution reflected = new IntegerArraySolution(new int[simplex[0].values.length]);
		double reflectedFunctionValue, reflectedFunctionFitness;

		final IntegerArraySolution expansion = new IntegerArraySolution(new int[simplex[0].values.length]);
		double expansionFuncionValue, expansionFunctionFitness;
		
		final IntegerArraySolution contracted = new IntegerArraySolution(new int[simplex[0].values.length]); 
		double contractedFunctionValue, contractedFunctionFitness;

		do {
			
			bestId = getBesttIndex(functionFitness);
			worstId = getWorstIndex(functionFitness);
			//
			IntegerArraySolution.calculateCentroid(centroid,simplex, worstId);
			//
			
			//reflection
			reflect(reflected,centroid,simplex[worstId]);
			reflectedFunctionValue = function.applyAsDouble(decoder.decode(reflected));
			reflectedFunctionFitness = evaluator.evaluate(reflected, reflectedFunctionValue);
			
			if(reflectedFunctionFitness>functionValues[worstId]) {
				
				//expand
				expand(expansion,centroid,simplex[worstId]);
				expansionFuncionValue = function.applyAsDouble(decoder.decode(expansion));
				expansionFunctionFitness = evaluator.evaluate(expansion, expansionFuncionValue);
				
				//switching worst point
				if(expansionFunctionFitness>functionFitness[bestId]) {
					simplex[worstId].copy(expansion);
					functionValues[worstId] = expansionFuncionValue;
					functionFitness[worstId] = expansionFunctionFitness;
				}
				else {
					simplex[worstId].copy(reflected);
					functionValues[worstId] = reflectedFunctionValue;
					functionFitness[worstId] = reflectedFunctionFitness;
				}
				
			}
			else if(theWorseEver(reflectedFunctionFitness,functionFitness,worstId)){
				if(reflectedFunctionFitness>functionFitness[worstId]) {
					simplex[worstId].copy(reflected);
					functionValues[worstId] = reflectedFunctionValue;
					functionFitness[worstId] = reflectedFunctionFitness;
				}
				
				//contracted
				contract(contracted,centroid,simplex[worstId]);
				contractedFunctionValue = function.applyAsDouble(decoder.decode(contracted));
				contractedFunctionFitness = evaluator.evaluate(contracted, contractedFunctionValue);
				
				if(contractedFunctionFitness>functionFitness[worstId]) {
					simplex[worstId].copy(contracted);
					functionValues[worstId] = contractedFunctionValue;
					functionFitness[worstId] = contractedFunctionFitness;
				}
				else {
					moveAllToBest(simplex,bestId);
					calculateFunctionValues(simplex,functionValues);
					calculateFunctionFitness(simplex, functionValues, functionFitness);
				}
				
			}
			else {
				simplex[worstId].copy(reflected);
				functionValues[worstId] = reflectedFunctionValue;
				functionFitness[worstId] = reflectedFunctionFitness;
			}
						
		}while(!stopper.shouldStop(simplex, functionFitness, functionValues));
		
		
		return OptimizationAlgorithmsUtility.getBestSolution(simplex, functionFitness);
	}
	
	private void reflect(IntegerArraySolution reflected,IntegerArraySolution centroid, IntegerArraySolution x) {
		reflected.copy(centroid);
		for(int i=0; i<reflected.values.length; i++) {
			reflected.values[i] -= (int)(alfa*(x.values[i]-centroid.values[i])); 
		}
	}

	private void expand(IntegerArraySolution expanded,IntegerArraySolution centroid, IntegerArraySolution x) {
		expanded.copy(centroid.clone());
		for(int i=0; i<expanded.values.length; i++) {
			expanded.values[i] -= (int)(beta*(x.values[i]-centroid.values[i])); 
		}
	}

	private void contract(IntegerArraySolution contracted, IntegerArraySolution centroid, IntegerArraySolution x) {
		contracted.copy(centroid);
		for(int i=0; i<contracted.values.length; i++) {
			contracted.values[i] -= (int)(gama*(x.values[i]-centroid.values[i])); 
		}
	}

	private void moveAllToBest(IntegerArraySolution[] simplex, int bestId) {
		for(int i=0; i<simplex.length; i++) {
			if(i==bestId)
				continue;
			
			IntegerArraySolution point = simplex[i];
			for(int j=0; j<point.values.length; j++) {
				point.values[j] = (point.values[j]+simplex[bestId].values[j])/2;
			}
			
		}
		
	}
	
	private boolean theWorseEver(double reflectedFunctionFitness, double[] functionFitness, int worstId) {
		for(int i=0; i<functionFitness.length; i++) {
			if(i==worstId)
				continue;
			if(reflectedFunctionFitness>functionFitness[i])
				return false;
		}
		
		return true;
	}


	private int getWorstIndex(double[] functionFitness) {
		
		double min = Double.POSITIVE_INFINITY;
		int index = -1;
		
		for(int i=0; i<functionFitness.length; i++) {
			double fitness = functionFitness[i];
			if(fitness<min) {
				min = fitness;
				index = i;
			}
		}
		
		return index;
	}

	private int getBesttIndex(double[] functionFitness) {
		
		double max = Double.NEGATIVE_INFINITY;
		int index = -1;
		
		for(int i=0; i<functionFitness.length; i++) {
			double fitness = functionFitness[i];
			if(fitness>max) {
				max = fitness; 
				index = i;
			}
		}
		
		return index;
	}

	private void calculateFunctionValues(IntegerArraySolution[] simplex, double[] functionValues) {
		for (int i = 0; i < simplex.length; i++) {
			IntegerArraySolution solution = simplex[i];
			functionValues[i] = function.applyAsDouble(decoder.decode(solution));
		}
	}

	private void calculateFunctionFitness(IntegerArraySolution[] simplex, double[] functionValues,
			double[] functionFitness) {
		for (int i = 0; i < simplex.length; i++) {
			IntegerArraySolution solution = simplex[i];
			double functionValue = functionValues[i];
			functionFitness[i] = evaluator.evaluate(solution, functionValue);
		}
	}

}
