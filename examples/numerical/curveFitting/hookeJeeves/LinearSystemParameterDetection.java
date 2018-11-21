package numerical.curveFitting.hookeJeeves;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import dataset.handeling.DataSetLoader;
import function.decorators.FunctionCallCounterWrapper;
import function.error.PrototypeBasedSystemLossFunction;
import function.error.SquareErrorFunction;
import function.prototype.LinearFunction;
import numerical.HookeJeevesAlgorithm;
import optimization.algorithm.decorator.TimedOptimizationAlgorithm;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.solution.DoubleArraySolution;
import optimization.startSolutionGenerator.IStartSolutionGenerator;
import optimization.startSolutionGenerator.RandomStartSolutionGenerator;
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
		 
		double acceptableErrorRate = 1e-3;
		int maximumNumberOfGenerations = 100; 
				
		//Fitness evaluator
		IFitnessEvaluator<DoubleArraySolution> evaluator = new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>());

		//Optimization stopper
		IOptimisationStopper<DoubleArraySolution> stopper = new CompositeOptimisationStopper<>(Arrays.asList(
			new FunctionValueStopper<>(acceptableErrorRate),
			new GenerationNumberEvolutionStopper<>(maximumNumberOfGenerations)
		));

		//Start solution generator
		IStartSolutionGenerator<DoubleArraySolution> startSolutionGenerator = new RandomStartSolutionGenerator(2,5,5);
		
		//Optimization algorithm
		HookeJeevesAlgorithm optimizationAlgorithm = new HookeJeevesAlgorithm(function, 
				evaluator,stopper, startSolutionGenerator, new double[]{2,2}, 0.5, 1e-2);		
		TimedOptimizationAlgorithm<DoubleArraySolution> timedOptAlgorithm = new TimedOptimizationAlgorithm<>(optimizationAlgorithm);
		
		//Solution presentation
		DoubleArraySolution solution = timedOptAlgorithm.run();
		AlgorithmsPresentationUtility.printExecutionTime(timedOptAlgorithm.getExecutionTime());
		System.out.println("Solution: "  + solution);
		System.out.println("Error: " + function.applyAsDouble(solution.getValues()));
		AlgorithmsPresentationUtility.printEvaluationCount(function.getEvaluationCount());
	}
	
}
