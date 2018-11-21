package numerical;

import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnull;

public class UnimodalFinder {

	private UnimodalFinder() {
	}

	/**
	 * Finds the unimodal interval of the given function (under presumption that
	 * it is a unimodal function) using shift value 1
	 */
	public static double[] findUnimodalInterval(@Nonnull ToDoubleFunction<double[]> function, double xS) {
		return findUnimodalInterval(function, xS, 1.0);
	}

	/**
	 * Finds the unimodal interval of the given function (under presumption that
	 * it is a unimodal function) using the given shift value
	 */
	public static double[] findUnimodalInterval(@Nonnull ToDoubleFunction<double[]> function, double xS, double shift) {		
		return findUnimodalInterval(function, new double[]{xS}, 0, shift);
	}

	public static double[] findUnimodalInterval(@Nonnull ToDoubleFunction<double[]> function, double[] xS, int i) {
		return findUnimodalInterval(function, xS, i, 1.0);
	}
	
	public static double[] findUnimodalInterval(@Nonnull ToDoubleFunction<double[]> function, double[] xS, int i, double shift) {

		double[] interval = new double[2];
		findUnimodalInterval(function, xS, i, shift, interval);
		return interval;
	}

	public static void findUnimodalInterval(@Nonnull ToDoubleFunction<double[]> function, double[] xS, int i, double shift, double[] interval) {
		double originalValue = xS[i];
		
		double l = originalValue - shift, r = originalValue + shift;
		double m = originalValue;
		double fl, fm, fr;
		int step = 1;

		fm = function.applyAsDouble(getPoint(xS, m, i));
		fl = function.applyAsDouble(getPoint(xS, l, i));
		fr = function.applyAsDouble(getPoint(xS, r, i));
		
		if (fm < fr && fm < fl){
//			interval[0] = l;
//			interval[1] = r;
//			xS[i] = originalValue;
		}
		else if (fm > fr) {
			do {
				
				l = m;
				m = r;
				fm = fr;
				r = originalValue + shift * (step *= 2);
				fr = function.applyAsDouble(getPoint(xS, r, i));


			} while (fm > fr);
		} else {
			do {
								
				r = m;
				m = l;
				fm = fl;
				l = originalValue - shift * (step *= 2);
				fl = function.applyAsDouble(getPoint(xS, l, i));

			} while (fm > fl);
		}
		
		interval[0] = l;
		interval[1] = r;
		xS[i] = originalValue;
	}
	
	public static double[] getPoint(double[] xT, double a, int i) {
		xT[i]  = a;
		return xT;
	}
	
}
