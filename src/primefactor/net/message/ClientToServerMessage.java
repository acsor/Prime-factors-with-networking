package primefactor.net.message;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by n0ne on 14/03/17.
 */
public abstract class ClientToServerMessage extends Message {

	public static class FactorMessage extends ClientToServerMessage {

		public static final BigInteger CONST_MIN_N = BigInteger.valueOf(2);
		public static final BigInteger CONST_MIN_LOW_BOUND = BigInteger.valueOf(2);
		public static final int CONST_DEFAULT_PARTITIONS = 5;

		public static final String CONST_PROT_FACTOR = "factor";

		private BigInteger n;
		private BigInteger low, high;

		public FactorMessage (BigInteger n, BigInteger low, BigInteger high) {
			this.n = n;
			this.low = low;
			this.high = high;

			if (!isValid()) {
				throw new IllegalArgumentException(
						String.format(
								"some of n (%s), low (%s) or high (%s) wasn't properly set",
								n, low, high
						)
				);
			}
		}

		private boolean isValid () {
			return this.n.compareTo(CONST_MIN_N) >= 0 &&
					this.low.compareTo(CONST_MIN_LOW_BOUND) >= 0 &&
					this.high.compareTo(this.n) < 0 &&
					this.low.compareTo(this.high) <= 0
					;
		}

		public BigInteger getN () {
			return n;
		}

		public BigInteger getLowBound () {
			return low;
		}

		public BigInteger getHighBound () {
			return high;
		}

		/**
		 * A variant of partition(int slots) automating the choice of the slots parameter.
		 * @return return value of partition(int slots).
		 */
		public List<FactorMessage> partition () {
			if (high.subtract(low).compareTo(BigInteger.valueOf(CONST_DEFAULT_PARTITIONS)) >= 1) {
				return partition(CONST_DEFAULT_PARTITIONS);
			}
			return partition(1);
		}

		/**
		 * Utility method to split a FactorMessage instance into multiple parts. Each of these is supposed to be passed
		 * to a PrimeFactorServer for parallel processing.
		 * @param slots number of FactorMessage partitions to divide the current instance in.
		 * @return a list of FactorMessage instances each comprising a range of the number n to be factored by a server
		 * in a thread of its own.
		 */
		public List<FactorMessage> partition (int slots) {
			final LinkedList<FactorMessage> result = new LinkedList<>();
			final BigInteger width, remainder;
			BigInteger low, high;

			if (slots <= 0) {
				throw new IllegalArgumentException("slots parameter must be greater than 0");
			} else {
				low = this.low;
				width = this.high.subtract(this.low).divide(BigInteger.valueOf(slots));
				high = low.add(width);
				remainder = this.high.subtract(this.low).remainder(BigInteger.valueOf(slots));

				if (width.compareTo(BigInteger.ONE) == -1) {
					throw new IllegalArgumentException(
							String.format(
									"parameter slots (%d) too big for partitioning range (%s) = high (%s) - low (%s)",
									slots, this.high.subtract(this.low), this.high, this.low
							)
					);
				}

				for (int i = 0; i < slots; i++) {
					if (i + 1 == slots) {
						high = high.add(remainder);
					}

					result.add(
							new FactorMessage(this.n, low, high)
					);

					low = high.add(BigInteger.ONE);
					high = high.add(width);
				}
			}

			return result;
		}

		@Override
		public String toString () {
			final StringBuilder b = new StringBuilder();

			b.append(CONST_PROT_FACTOR)
					.append(CONST_PROT_SPACE)
					.append(n)
					.append(CONST_PROT_SPACE)
					.append(low)
					.append(CONST_PROT_SPACE)
					.append(high)
					.append(CONST_PROT_NEWLINE)
			;

			return b.toString();
		}

	}

	public static class SpawnMessage extends ClientToServerMessage {

		private int serversNumber;

		public SpawnMessage (int serversNumber) {
			this.serversNumber = serversNumber;
		}

		public int getServersNumber () {
			return serversNumber;
		}

	}

}
