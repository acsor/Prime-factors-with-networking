package primefactor.util;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

public class BigMath {

	private static int CONST_PRIME_PROBABILITY = 10;

	/**
	 * Given a BigInteger input n, where n >= 0, returns the largest BigInteger r such that r*r <= n.<br>
	 * For n < 0, returns 0.<br>
	 * details: http://faruk.akgul.org/blog/javas-missing-algorithm-biginteger-sqrt
	 *
	 * @param n BigInteger input.
	 * @return for n >= 0: largest BigInteger r such that r*r <= n; for n <  0: BigInteger 0.
	 */
	public static BigInteger sqrt (BigInteger n) {
		BigInteger a = BigInteger.ONE;
		BigInteger b = new BigInteger(n.shiftRight(5).add(new BigInteger("8")).toString());
		while (b.compareTo(a) >= 0) {
			BigInteger mid = new BigInteger(a.add(b).shiftRight(1).toString());
			if (mid.multiply(mid).compareTo(n) > 0)
				b = mid.subtract(BigInteger.ONE);
			else
				a = mid.add(BigInteger.ONE);
		}
		return a.subtract(BigInteger.ONE);
	}

	public static List<BigInteger> primeFactorsOf (BigInteger n, BigInteger low, BigInteger high) {
		final List<BigInteger> factors = new LinkedList<>();

		if (n.compareTo(new BigInteger("2")) == -1) {
			throw new IllegalArgumentException(String.format("Parameter n has value %s < 2", n));
		}
		if (low.compareTo(BigInteger.ONE) == -1 || high.compareTo(low) == -1) {
			throw new IllegalArgumentException(
					String.format(
							"low and high parameters are not such that 1 <= low (%s) <= high (%s)",
							low, high
					)
			);
		}

		for (BigInteger divisor = low; divisor.compareTo(high) < 1; divisor = divisor.add(BigInteger.ONE)) {
			if (divisor.isProbablePrime(CONST_PRIME_PROBABILITY)) {
				while (n.remainder(divisor).compareTo(BigInteger.ZERO) == 0) {
					n = n.divide(divisor);
					factors.add(divisor);
				}
			}
		}

		if (n.compareTo(BigInteger.ONE) == 1) {
			factors.add(n);
		}

		return factors;
	}

}
