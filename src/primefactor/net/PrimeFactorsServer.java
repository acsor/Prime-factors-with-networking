package primefactor.net;

import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ServerToClientMessage;
import primefactor.util.BigMath;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.List;

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
	public static final BigInteger CONST_MIN_N = new BigInteger("2");
	public static final BigInteger CONST_MIN_LOW_BOUND = new BigInteger("2");

	private ObjectInputStream objectIn;
	private ObjectOutputStream objectOut;

	/**
	 * Certainty variable for BigInteger isProbablePrime() function.
	 */
	private final static int CONST_PRIME_CERTAINTY = 10;

	public PrimeFactorsServer (boolean logEnabled) throws IOException {
		this(CONST_DEF_PORT, logEnabled);
	}

	public PrimeFactorsServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
	}

	@Override
	protected void onNextClient (Socket client) throws IOException {
		objectIn = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
		objectOut = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
	}

	@Override
	public String readMessage () {
		throw new UnsupportedOperationException();
	}

	public ClientToServerMessage readClientFactorMessage () throws IOException, ClassNotFoundException {
		return (ClientToServerMessage) objectIn.readObject();
	}

	public boolean isClientToServerMessageValid (ClientToServerMessage message) {
		return message.getN().compareTo(CONST_MIN_N) >= 0 &&
				message.getLowBound().compareTo(CONST_MIN_LOW_BOUND) >= 0 &&
				message.getHighBound().compareTo(message.getN()) < 0 &&
				message.getLowBound().compareTo(message.getHighBound()) <= 0
				;
	}

	public void writeMessage (ServerToClientMessage message) throws IOException {
		objectOut.writeObject(message);
	}

	@Override
	public boolean writeMessage (String message) {
		throw new UnsupportedOperationException(
				String.format(
						"See the other methods when using %s for communicating with clients",
						PrimeFactorsServer.class.getSimpleName()
				)
		);
	}

	@Override
	protected void onCloseClient () throws IOException {
		objectIn.close();
		objectOut.close();
	}

	/**
	 * @param args String array containing Program arguments.  It should only
	 *             contain one String indicating the port it should connect to.
	 *             Defaults to port 4444 if no Program argument is present.
	 */
	public static void main (String[] args) throws IOException {
		final PrimeFactorsServer server;
		ClientToServerMessage inMessage = null;
		ServerToClientMessage outMessage;
		List<BigInteger> primes;
		boolean isMessageValid;

		if (args.length > 0) {
			server = new PrimeFactorsServer(parsePort(args[0], CONST_DEF_PORT), true);
		} else {
			server = new PrimeFactorsServer(true);
		}

		while (true) {
			server.nextClient();

			do {
				try {
					inMessage = server.readClientFactorMessage();
					isMessageValid = server.isClientToServerMessageValid(inMessage);
				} catch (ClassNotFoundException e) {
					server.writeMessage(
							new ServerToClientMessage.InvalidMessage()
					);
					isMessageValid = false;
				}
			} while (!isMessageValid);

			primes = BigMath.primeFactorsOf(
					inMessage.getN(),
					inMessage.getHighBound(),
					inMessage.getLowBound()
			);

			for (BigInteger prime : primes) {
				outMessage = new ServerToClientMessage.FoundMessage(
						inMessage.getN(),
						prime
				);
				server.writeMessage(outMessage);
			}

			outMessage = new ServerToClientMessage.DoneMessage(
					inMessage.getN(),
					inMessage.getLowBound(),
					inMessage.getHighBound()
			);
			server.writeMessage(outMessage);

			server.closeClient();
		}
	}

}
