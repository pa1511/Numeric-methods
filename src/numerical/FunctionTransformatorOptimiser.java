package numerical;

import java.util.Collection;
import java.util.function.ToDoubleFunction;

import function.TransformFunction;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.solution.DoubleArraySolution;
import optimization.startSolutionGenerator.ProvidingStartSolutionGenerator;
import optimization.stopper.GenerationNumberEvolutionStopper;
import util.VectorUtils;

public class FunctionTransformatorOptimiser {

	

	public double[] run(ToDoubleFunction<double[]> f, Collection<ToDoubleFunction<double[]>> g, Collection<ToDoubleFunction<double[]>> h, double t, double precision, double[] X0){
		
		X0 = makeInnerPoint(X0,g);		
		
		TransformFunction transformFunction = new TransformFunction(f,g,h);
		double[] solution = X0;
		double[] change;
		
		do{
			
			transformFunction.setT(t);
			
			
			double[] xD = new double[X0.length];
			for(int i=0; i<xD.length;i++){
				xD[i] = 1;
			}

			ProvidingStartSolutionGenerator<DoubleArraySolution> solutionGenerator = new ProvidingStartSolutionGenerator<>();
			solutionGenerator.accept(new DoubleArraySolution(solution));
			
			HookeJeevesAlgorithm hookJeeves = new HookeJeevesAlgorithm(transformFunction,
					new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>()),
					new GenerationNumberEvolutionStopper<>(Integer.MAX_VALUE),
					solutionGenerator, xD, 0.5, 1e-6);
			double[] newSolution = hookJeeves.run().values;
						
			change = VectorUtils.subtract(solution, newSolution,false);
			solution = newSolution;
			
			t = t*10;
		}while(!isZeroVector(change, precision));
		
		
		return solution;
	}
	
	private double[] makeInnerPoint(double[] x0,final Collection<ToDoubleFunction<double[]>> g) {

		while(notInnerPoint(x0, g)){
			ToDoubleFunction<double[]> function = new ToDoubleFunction<double[]>() {
				
				@Override
				public double applyAsDouble(double[] X) {
					return g.stream().mapToDouble(gi->{
						double value = gi.applyAsDouble(X);
						if(value<0)
							return -1*value;
						return 0.0;
					}).sum();
				}
			};

			
			double[] xD = new double[x0.length];
			for(int i=0; i<xD.length;i++){
				xD[i] = 1;
			}
			
			ProvidingStartSolutionGenerator<DoubleArraySolution> solutionGenerator = new ProvidingStartSolutionGenerator<>();
			solutionGenerator.accept(new DoubleArraySolution(x0));

			HookeJeevesAlgorithm hookJeeves = new HookeJeevesAlgorithm(function,
					new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>()),
					new GenerationNumberEvolutionStopper<>(Integer.MAX_VALUE),
					solutionGenerator, xD, 0.5, 1e-6);
			x0 = hookJeeves.run().values;
			
		}
		
		return x0;
	}

	private boolean notInnerPoint(double[] x0, Collection<ToDoubleFunction<double[]>> g) {
		return g.stream().anyMatch(gi->gi.applyAsDouble(x0)<0);
	}

	private boolean isZeroVector(double[] change, double precision) {
		
		for(int i=0; i<change.length;i++){
			if(Math.abs(change[i])>=precision)
				return false;
		}
		
		return true;
	}
	
}
