package numerical;

import java.util.function.ToDoubleFunction;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class GoldenCut {

	private static final double k = 0.5 * (Math.sqrt(5) - 1);

	public double[] optimise(@Nonnull ToDoubleFunction<double[]> function, double startPoint){
		double[] interval = UnimodalFinder.findUnimodalInterval(function, startPoint);
		optimise(function, interval);
		return interval;
	}
	
	public double[] optimise(@Nonnull ToDoubleFunction<double[]> function, double startPoint, double precision){
		double[] interval = UnimodalFinder.findUnimodalInterval(function, startPoint);
		optimise(function, interval, precision);
		return interval;
	}
	
	public void optimise(@Nonnull ToDoubleFunction<double[]> function, @Nonnull double[] interval){
		optimise(function, interval, 1e-6);
	}
	
	public void optimise(@Nonnull ToDoubleFunction<double[]> function, @Nonnull double[] interval, @Nonnegative double e) {
		optimiseMultyDimension(function, new double[1], interval, 0, e);
	}

	public void optimiseMultyDimension(@Nonnull ToDoubleFunction<double[]> function, double[] x, @Nonnull double[] interval, int i, @Nonnegative double e) {

		double originalValue = x[i];
		
		double a = interval[0];
		double b = interval[1];
		
		if(a>b){
			double swapHelp = a;
			a = b;
			b = swapHelp;
		}

		double c = b - k * (b - a);
		double d = a + k * (b - a);

		double fc = function.applyAsDouble(UnimodalFinder.getPoint(x, c, i));
		double fd = function.applyAsDouble(UnimodalFinder.getPoint(x, d, i));

		while ((b - a) > e) {
			
			if (fc < fd) {
				b = d;
				d = c;
				c = b - k * (b - a);
				
				fd = fc;
				fc = function.applyAsDouble(UnimodalFinder.getPoint(x, c, i));

			} 
			else {
				a = c;
				c = d;
				d = a + k * (b - a);
				
				fc = fd;
				fd = function.applyAsDouble(UnimodalFinder.getPoint(x, d, i));

			}
			
			
		}
		
		x[i] = originalValue;
		interval[0] = a;
		interval[1] = b;
	}

}
