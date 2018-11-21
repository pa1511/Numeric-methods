package numerical;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import function.FunctionProvider;
import numerical.HookeJeevesAlgorithm;
import optimization.fittnesEvaluator.FunctionValueFitnessEvaluator;
import optimization.fittnesEvaluator.ThroughOneFitnessEvaluator;
import optimization.solution.DoubleArraySolution;
import optimization.startSolutionGenerator.ProvidingStartSolutionGenerator;
import optimization.stopper.GenerationNumberEvolutionStopper;


public class HookeJeevesTest {

	@Test
	public void testF1() {
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(1);
		DoubleArraySolution x0 = new DoubleArraySolution(new double[]{-1.9,2});
		double[] solution = findSolution(f, x0);
		
		double[] expected = new double[]{1,1};
		printAndTest(expected, solution);
	}

	@Test
	public void testF2() {
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(2);
		DoubleArraySolution x0 = new DoubleArraySolution(new double[]{0.1,0.3});
		double[] solution = findSolution(f, x0);
		
		double[] expected = new double[]{4,2};
		printAndTest(expected, solution);
	}
	
	@Test
	public void testF3() {
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(3);
		DoubleArraySolution x0 = new DoubleArraySolution(new double[]{0,0,0,0,0});
		double[] solution = findSolution(f, x0);
		
		double[] expected = new double[]{1,2,3,4,5};
		printAndTest(expected, solution);
	}
	
	private double getReductionFactor() {
		return 0.5;
	}

	private double[] findSolution(ToDoubleFunction<double[]> f, DoubleArraySolution x0) {
		double[] xD = new double[x0.values.length];
		Arrays.fill(xD, 0.5);
		double reductionFactor = getReductionFactor();
		
		ProvidingStartSolutionGenerator<DoubleArraySolution> solutionGenerator = new ProvidingStartSolutionGenerator<>();
		solutionGenerator.accept(x0);
		
		HookeJeevesAlgorithm algorithm = new HookeJeevesAlgorithm(f, 
				new ThroughOneFitnessEvaluator<>(new FunctionValueFitnessEvaluator<>()),
				new GenerationNumberEvolutionStopper<>(Integer.MAX_VALUE)
				,solutionGenerator, xD, reductionFactor, 1e-6);
		return algorithm.run().values;
	}

	private void printAndTest(double[] expected, double[] solution1) {
		System.out.println("Solution :" + Arrays.toString(solution1));
		assertEquals(expected[0], solution1[0], 1e-3);
		assertEquals(expected[1], solution1[1], 1e-3);
	}


}
