package numerical;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;


public class NelderMeadSimplex {
	
	public static final double ALFA = 1;
	public static final double BETA = 0.5;
	public static final double GAMA = 2;
	public static final double EPSILON = 1e-8;
	public static final double T = 1;
	
	public double[] optimise(ToDoubleFunction<double[]> function, double[] x0, double alfa, double beta, double gama, double epsilon,
			double t) {

		double[][] simplexPoints = calculateSimplexPoints(x0, t);
		double[] pointFunctionValues = calculateFunctionValues(simplexPoints, function);

		double[] xC;
		double fxC;
		int l, h;

		do {

			h = getHighestIndex(pointFunctionValues);
			l = getLowestIndex(pointFunctionValues);

			xC = calculateCentroid(simplexPoints, h);
			fxC = function.applyAsDouble(xC);

			double[] xR = reflection(xC, simplexPoints[h], alfa);

			double fxR = function.applyAsDouble(xR);
			double fxH = pointFunctionValues[h];
			double fxL = pointFunctionValues[l];
			
			if (fxR < fxL) {
				double[] xE = expansion(xC, xR, gama);
				double fxE = function.applyAsDouble(xE);
				if (fxE < fxL) {
					simplexPoints[h] = xE;
					pointFunctionValues[h] = fxE;
				} else {
					simplexPoints[h] = xR;
					pointFunctionValues[h] = fxR;
				}
			} else if (worseThenOthers(fxR, pointFunctionValues, h)) {				
				if (fxR < fxH) {
					simplexPoints[h] = xR;
					pointFunctionValues[h] = fxR;
					fxH = pointFunctionValues[h];
				}
				
				double[] xK = contraction(xC, simplexPoints[h], beta);
				double fxK = function.applyAsDouble(xK);

				if (fxK < fxH) {
					simplexPoints[h] = xK;
					pointFunctionValues[h] = fxK;
				} else {
					moveAllPointsToLower(simplexPoints, l);
					pointFunctionValues = calculateFunctionValues(simplexPoints, function);
				}
			} else {
				simplexPoints[h] = xR;
				pointFunctionValues[h] = fxR;
			}

			xC = calculateCentroid(simplexPoints, h);
			fxC = function.applyAsDouble(xC);
			
			
		} while (!isPreciseEnought(pointFunctionValues, fxC, epsilon));

		return calculateCentroid(simplexPoints, simplexPoints.length);
	}

	private void moveAllPointsToLower(double[][] simplexPoints, int l) {

		for (int i = 0; i < simplexPoints.length; i++) {
			if (i == l)
				continue;

			for (int j = 0; j < simplexPoints[i].length; j++) {
				simplexPoints[i][j] = (simplexPoints[i][j] + simplexPoints[l][j]) / 2;
			}
		}

	}

	private boolean isPreciseEnought(double[] pointFunctionValues, double fxC, double epsilon) {
		double sum = 0;
		for (double functionValue : pointFunctionValues) {
			sum += Math.pow(functionValue - fxC, 2);
		}

		double sqr = Math.sqrt(sum / pointFunctionValues.length);
		
		return sqr <= epsilon;
	}

	private boolean worseThenOthers(double fxR, double[] pointFunctionValues, int h) {

		for (int i = 0; i < pointFunctionValues.length; i++) {
			if (i == h)
				continue;
			if (fxR <= pointFunctionValues[i])
				return false;
		}

		return true;
	}

	private double[] contraction(double[] xC, double[] xH, double beta) {
		return add(Arrays.stream(xC).map(x -> (1 - beta) * x).toArray(),
				Arrays.stream(xH).map(x -> beta * x).toArray());
	}

	private double[] expansion(double[] xC, double[] xR, double gama) {
		return add(Arrays.stream(xC).map(x -> (1 - gama) * x).toArray(),
				Arrays.stream(xR).map(x -> gama * x).toArray());
	}

	private double[] reflection(double[] xC, double[] xH, double alfa) {
		return add(Arrays.stream(xC).map(x -> (1 + alfa) * x).toArray(),
				Arrays.stream(xH).map(x -> alfa * -1 * x).toArray());
	}

	private double[] calculateCentroid(double[][] simplexPoints, int h) {
		double[] sum = new double[simplexPoints[0].length];

		for (int i = 0; i < simplexPoints.length; i++) {
			if (i == h){
				continue;
			}
			sum = add(sum, simplexPoints[i]);
		}
		int divideFactor = (h>=0 && h<simplexPoints.length) ? simplexPoints.length-1 : simplexPoints.length;
		return Arrays.stream(sum).map(x -> x / divideFactor).toArray();
	}

	public double[] add(double[] v1, double[] v2) {
		double[] vr = new double[v1.length];
		for (int i = 0; i < v1.length; i++) {
			vr[i] = v1[i] + v2[i];
		}
		return vr;
	}

	private double[] calculateFunctionValues(double[][] simplexPoints, ToDoubleFunction<double[]> function) {
		return Arrays.stream(simplexPoints).mapToDouble(point -> function.applyAsDouble(point)).toArray();
	}

	private int getHighestIndex(double[] pointFunctionValues) {
		double max = Arrays.stream(pointFunctionValues).max().getAsDouble();
		for (int i = 0; i < pointFunctionValues.length; i++) {
			if (pointFunctionValues[i] == max)
				return i;
		}
		return -1;
	}

	private int getLowestIndex(double[] pointFunctionValues) {
		double min = Arrays.stream(pointFunctionValues).min().getAsDouble();
		for (int i = 0; i < pointFunctionValues.length; i++) {
			if (pointFunctionValues[i] == min)
				return i;
		}
		return -1;
	}

	private double[][] calculateSimplexPoints(double[] x0, double t) {

		int n = x0.length;

		double[][] points = new double[n + 1][n];
		points[0] = x0;

		for (int i = 1; i < points.length; i++) {
			points[i] = Arrays.copyOf(x0, x0.length);
			points[i][i - 1] = x0[i - 1] + t;
		}

		return points;
	}
}
