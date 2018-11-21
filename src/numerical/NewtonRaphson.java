package numerical;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import function.IHFunction;
import util.Matrix;
import util.VectorUtils;

public class NewtonRaphson {

	public double[] run(IHFunction function, double[] startPoint, double precision, boolean searchForOptimalMove){
		
		//Initialize main variables
		double[] solution = startPoint;
		double lambda = -1.0;
		double[] gradient, move;
		double[][] hess;
		double[] v = new double[solution.length];
		double lambdaPrecision = precision*1e-3;
		
		
		double minValue = Double.MAX_VALUE;
		int divergenceCount = 0;
		
		do{
			//Divergence check support
			double value = function.applyAsDouble(solution);

			if(value<minValue){
				minValue = value;
				divergenceCount = 0;
			}
			else{
				divergenceCount++;
			}
			
			//Calculate function gradient
			gradient = function.getGradientValueAt(solution);
			hess = function.getHessMatrix(solution);
			
			double[][] invertedHess = Matrix.invers(new Matrix(hess.length, hess[0].length, hess)).getMatrixData();
			double[] invertedHessTimesGrad = VectorUtils.multiply(invertedHess,gradient);
			
			double norm = VectorUtils.secondNorm(invertedHessTimesGrad);
			for(int i=0; i<gradient.length; i++){
				v[i] = invertedHessTimesGrad[i]/norm;
			}
			
			//Determine move
			if(searchForOptimalMove){
				lambda = findOptimalLambda(function, solution, v,lambda,lambdaPrecision);
			}
			
			move = VectorUtils.multiplyByScalar(v, lambda, false);

			//Perform move
			solution = VectorUtils.add(solution, move,false);
						
		}while(!isPreciseEnough(move,precision) && divergenceCount<500);
		
		if(divergenceCount>=500)
			throw new RuntimeException("Can not find solution. Algorithm is diverging.");
		
		return solution;
	}
	
	private double findOptimalLambda(ToDoubleFunction<double[]> function, double[] currentSolution, double[] v, double startLambda, double precision) {

		//Make transform function which only depends on lambda
		ToDoubleFunction<double[]> lambdaTrnasformFunction = new ToDoubleFunction<double[]>() {
			
			@Override
			public double applyAsDouble(double[] value) {
				double lambda = value[0];
				
				double[] move = VectorUtils.multiplyByScalar(v, lambda, false);
				double[] point = VectorUtils.add(currentSolution, move,false);
				
				return function.applyAsDouble(point);
			}
						
		};
		
		
		//Perform search for best lambda
		GoldenCut  goldenCut = new GoldenCut();
		double bestLambda = goldenCut.optimise(lambdaTrnasformFunction, startLambda, precision)[0];
		return bestLambda;
	}

	private boolean isPreciseEnough(double[] move, double precision) {
		return Arrays.stream(move).allMatch(v->Math.abs(v)<precision);
	}

}
