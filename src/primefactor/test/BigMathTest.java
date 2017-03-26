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
		final BigInteger[] composites = {
				new BigInteger("1"),
				new BigInteger("2"),
				new BigInteger("3"),
				new BigInteger("4"),
				new BigInteger("44"),
				new BigInteger("85"),
				new BigInteger("264"),
				new BigInteger("1331"),
				new BigInteger("18161"),
				new BigInteger("26247"),
				new BigInteger("256159"),
				new BigInteger("658073"),
		};
		List<BigInteger> factors;

		for (int i = 0; i < composites.length; i++) {
			factors = BigMath.primeFactorsOf(composites[i]);

			Assert.assertEquals(
					BigMath.multiply(factors),
					composites[i]
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
