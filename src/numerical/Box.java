package numerical;

import java.util.Collection;
import java.util.Random;
import java.util.function.ToDoubleFunction;

import util.VectorUtils;
import utilities.random.RNGProvider;

public class Box {

	public double[] run(ToDoubleFunction<double[]> function, double[] X0, double[] Xd, double[] Xg, Collection<ToDoubleFunction<double[]>> g,
			double precision, double alfa) {
		
		Random random = RNGProvider.getRandom();
		
		for(int i=0; i<X0.length;i++){
			if(!(Xd[i]<X0[i] && X0[i]<Xg[i]))
				throw new RuntimeException("");
		}
		if(isAnyLessThenZero(g, X0))
			throw new RuntimeException("");
		
		double[] Xc = new double[]{X0[0],X0[1]};
		
		// generiranje skupa 2n tocaka
		double[][] X = new double[X0.length * 2][Xc.length];
		X[0] = X0;
		for (int t = 1; t < 2 * X0.length; t++) {
			for (int i = 0; i < X0.length; i++) {
				double R = random.nextDouble();
				X[t][i] = Xd[i] + R * (Xg[i] - Xd[i]);
			}
			// nova tocka je unutar eksplicitnih ogr.
			int finalT = t;
			while (g.stream().anyMatch(gi -> gi.applyAsDouble(X[finalT]) < 0))
			// postoji gi(X) za koji  gi(X[t])<0)
				X[t] = VectorUtils.multiplyByScalar(VectorUtils.add(X[t], Xc,false), 0.5, false);
			// nisu zadovoljena implicitna ogr.
			// pomakni prema centroidu prihvacenih tocaka

			// izracunaj novi Xc (sa novom prihvacenom tockom);
			for (int i = 0; i < Xc.length; i++) {
				Xc[i] = 0.0;
			}
			for (int j = 0; j <= t; j++) {
				Xc = VectorUtils.add(Xc, X[j],false);
			}
			Xc = VectorUtils.multiplyByScalar(Xc, 1.0 / (t + 1.0), false);
		}

		double[] functionValues = new double[X.length];
		for (int i = 0; i < X.length; i++) {
			functionValues[i] = function.applyAsDouble(X[i]);
		}
		do {
			// odredi indekse h, h2 : F(X[h]) = max, F(X[h2]) = drugi najlosiji;
			int h = 0, h2 = 0;
			double max = Double.NEGATIVE_INFINITY;
			double max2 = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < X.length; i++) {
				if (functionValues[i] > max) {
					max2 = max;
					max = functionValues[i];
					h2 = h;
					h = i;
				}
				if(functionValues[i]>max2 && functionValues[i]!=max){
					max2 = functionValues[i];
					h2 = i;
				}
			}

			// izracunaj Xc (bez X[h]);
			for (int i = 0; i < Xc.length; i++) {
				Xc[i] = 0.0;
			}
			for (int i = 0; i < X.length; i++) {
				if (i == h)
					continue;
				Xc = VectorUtils.add(Xc, X[i],false);
			}
			Xc = VectorUtils.multiplyByScalar(Xc, 1.0 / (X.length - 1), false);
			//

			// refleksija
			double[] Xr = VectorUtils.subtract(VectorUtils.multiplyByScalar(Xc, 1 + alfa, false),
					VectorUtils.multiplyByScalar(X[h], alfa, false),false);
			
			if(areAlmostEqual(Xr,Xc,precision*1e-1)){
				X[h] = VectorUtils.add(X[random.nextInt(X.length)], Xc,false);
				continue;
			}
			
			for (int i = 0; i < Xc.length; i++) {
				// pomicemo na granicu ekspl. ogranicenja
				if (Xr[i] < Xd[i])
					Xr[i] = Xd[i];
				else if (Xr[i] > Xg[i])
					Xr[i] = Xg[i];
			}

			// provjeravamo implicitna ogr.
			while (isAnyLessThenZero(g, Xr))
				Xr = VectorUtils.multiplyByScalar(VectorUtils.add(Xr, Xc,false), 0.5, false);

			double XrValue = function.applyAsDouble(Xr);
			if (XrValue > functionValues[h2]) { // ako je to i dalje najlosija
												// tocka
				// jos jednom pomakni prema Xc
				Xr = VectorUtils.multiplyByScalar(VectorUtils.add(Xr, Xc,false), 0.5, false);
				XrValue = function.applyAsDouble(Xr);
			}

			X[h] = Xr;
			functionValues[h] = XrValue;

		} while (!isSmallEnough(X, Xc, precision));

		for (int i = 0; i < Xc.length; i++) {
			Xc[i] = 0.0;
		}
		for (int j = 0; j < X.length; j++) {
			Xc = VectorUtils.add(Xc, X[j],false);
		}
		Xc = VectorUtils.multiplyByScalar(Xc, 1.0 / X.length, false);

		return Xc;
	}

	private boolean areAlmostEqual(double[] xr, double[] xc, double precision) {
		for(int i=0; i<xr.length;i++){
			if(Math.abs(xr[i]-xc[i])>precision)
				return false;
		}
		return true;
	}

	private boolean isAnyLessThenZero(Collection<ToDoubleFunction<double[]>> g, double[] Xr) {
		return g.stream().anyMatch(gi -> gi.applyAsDouble(Xr) < 0);
	}

	public boolean isSmallEnough(double[][] X, double[] Xc, double precision) {
		for (int i = 0; i < X.length; i++) {
			double sum = 0.0;
			for (int j = 0; j < Xc.length; j++) {
				sum += Math.pow(X[i][j] - Xc[j], 2);
			}
			sum = Math.sqrt(sum);
			if (sum > precision)
				return false;
		}

		return true;
	}

}
