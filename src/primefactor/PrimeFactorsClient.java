package primefactor;


import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

/**
 * PrimeFactorsClient class for PrimeFactorsServer.
 * <p>
 * Your PrimeFactorsClient class should take in Program arguments space-delimited
 * indicating which PrimeFactorsServers it will connect to.
 * ex. args of "localhost:4444 localhost:4445 localhost:4446"
 * will connect the primefactor to PrimeFactorsServers running on
 * localhost:4444, localhost:4445, localhost:4446
 * <p>
 * Your primefactor should take user input from standard input.  The appropriate input
 * that can be processed is a number.  If your input is not of the correct format,
 * you should ignore it and continue to the next one.
 * <p>
 * Your primefactor should distribute to each server the appropriate range of values
 * to look for prime primefactor through.
 */
public class PrimeFactorsClient extends BaseClient {

	public static final BigInteger CONST_INPUT_MIN_VALID = new BigInteger("2");

	public static final String CONST_PROT_INVALID = "invalid";
	public static final String CONST_PROT_EQUALS = "=";
	public static final String CONST_PROT_SPACE = " ";
	public static final String CONST_PROT_MULT = "*";
	public static final String CONST_PROT_NEWLINE = "\n";

	public PrimeFactorsClient (String server, int port) throws IOException {
		super(server, port);
	}

	public void writeServer (ClientToServerMessage message) throws IOException {
		final ObjectOutputStream objectOut = new ObjectOutputStream(connection.getOutputStream());

		try {
			objectOut.writeObject(message);
		} finally {
			objectOut.close();
		}
	}

	public boolean writeUserFactoringResult (ClientToUserMessage message) {
		//TO-DO Test this method's code.
		final StringBuilder b = new StringBuilder();
		final BigInteger[] factors = message.getFactors();

		b.append(message.getProduct())
				.append(CONST_PROT_SPACE + CONST_PROT_EQUALS + CONST_PROT_SPACE);

		if (factors.length >= 1) {
			b.append(factors[0]);

			for (int i = 1; i < factors.length; i++) {
				b.append(CONST_PROT_SPACE)
				.append(CONST_PROT_MULT)
				.append(CONST_PROT_SPACE)
				.append(factors[i]);
			}
		}

		b.append(CONST_PROT_NEWLINE);

		return writeUser(b.toString());
	}

	public boolean writeUserInvalid () {
		return writeUser(CONST_PROT_INVALID);
	}

	@Override
	public boolean isUserInputValid (String input) {
		final BigInteger product;

		try {
			product = new BigInteger(input);

			return product.compareTo(CONST_INPUT_MIN_VALID) >= 1;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String filterUserInput (String input) {
		return input;
	}

	/**
	 * @param args String array containing Program arguments.  Each String indicates a
	 *             PrimeFactorsServer location in the form "host:port"
	 *             If no program arguments are inputted, this Client will terminate.
	 */
	public static void main (String[] args) {
		// TODO complete this implementation.
	}

}
