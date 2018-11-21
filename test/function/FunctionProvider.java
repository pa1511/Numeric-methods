package function;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

import function.IGFunction;
import function.common.XPowerN;
import function.common.benchmark.RosenbrockBananaFunction;
import function.common.benchmark.SchaffersFunction6;

public class FunctionProvider {

	public static List<ToDoubleFunction<double[]>> functions = new ArrayList<>();
	static{
		//0
		functions.add(new XPowerN(2));
		//1
		functions.add(new RosenbrockBananaFunction());
		//2
		functions.add(new ToDoubleFunction<double[]>() {
			
			@Override
			public double applyAsDouble(double[] X) {
				double x1 = X[0];
				double x2 = X[1];
				
				return (x1-4)*(x1-4) + 4*(x2-2)*(x2-2);
			}
		});
		//3
		functions.add(new ToDoubleFunction<double[]>() {
						
			@Override
			public double applyAsDouble(double[] X) {
				double sum = 0;
				for(int i=0; i<X.length;i++){
					sum+=(X[i]-i-1)*(X[i]-i-1);
				}
				return sum;
			}
		});
		//4
		functions.add(new ToDoubleFunction<double[]>() {
			
			@Override
			public double applyAsDouble(double[] X) {
				double x1 = X[0];
				double x2 = X[1];
				
				return Math.abs(x1*x1-x2*x2)+Math.sqrt(x1*x1+x2*x2);
			}
		});
		//5
		functions.add(new SchaffersFunction6(10));
		//6
		functions.add(new IGFunction() {
			
			@Override
			public double applyAsDouble(double[] X) {
				return (X[0]-3)*(X[0]-3);
			}
						
			@Override
			public void getGradientValueAt(double[] X, double[] gradient) {
				gradient[0] = 2*(X[0]-3);
			}
			
			@Override
			public int getVariableCount() {
				return 1;
			}
		} );

	}
	
	public static ToDoubleFunction<double[]> getFunction(int functionIndex) {
		if(functionIndex>functions.size())
			throw new IllegalArgumentException("Unknown function requested. Function index: " + functionIndex);

		return functions.get(functionIndex);
	}
	
	public static int numberOfFunctions(){
		return functions.size();
	}

}
