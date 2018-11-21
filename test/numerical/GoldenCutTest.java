package numerical;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.function.ToDoubleFunction;

import org.junit.Test;

import function.FunctionProvider;
import numerical.GoldenCut;

public class GoldenCutTest {

	@Test
	public void test() {
		
		GoldenCut goldenCut = new GoldenCut();
		
		ToDoubleFunction<double[]> f = FunctionProvider.getFunction(0);
		double[] p = goldenCut.optimise(f, 5);
		double solution = (p[0]+p[1])/2;
		
		System.out.println(Arrays.toString(p));
		System.out.println("Solution1: " + solution);
		assertEquals(0, solution,1e-3);
	}

}
