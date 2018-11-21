package numerical;

import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;

import optimization.algorithm.ISingleUnitOptimizationAlgorithm;
import optimization.fittnesEvaluator.IFitnessEvaluator;
import optimization.solution.DoubleArraySolution;
import optimization.startSolutionGenerator.IStartSolutionGenerator;
import optimization.stopper.IOptimisationStopper;
import util.VectorUtils;
import utilities.PArrays;


public class HookeJeevesAlgorithm implements ISingleUnitOptimizationAlgorithm<DoubleArraySolution>{
	

	private final @Nonnull ToDoubleFunction<double[]> function;
	private final @Nonnull double[] xD;
	private double reductionFactor;
	private final double minimalStepLimit;
	private IFitnessEvaluator<DoubleArraySolution> evaluator;
	private IOptimisationStopper<DoubleArraySolution> stopper;
	private IStartSolutionGenerator<DoubleArraySolution> startSolutionGenerator;

	public HookeJeevesAlgorithm(@Nonnull ToDoubleFunction<double[]> function, 
			IFitnessEvaluator<DoubleArraySolution> evaluator,
			IOptimisationStopper<DoubleArraySolution> stopper,
			@Nonnull IStartSolutionGenerator<DoubleArraySolution> startSolutionGenerator, 
			@Nonnull double[] xD,
			double reductionFactor, double minimalStepLimit) {
			this.function = function;
			this.evaluator = evaluator;
			this.stopper = stopper;
			this.startSolutionGenerator = startSolutionGenerator;
			this.xD = xD;
			this.reductionFactor = reductionFactor;
			this.minimalStepLimit = minimalStepLimit;
	}
	
	@Override
	public DoubleArraySolution run() {
		DoubleArraySolution x0 = startSolutionGenerator.generate();
		DoubleArraySolution xP = x0.clone();
		DoubleArraySolution xB = x0.clone();
		DoubleArraySolution xN = new DoubleArraySolution(new double[x0.values.length]);

		double xBValue;
		double xBFitness;
		
		do {
			investigate(xN,xP);
			
			double xNValue = function.applyAsDouble(xN.values);
			xBValue = function.applyAsDouble(xB.values);
			
			double xNFitness = evaluator.evaluate(xN, xNValue);
			xBFitness = evaluator.evaluate(xB, xBValue);

			if (xNFitness>xBFitness){
				getNewXp(xP, xN, xB);
				
				//switch
				DoubleArraySolution help = xB;
				xB = xN;
				xBFitness = xNFitness;
				xN = help;
			} else {
				VectorUtils.multiplyByScalar(xD, reductionFactor, xD, false);
				System.arraycopy(xB.values, 0, xP.values, 0, xP.values.length);
			}
						
		} while (!stopper.shouldStop(xB, xBFitness, xBValue) && !PArrays.allMatch(xD,x->x<=minimalStepLimit));

		return new DoubleArraySolution(xB.values);
	}

	private void getNewXp(DoubleArraySolution xP, DoubleArraySolution xN, DoubleArraySolution xB) {
		for (int i = 0; i < xN.values.length; i++) {
			xP.values[i] = 2 * xN.values[i] - xB.values[i];
		}
	}

	private void investigate(DoubleArraySolution xN, DoubleArraySolution xP) {

		System.arraycopy(xP.values, 0, xN.values, 0, xP.values.length);

		for (int i = 0; i < xN.values.length; i++) {
			double P = function.applyAsDouble(xN.values);

			xN.values[i] = xN.values[i] + xD[i];
			double N = function.applyAsDouble(xN.values);

			if (N > P) {

				xN.values[i] = xN.values[i] - 2 * xD[i];
				N = function.applyAsDouble(xN.values);

				if (N > P) {
					xN.values[i] = xN.values[i] + xD[i];
				}

			}

		}
		
	}
	
}
