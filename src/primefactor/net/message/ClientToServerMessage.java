package primefactor.net.message;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by n0ne on 14/03/17.
 */
public class ClientToServerMessage extends Message {

	public static final BigInteger CONST_MIN_N = BigInteger.valueOf(2);
	public static final BigInteger CONST_MIN_LOW_BOUND = BigInteger.valueOf(2);

	public static final String CONST_PROT_FACTOR = "factor";

	private BigInteger n;
	private BigInteger low, high;

	public ClientToServerMessage (BigInteger n, BigInteger low, BigInteger high) {
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

	public List<ClientToServerMessage> partition (int slots) {
		final LinkedList<ClientToServerMessage> result = new LinkedList<>();
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
						new ClientToServerMessage(this.n, low, high)
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
