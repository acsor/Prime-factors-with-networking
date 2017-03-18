package primefactor.net;


import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ClientToUserMessage;
import primefactor.net.message.ServerToClientMessage;
import primefactor.util.BigMath;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.List;

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
	public static final BigInteger CONST_MIN_LOW_BOUND = new BigInteger("2");

	public static final String CONST_PROT_INVALID = "invalid";
	public static final String CONST_PROT_EQUALS = "=";
	public static final String CONST_PROT_SPACE = " ";
	public static final String CONST_PROT_MULT = "*";
	public static final String CONST_PROT_NEWLINE = "\n";

	public static final String CONST_USER_INPUT = "Unsigned integer to factor: ";

	private ObjectInputStream serverIn;
	private ObjectOutputStream serverOut;

	public PrimeFactorsClient (String server, int port) throws IOException {
		super(server, port);
	}

	public static PrimeFactorsClient clientFactory (String address) throws IOException {
		final String[] splitAddress = address.split(":");

		return new PrimeFactorsClient(splitAddress[0], Integer.valueOf(splitAddress[1]));
	}

	public ServerToClientMessage readServerToClientMessage () throws IOException {
		ServerToClientMessage result;

		try {
			result = (ServerToClientMessage) serverIn.readObject();
		} catch (ClassNotFoundException e) {
			result = null;
		}

		return result;
	}

	public void writeServer (ClientToServerMessage message) throws IOException {
		serverOut.writeObject(message);
	}

	public boolean writeUserFactoringResult (ClientToUserMessage message) {
		//TO-DO Test this method's code.
		final StringBuilder b = new StringBuilder();
		final List<BigInteger> factors = message.getFactors();

		b.append(message.getProduct())
				.append(CONST_PROT_SPACE + CONST_PROT_EQUALS + CONST_PROT_SPACE);

		if (factors.size() >= 1) {
			b.append(factors.get(0));

			for (int i = 1; i < factors.size(); i++) {
				b.append(CONST_PROT_SPACE)
				.append(CONST_PROT_MULT)
				.append(CONST_PROT_SPACE)
				.append(factors.get(i));
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

			return product.compareTo(CONST_INPUT_MIN_VALID) >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	@Override
	public String filterUserInput (String input) {
		return input.replaceAll("\\s+", "");
	}

	@Override
	public String readServer () {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean writeServer (String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void onConnectServer (Socket connection) throws IOException {
		serverIn = new ObjectInputStream(new BufferedInputStream(connection.getInputStream()));
		serverOut = new ObjectOutputStream(new BufferedOutputStream(connection.getOutputStream()));
	}

	@Override
	protected void onCloseServer () throws IOException {
		serverIn.close();
		serverOut.close();
	}

	/**
	 * @param args String array containing Program arguments.  Each String indicates a
	 *             PrimeFactorsServer location in the form "host:port"
	 *             If no program arguments are inputted, this Client will terminate.
	 */
	public static void main (String[] args) throws IOException {
		final PrimeFactorsClient client;
		ClientToServerMessage serverOutMessage;
		ServerToClientMessage serverInMessage;
		ClientToUserMessage userOutMessage;
		String userInMessage;

		if (args.length > 0) {
			client = PrimeFactorsClient.clientFactory(args[0]);

			do { //Until the user inputs an end-of-stream character:
				client.writeUser(CONST_USER_INPUT);
				userInMessage = client.filterUserInput(client.readUser());
				client.writeUser("\n");

				if (userInMessage != null && client.isUserInputValid(userInMessage)) {
					serverOutMessage = new ClientToServerMessage(
							new BigInteger(userInMessage),
							CONST_MIN_LOW_BOUND,
							BigMath.sqrt(new BigInteger(userInMessage))
					);
					client.writeServer(serverOutMessage);

					do { //Until the server responds with an "invalid message" or "done message":
						serverInMessage = client.readServerToClientMessage();
						userOutMessage = new ClientToUserMessage(new BigInteger(userInMessage));

						if (serverInMessage instanceof ServerToClientMessage.InvalidMessage) {
							client.writeUserInvalid();
							userOutMessage = null;
						} else if (serverInMessage instanceof ServerToClientMessage.FoundMessage) {
							userOutMessage.addFactor(
									((ServerToClientMessage.FoundMessage) serverInMessage).getFactor()
							);
						}
					} while (serverInMessage instanceof ServerToClientMessage.FoundMessage);

					if (userOutMessage != null) {
						client.writeUserFactoringResult(userOutMessage);
					}
				}
			} while (userInMessage != null);

			client.close();
		} else {
			System.err.format("%s: <server:port>\n", PrimeFactorsClient.class.getSimpleName());
			System.exit(1);
		}
	}

}
