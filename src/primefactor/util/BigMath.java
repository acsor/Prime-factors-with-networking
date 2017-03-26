package primefactor.util;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class BigMath {

	private static int CONST_PRIME_CERTAINTY = 10;
	private static BigInteger CONST_MIN_LOW = BigInteger.valueOf(2);

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

	/**
	 *
	 * @param n number to factorize.
	 * @return a list of <i>all</i> the prime factors of n calculated from 2 to sqrt(n).
	 */
	public static List<BigInteger> primeFactorsOf (BigInteger n) {
		final List<BigInteger> result;

		if (n.compareTo(BigInteger.ZERO) == 1 && n.compareTo(BigInteger.valueOf(4)) == -1) { //If is 0 < n <= 3
			result = new LinkedList<>();
			result.add(n);
		} else {
			result = primeFactorsOf(n, CONST_MIN_LOW, sqrt(n));

			for (BigInteger factor: result) {
				n = n.divide(factor);
			}

			if (n.isProbablePrime(CONST_PRIME_CERTAINTY) && n.compareTo(BigInteger.ONE) == 1) {
				result.add(n);
			}
		}

		return result;
	}

	public static List<BigInteger> primeFactorsOf (BigInteger n, BigInteger low, BigInteger high) {
		return primeFactorsOf(n, low, high, CONST_PRIME_CERTAINTY);
	}

	/**
	 * Computes all the prime factors f of n such that low <= f <= high, for every f.<br>
	 *
	 * <b>Note:</b> users should <i>not</i> expect this method to return all the prime factors of n when
	 * low = 2 and high = sqrt(n), for example. Indeed (see multithreading_extension, bug fixes #1) there is always the possibility that
	 * one such factor (and no more than one) is greater than sqrt(n). (See also the problem assignment PDF.)
	 * For computing all the prime factors of a number n, see {@link BigMath#primeFactorsOf(BigInteger)} instead.
	 * @param n BigInteger to find prime factors for.
	 * @param low minimum value a factor can take.
	 * @param high maximum value a factor can take.
	 * @param primeCertainty parameter to pass to {@link BigInteger#isProbablePrime(int)}.
	 * @return a list of prime factors of n such that if f belongs to this list, then low <= f <= high.
	 */
	public static List<BigInteger> primeFactorsOf (BigInteger n, BigInteger low, BigInteger high, int primeCertainty) {
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
			if (divisor.isProbablePrime(primeCertainty)) {
				while (n.remainder(divisor).compareTo(BigInteger.ZERO) == 0) {
					n = n.divide(divisor);
					factors.add(divisor);
				}
			}
		}

		return factors;
	}

	public static BigInteger multiply (Collection<BigInteger> integers) {
		BigInteger result = BigInteger.ONE;
		final Iterator<BigInteger> it = integers.iterator();

		if (it.hasNext()) {
			result = it.next();

			while (it.hasNext()) {
				result = result.multiply(it.next());
			}
		}

		return result;
	}

}
