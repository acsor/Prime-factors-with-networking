package primefactor.test;

import org.junit.Assert;
import org.junit.Test;
import primefactor.net.message.ClientToServerFactorMessage;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by n0ne on 19/03/17.
 */
public class MessageTest {

	@Test
	public void testPartition () {
		final BigInteger n = new BigInteger("2424234");
		final ClientToServerFactorMessage[] messages = {
				new ClientToServerFactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(5)),
				new ClientToServerFactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(43)),
				new ClientToServerFactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(102)),
				new ClientToServerFactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(92)),
				new ClientToServerFactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(127)),
		};
		final int[] slots = {
				3,
				4,
				5,
				3,
				6
		};
		final int cycles = Math.min(messages.length, slots.length);
		List<ClientToServerFactorMessage> partition;

		for (int i = 0; i < cycles; i++) {
			partition = messages[i].partition(slots[i]);

			System.out.println(partition);

			//I don't think these assertions can completely ensure the validity of the code, but they are better than nothing.
			Assert.assertEquals(slots[i], partition.size());
			Assert.assertEquals(messages[i].getHighBound(), partition.get(partition.size() - 1).getHighBound());
		}
	}

}
