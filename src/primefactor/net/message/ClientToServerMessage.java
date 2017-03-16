package primefactor.net.message;

import java.math.BigInteger;

/**
 * Created by n0ne on 14/03/17.
 */
public class ClientToServerMessage extends Message {

	public static final String CONST_PROT_FACTOR = "factor";

	private BigInteger n;
	private BigInteger low, high;

	public ClientToServerMessage (BigInteger n, BigInteger low, BigInteger high) {
		this.n = n;
		this.low = low;
		this.high = high;
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
