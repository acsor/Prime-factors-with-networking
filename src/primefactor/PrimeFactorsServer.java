package primefactor;

import java.io.IOException;

/**
 * PrimeFactorsServer performs the "server-side" algorithm
 * for counting prime primefactor.
 * <p>
 * Your PrimeFactorsServer should take in a single Program Argument
 * indicating which port your Server will be listening on.
 * ex. arg of "4444" will make your Server listen on 4444.
 * <p>
 * Your server will only need to handle one primefactor at a time.  If the
 * connected primefactor disconnects, your server should go back to listening for
 * future clients to connect to.
 * <p>
 * The primefactor messages that come in will indicate the value that is being
 * factored and the range of values this server will be processing over.
 * Your server will take this in and message back all primefactor for our value.
 */
public class PrimeFactorsServer extends BaseServer {

	public static final int CONST_DEF_PORT = 4444;

	/**
	 * Certainty variable for BigInteger isProbablePrime() function.
	 */
	private final static int CONST_PRIME_CERTAINTY = 10;

	public static final String CONST_PROT_FOUND = "found";
	public static final String CONST_PROT_DONE = "done";
	public static final String CONST_PROT_INVALID = "invalid";

	public PrimeFactorsServer (boolean logEnabled) throws IOException {
		this(CONST_DEF_PORT, logEnabled);
	}

	public PrimeFactorsServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
	}

	@Override
	public boolean writeMessage (String message) {
		throw new UnsupportedOperationException(
				String.format(
						"See methods 1 and 2 when using %s for communicating with clients",
						PrimeFactorsServer.class.getSimpleName()
				)
		);
	}

	/**
	 * @param args String array containing Program arguments.  It should only
	 *             contain one String indicating the port it should connect to.
	 *             Defaults to port 4444 if no Program argument is present.
	 */
	public static void main (String[] args) {
		// TODO Complete this implementation.
	}

}
