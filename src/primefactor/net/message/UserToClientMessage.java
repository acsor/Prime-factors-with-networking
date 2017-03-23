package primefactor.net.message;

import java.math.BigInteger;

/**
 * Created by n0ne on 23/03/17.
 */
public abstract class UserToClientMessage extends Message {

	public static class FactorMessage {

		public static final BigInteger CONST_INPUT_MIN_VALID = BigInteger.valueOf(2);

		private BigInteger n;

		/**
		 *
		 * @param n BigInteger to factor; must be n >= CONST_INPUT_MIN_VALID.
		 */
		public FactorMessage (BigInteger n) {
			//TO-DO This class wasn't tested. Do it in some way.
			this.n = n;

			if (!checkRepresentation()) {
				throw new InvalidMessageException(
						String.format("BigInteger %s does not comply to FactorMessage specification", n)
				);
			}
		}

		private boolean checkRepresentation () {
			return n.compareTo(CONST_INPUT_MIN_VALID) >= 0;
		}

		public BigInteger getN () {
			return n;
		}

		public static FactorMessage factorMessageFactory (String userInput) {
			BigInteger input;

			try {
				input = new BigInteger(filterUserInput(userInput));
				return new FactorMessage(input);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		private static String filterUserInput (String input) {
			return input.replaceAll("\\s+", "");
		}

	}

}
