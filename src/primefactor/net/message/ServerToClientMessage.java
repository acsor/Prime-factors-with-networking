package primefactor.net.message;

import java.math.BigInteger;
import java.net.InetAddress;

/**
 * Created by n0ne on 16/03/17.
 */
public abstract class ServerToClientMessage extends Message {

	public static final String CONST_PROT_FOUND = "found";
	public static final String CONST_PROT_DONE = "done";
	public static final String CONST_PROT_INVALID = "invalid";

	public static class FoundMessage extends ServerToClientMessage {

		private BigInteger n, factor;

		public FoundMessage (BigInteger n, BigInteger factor) {
			this.n = n;
			this.factor = factor;
		}

		public BigInteger getN () {
			return n;
		}

		public BigInteger getFactor () {
			return factor;
		}

		@Override
		public String toString () {
			final StringBuilder b = new StringBuilder();

			b.append(CONST_PROT_FOUND)
					.append(CONST_PROT_SPACE)
					.append(n)
					.append(CONST_PROT_SPACE)
					.append(factor)
					.append(CONST_PROT_NEWLINE)
			;

			return b.toString();
		}

	}

	public static class DoneMessage extends ServerToClientMessage {

		private BigInteger n;
		private BigInteger low, high;

		public DoneMessage (BigInteger n, BigInteger low, BigInteger high) {
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

			b.append(CONST_PROT_DONE)
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

	public static class InvalidMessage extends ServerToClientMessage {

		@Override
		public String toString () {
			return CONST_PROT_INVALID;
		}

	}

	public static class SpawnMessage extends ServerToClientMessage {

		private InetAddress address;
		private int port;

		public SpawnMessage (final InetAddress address, int port) {
			this.address = address;
			this.port = port;
		}

		public InetAddress getAddress () {
			return address;
		}

		public int getPort () {
			return port;
		}

		@Override
		public String toString () {
			return String.format(
					"%s at port %d",
					address.toString(), port
			);
		}

	}

}
