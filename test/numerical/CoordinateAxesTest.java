package numerical;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import function.FunctionProvider;
import numerical.CoordinateAxesAlgorithm;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.solution.DoubleArraySolution;
import optimization.stopper.FunctionValueStopper;

public class CoordinateAxesTest {

	@Test
	public void testF1() {		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(1);
		DoubleArraySolution x0 = new DoubleArraySolution(new double[]{-1.9,2});
		double e = 1e-6;
		
		CoordinateAxesAlgorithm optimisationStrategy = new CoordinateAxesAlgorithm(f,
				new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>()),
				new FunctionValueStopper<>(1e-8), x0, e);
		double[] solution = optimisationStrategy.run().values;
		
		double[] expected = new double[]{1,1};
		printAndTest(expected, solution,"F1");
	}

	@Test
	public void testF2() {
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(2);
		DoubleArraySolution x0 = new DoubleArraySolution(new double[]{0.1,0.3});
		double e = 1e-6;
		
		CoordinateAxesAlgorithm optimisationStrategy = new CoordinateAxesAlgorithm(f,
				new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>()),
				new FunctionValueStopper<>(1e-6), x0, e);
		double[] solution = optimisationStrategy.run().values;
		
		double[] expected = new double[]{4,2};
		printAndTest(expected, solution,"F2");
	}

	@Test
	public void testF3() {
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(3);
		DoubleArraySolution x0 = new DoubleArraySolution(new double[]{0,0,0,0,0});
		double e = 1e-6;
		
		CoordinateAxesAlgorithm optimisationStrategy = new CoordinateAxesAlgorithm(f,
				new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>()),
				new FunctionValueStopper<>(1e-6), x0, e);
		double[] solution = optimisationStrategy.run().values;
		
		double[] expected = new double[]{1,2,3,4,5};
		printAndTest(expected, solution,"F3");
	}

	
	private void printAndTest(double[] expected, double[] solution1,String testId) {
		System.out.println("Solution "+testId+" :" + Arrays.toString(solution1));
		assertEquals(expected[0], solution1[0], 1e-3);
		assertEquals(expected[1], solution1[1], 1e-3);
	}

}
