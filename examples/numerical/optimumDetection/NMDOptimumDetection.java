package numerical.optimumDetection;

import java.util.Arrays;

import function.common.benchmark.RosenbrockBananaFunction;
import function.common.benchmark.SchaffersFunction7;
import function.decorators.DiscreteFunctionWrapper;
import function.decorators.FunctionCallCounterWrapper;
import numerical.NelderMeadDiscreteSimplex;
import optimization.algorithm.IOptimizationAlgorithm;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.decoder.IDecoder;
import optimization.decoder.PassThroughIntegerDecoder;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.NegateFitnessEvaluator;
import optimization.fittnesEvaluator.observable.BestObserver;
import optimization.fittnesEvaluator.observable.PerChromosomeObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PrintBestObserver;
import optimization.solution.IntegerArraySolution;
import optimization.startPopulationGenerator.IStartPopulationGenerator;
import optimization.startPopulationGenerator.RandomDiscreteStartPopulationGenerator;
import optimization.stopper.CompositeOptimisationStopper;
import optimization.stopper.FunctionValueStopper;
import optimization.stopper.GenerationNumberEvolutionStopper;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.AlgorithmsPresentationUtility;

public class NMDOptimumDetection {
	
	public static void main(String[] args) {

		int populationSize = 50;
		double alfa = 1;
		double beta = 2.5;
		double gamma = 0.5;

		//Function
		FunctionCallCounterWrapper<int[]> function = new FunctionCallCounterWrapper<>(new DiscreteFunctionWrapper(
					new RosenbrockBananaFunction()
					//new SchaffersFunction7(8)
				));
		
		//Start point generator
		IStartPopulationGenerator<IntegerArraySolution> startPopulationGenerator = 
				new RandomDiscreteStartPopulationGenerator(populationSize , function.getVariableCount(), -50, -25);

		//Decoder
		IDecoder<IntegerArraySolution, int[]> decoder = new PassThroughIntegerDecoder();

		//Evaluator
		PerChromosomeObservableFitnessEvaluator<IntegerArraySolution> evaluator = new PerChromosomeObservableFitnessEvaluator<>(
				v -> NegateFitnessEvaluator.evaluationMethod
						.applyAsDouble(FunctionValueFitnessEvaluator.evaluationMethod.applyAsDouble(v)));
		evaluator.addObserver(new BestObserver<>(decoder,
				Arrays.asList(new PrintBestObserver<IntegerArraySolution, int[]>(System.out)),
				true));
				
		//Stopper
		IOptimisationStopper<IntegerArraySolution> stopper = new CompositeOptimisationStopper<>(Arrays.asList(
					new FunctionValueStopper<IntegerArraySolution>(1e-3),
					new GenerationNumberEvolutionStopper<>(5000)
				));
		
		
		//Optimization algorithm
		IOptimizationAlgorithm<IntegerArraySolution> optimizationAlgorithm = 
				new NelderMeadDiscreteSimplex(function, startPopulationGenerator, alfa, beta, gamma, decoder, evaluator, stopper);
		
		TimedOptimizationAlgorithm<IntegerArraySolution> timedOptAlgorithm = new TimedOptimizationAlgorithm<>(optimizationAlgorithm);
		
		//Solution presentation
		IntegerArraySolution solution = timedOptAlgorithm.run();
		System.out.println();
		AlgorithmsPresentationUtility.printExecutionTime(timedOptAlgorithm.getExecutionTime());
		System.out.println("Solution: "  + solution);
		System.out.println("Value: " + function.applyAsDouble(solution.getValues()));
		AlgorithmsPresentationUtility.printEvaluationCount(function.getEvaluationCount());

	}

}
