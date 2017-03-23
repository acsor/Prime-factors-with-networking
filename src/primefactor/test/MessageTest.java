package primefactor.test;

import org.junit.Assert;
import org.junit.Test;
import primefactor.net.message.ClientToServerMessage.FactorMessage;

import java.math.BigInteger;
import java.util.List;

/**
 * Created by n0ne on 19/03/17.
 */
public class MessageTest {

	private final FactorMessage[] messages;

	public MessageTest () {
		final BigInteger n = new BigInteger("2424234");
		messages = new FactorMessage[] {
				new FactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(5)),
				new FactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(43)),
				new FactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(102)),
				new FactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(92)),
				new FactorMessage(n, BigInteger.valueOf(2), BigInteger.valueOf(127)),
		};
	}

	/**
	 * This methods tests FactorMessage.partition(int slots).
	 */
	@Test
	public void testPartition () {
		final int[] slots = {
				3,
				4,
				5,
				3,
				6
		};
		final int cycles = Math.min(messages.length, slots.length);
		List<FactorMessage> partition;

		for (int i = 0; i < cycles; i++) {
			partition = messages[i].partition(slots[i]);

			//System.out.println(partition);

			//I don't think these assertions can completely ensure the validity of the code, but they are better than nothing.
			Assert.assertEquals(slots[i], partition.size());
			Assert.assertEquals(messages[i].getHighBound(), partition.get(partition.size() - 1).getHighBound());
		}
	}


	/**
	 * This methods tests FactorMessage.partition().
	 */
	@Test
	public void testPartitionDefault () {
		List<FactorMessage> partition;

		for (FactorMessage message: messages) {
			partition = message.partition();

			if (message.getHighBound().subtract(message.getLowBound()).compareTo(BigInteger.valueOf(FactorMessage.CONST_DEFAULT_PARTITIONS)) >= 1) {
				Assert.assertEquals(FactorMessage.CONST_DEFAULT_PARTITIONS, partition.size());
			} else {
				Assert.assertEquals(1, partition.size());
			}
			Assert.assertEquals(message.getHighBound(), partition.get(partition.size() - 1).getHighBound());
		}
	}

}
