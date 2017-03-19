package primefactor.test;

import org.junit.Assert;
import org.junit.Test;
import primefactor.util.BigMath;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by n0ne on 13/03/17.
 */
public class BigMathTest {

	@Test
	public void testPrimeFactorsOf () {
		final BigInteger[] products = {
				new BigInteger("2"),
				new BigInteger("44"),
				new BigInteger("85"),
				new BigInteger("264"),
				new BigInteger("1331"),
				new BigInteger("18161"),
				new BigInteger("26247"),
				new BigInteger("256159"),
				new BigInteger("658073"),
		};
		final BigInteger[][] expectedResults = {
				{new BigInteger("2")},
				{new BigInteger("2"), new BigInteger("2"), new BigInteger("11")},
				{new BigInteger("5"), new BigInteger("17")},
				{new BigInteger("2"), new BigInteger("2"), new BigInteger("2"), new BigInteger("3"), new BigInteger("11")},
				{new BigInteger("11"), new BigInteger("11"), new BigInteger("11")},
				{new BigInteger("11"), new BigInteger("13"), new BigInteger("127")},
				{new BigInteger("3"), new BigInteger("13"), new BigInteger("673")},
				{new BigInteger("127"), new BigInteger("2017")},
				{new BigInteger("13"), new BigInteger("223"), new BigInteger("227")},
		};
		final int min = Math.min(products.length, expectedResults.length);
		List<BigInteger> actualResult;

		for (int i = 0; i < min; i++) {
			actualResult = BigMath.primeFactorsOf(
					products[i],
					new BigInteger("2"),
					BigMath.sqrt(products[i]).add(BigInteger.ONE)
			);

			Assert.assertEquals(
					expectedResults[i],
					actualResult.toArray(new BigInteger[expectedResults[i].length])
			);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPrimeFactorsOfLessThanTwo () {
		BigMath.primeFactorsOf(new BigInteger("1"), new BigInteger("4"), new BigInteger("10"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPrimeFactorsOfLowHighInvalid () {
		BigMath.primeFactorsOf(new BigInteger("4040"), new BigInteger("11"), new BigInteger("1"));
	}

}
