package primefactor.net;

import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ClientToServerMessage.FactorMessage;
import primefactor.net.message.ServerToClientMessage;
import primefactor.net.message.ServerToClientMessage.DoneMessage;
import primefactor.util.BigMath;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;

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
public class PrimeFactorsServer extends BaseServer implements Callable<DoneMessage> {

	public static final int CONST_DEF_PORT = 4444;
	/**
	 * Certainty variable for BigInteger isProbablePrime() function.
	 */
	public final static int CONST_PRIME_CERTAINTY = 10;

	private ObjectInputStream in;
	private ObjectOutputStream out;

	public PrimeFactorsServer (boolean logEnabled) throws IOException {
		this(CONST_DEF_PORT, logEnabled);
	}

	public PrimeFactorsServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
	}

	@Override
	protected void onNextClient (Socket client) throws IOException {
		out = new ObjectOutputStream(client.getOutputStream());
		in = new ObjectInputStream(client.getInputStream());
	}

	@Override
	public String readMessage () {
		throw new UnsupportedOperationException();
	}

	public FactorMessage readClientFactorMessage () throws IOException, ClassNotFoundException {
		final ClientToServerMessage result = (ClientToServerMessage) in.readObject();

		if (logEnabled) {
			log(result.toString());
		}

		return (FactorMessage) result;
	}

	public void writeMessage (ServerToClientMessage message) throws IOException {
		if (logEnabled) {
			log(message.toString());
		}

		out.writeObject(message);
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
		out.close();
		in.close();
	}

	/**
	 * Starts a communication with a {@link MasterClient} reporting errors or returning a DoneMessage in case of
	 * success.
	 * @return a DoneMessage instance if the communication was without errors, null otherwise.
	 * @throws Exception currently no exceptions are directly thrown.
	 */
	@Override
	public DoneMessage call () throws Exception {
		FactorMessage inMessage = null;
		ServerToClientMessage outMessage = null;
		List<BigInteger> primes;
		boolean isClientMessageValid;

		nextClient();

		do {
			try {
				inMessage = readClientFactorMessage();
				isClientMessageValid = true;
			} catch (ClassNotFoundException e) {
				writeMessage(new ServerToClientMessage.InvalidMessage());
				isClientMessageValid = false;
			} catch (EOFException e) {
				isClientMessageValid = false;
				break; //Break the do-while loop this catch statement is contained in.
			}
		} while (!isClientMessageValid);

		if (isClientMessageValid) {
			primes = BigMath.primeFactorsOf(
					inMessage.getN(),
					inMessage.getLowBound(),
					inMessage.getHighBound(),
					CONST_PRIME_CERTAINTY
			);

			for (BigInteger prime: primes) {
				outMessage = new ServerToClientMessage.FoundMessage(
						inMessage.getN(),
						prime
				);
				writeMessage(outMessage);
			}

			outMessage = new DoneMessage(
					inMessage.getN(),
					inMessage.getLowBound(),
					inMessage.getHighBound()
			);
			writeMessage(outMessage);
		}

		closeClient();

		return (DoneMessage) outMessage;
	}

	/**
	 * @param args String array containing Program arguments.  It should only
	 *             contain one String indicating the port it should connect to.
	 *             Defaults to port 4444 if no Program argument is present.
	 */
	public static void main (String[] args) throws IOException {
		final PrimeFactorsServer server;

		if (args.length > 0) {
			server = new PrimeFactorsServer(parsePort(args[0], CONST_DEF_PORT), true);
		} else {
			server = new PrimeFactorsServer(true);
		}

		while (true) {
			try {
				server.call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
