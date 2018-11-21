package numerical;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import function.FunctionProvider;
import numerical.NelderMeadSimplex;

public class NelderMeadSimplexTest {

	private static final double alfa = 1;
	private static final double beta = 0.5;
	private static final double gama = 2;
	private static final double epsilon = 1e-8;
	private static final double t = 1;
	
	@Test
	public void testF1() {
		NelderMeadSimplex optimisationStrategy = new NelderMeadSimplex();
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(1);
		double[] x0 = new double[]{-1.9,2};
		double[] solution = optimisationStrategy.optimise(f, x0, alfa, beta, gama, epsilon, t);
		
		double[] expected = new double[]{1,1};
		printAndTest(expected, solution);
	}

	@Test
	public void testF2() {
		NelderMeadSimplex optimisationStrategy = new NelderMeadSimplex();
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(2);
		double[] x0 = new double[]{0.1,0.3};
		double[] solution = optimisationStrategy.optimise(f, x0, alfa, beta, gama, epsilon, t);
		
		double[] expected = new double[]{4,2};
		printAndTest(expected, solution);
	}

	@Test
	public void testF3() {
		NelderMeadSimplex optimisationStrategy = new NelderMeadSimplex();
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(3);
		double[] x0 = new double[]{0,0,0,0,0};
		double[] solution = optimisationStrategy.optimise(f, x0, alfa, beta, gama, epsilon, t);
		
		double[] expected = new double[]{1,2,3,4,5};
		printAndTest(expected, solution);
	}

	@Test
	public void testF4() {
		NelderMeadSimplex optimisationStrategy = new NelderMeadSimplex();
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(4);
		double[] x0 = new double[]{5.1,1.1};
		double[] solution = optimisationStrategy.optimise(f, x0, alfa, beta, gama, epsilon, t);
		
		double[] expected = new double[]{0,0};
		printAndTest(expected, solution);
	}

//	@Test
//	public void testF5() {
//		NelderMeadSimplexOptimisationStrategy optimisationStrategy = new NelderMeadSimplexOptimisationStrategy();
//		
//		IFunction f = FunctionProvider.getFunction(5);
//		double[] x0 = new double[]{1,2};
//		double[] solution = optimisationStrategy.optimise(f, x0, alfa, beta, gama, epsilon, t,progressLogger);
//		
//		double[] expected = new double[]{0,0};
//		printAndTest(expected, solution);
//	}

	
	private void printAndTest(double[] expected, double[] solution1) {
		System.out.println("Solution :" + Arrays.toString(solution1));
		System.out.println();
		assertEquals(expected[0], solution1[0], 1e-3);
		assertEquals(expected[1], solution1[1], 1e-3);
	}


}
