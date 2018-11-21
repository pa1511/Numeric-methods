package numerical.curveFitting.coordinateAxes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import dataset.handeling.DataSetLoader;
import function.decorators.FunctionCallCounterWrapper;
import function.error.PrototypeBasedSystemLossFunction;
import function.error.SquareErrorFunction;
import function.prototype.LinearFunction;
import numerical.CoordinateAxesAlgorithm;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.decoder.PassThroughDoubleDecoder;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.fittnesEvaluator.observable.BestObserver;
import optimization.fittnesEvaluator.observable.PerChromosomeObservableFitnessEvaluator;
import optimization.fittnesEvaluator.observable.PrintBestObserver;
import optimization.solution.DoubleArraySolution;
import optimization.stopper.CompositeOptimisationStopper;
import optimization.stopper.FunctionValueStopper;
import optimization.stopper.GenerationNumberEvolutionStopper;
import optimization.stopper.IOptimisationStopper;
import optimization.utility.AlgorithmsPresentationUtility;

public class LinearSystemParameterDetection {

	private LinearSystemParameterDetection() {}
	
	
	public static void main(String[] args) throws IOException {
				
		//Function to optimize
		LinearFunction linearFunction = new LinearFunction();
		double[][] systemMatrix = DataSetLoader.loadMatrix(new File(System.getProperty("user.dir"),"data/linear-data.txt"));
		FunctionCallCounterWrapper<double[]> function =  new FunctionCallCounterWrapper<>(
				new PrototypeBasedSystemLossFunction(systemMatrix,linearFunction,new SquareErrorFunction()));		
		 
		double acceptableErrorRate = 1e-6;
		int maximumNumberOfGenerations = 100; 
		
		//Fitness evaluator
		PerChromosomeObservableFitnessEvaluator<DoubleArraySolution> evaluator = new PerChromosomeObservableFitnessEvaluator<>(v->
		 ThroughOneFitnessEvaluator.evaluationMethod.applyAsDouble(
					FunctionValueFitnessEvaluator.evaluationMethod.applyAsDouble(v)
				 ));
		evaluator.addObserver(new BestObserver<>(new PassThroughDoubleDecoder(), Arrays.asList(
				new PrintBestObserver<DoubleArraySolution,double[]>(System.out)
		)));

		//Optimization stopper
		IOptimisationStopper<DoubleArraySolution> stopper = new CompositeOptimisationStopper<>(Arrays.asList(
			new FunctionValueStopper<>(acceptableErrorRate),
			new GenerationNumberEvolutionStopper<>(maximumNumberOfGenerations)
		));

		//Start solution
		DoubleArraySolution startSolution = new DoubleArraySolution(new double[]{5,5});
		
		//Optimization algorithm
		CoordinateAxesAlgorithm optimizationAlgorithm = new CoordinateAxesAlgorithm(function, 
				evaluator,stopper, startSolution, acceptableErrorRate);		
		TimedOptimizationAlgorithm<DoubleArraySolution> timedOptAlgorithm = new TimedOptimizationAlgorithm<>(optimizationAlgorithm);
		
		//Solution presentation
		DoubleArraySolution solution = timedOptAlgorithm.run();
		AlgorithmsPresentationUtility.printExecutionTime(timedOptAlgorithm.getExecutionTime());
		System.out.println("Solution: "  + solution);
		System.out.println("Error: " + function.applyAsDouble(solution.getValues()));
		AlgorithmsPresentationUtility.printEvaluationCount(function.getEvaluationCount());
	}
	
}
