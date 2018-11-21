package numerical;

import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;

import optimization.algorithm.ISingleUnitOptimizationAlgorithm;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.solution.DoubleArraySolution;
import optimization.stopper.IOptimisationStopper;

public class CoordinateAxesAlgorithm implements ISingleUnitOptimizationAlgorithm<DoubleArraySolution>{

	private ToDoubleFunction<double[]> function;
	private IFitnessEvaluator<DoubleArraySolution> evaluator;
	private IOptimisationStopper<DoubleArraySolution> stopper;
	private DoubleArraySolution xS;
	private double e;

	public CoordinateAxesAlgorithm(@Nonnull ToDoubleFunction<double[]> function, 
			IFitnessEvaluator<DoubleArraySolution> evaluator,
			IOptimisationStopper<DoubleArraySolution> stopper, 
			@Nonnull DoubleArraySolution xS, @Nonnull double e) {
		this.function = function;
		this.evaluator = evaluator;
		this.stopper = stopper;
		this.xS = xS;
		this.e = e;
	}


	@Override
	public DoubleArraySolution run() {
		
		DoubleArraySolution x = xS.clone();
		double value;
		double fitness;

		GoldenCut goldenCut = new GoldenCut();		
		double[] interval = new double[2];
		do{
			
			for(int i=0; i<x.values.length;i++){
				UnimodalFinder.findUnimodalInterval(function, x.values, i, 1.0, interval);
				goldenCut.optimiseMultyDimension(function, x.values, interval, i, e);
				x.values[i] = (interval[0]+interval[1])/2;
			}
			
			value = function.applyAsDouble(x.values);
			fitness = evaluator.evaluate(x, value);
			
		}while(!stopper.shouldStop(x, fitness, value));

		return x;
	}
	
}
