package primefactor.net;

import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ClientToUserMessage;
import primefactor.net.message.ServerToClientMessage;
import primefactor.util.BigMath;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.*;

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
public class PrimeFactorsClient {

	public static final BigInteger CONST_INPUT_MIN_VALID = new BigInteger("2");
	public static final BigInteger CONST_MIN_LOW_BOUND = new BigInteger("2");

	public static final String CONST_PROT_INVALID = "invalid";
	public static final String CONST_PROT_EQUALS = "=";
	public static final String CONST_PROT_SPACE = " ";
	public static final String CONST_PROT_MULT = "*";
	public static final String CONST_PROT_NEWLINE = "\n";

	public static final String CONST_ADDRESS_SEP = ":";
	public static final String CONST_USER_INPUT = "Unsigned integer to factor: ";

	private List<Socket> servers;
	private Scanner userIn;
	private PrintStream userOut;

	public PrimeFactorsClient (String... addresses) throws IOException {
		String[] splitAddress;
		servers = new LinkedList<>();

		for (String address: addresses) {
			splitAddress = address.split(CONST_ADDRESS_SEP);
			servers.add(
					new Socket(splitAddress[0], Integer.parseInt(splitAddress[1]))
			);
		}

		userIn = new Scanner(System.in);
		userOut = System.out;
	}

	public ServerToClientMessage readServerToClientMessage (int server) throws IOException {
		final ObjectInputStream serverIn = new ObjectInputStream(servers.get(server).getInputStream());
		ServerToClientMessage result;

		try {
			result = (ServerToClientMessage) serverIn.readObject();
		} catch (ClassNotFoundException e) {
			result = null;
		}

		return result;
	}

	public void writeClientToServerMessage (int server, ClientToServerMessage message) throws IOException {
		final ObjectOutputStream serverOut = new ObjectOutputStream(servers.get(server).getOutputStream());

		serverOut.writeObject(message);
	}

	public boolean writeUserFactoringResult (ClientToUserMessage message) {
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

	public String readUserRaw () {
		if (userIn.hasNextLine()) {
			return userIn.nextLine();
		}

		return null;
	}

	public String readUserFiltered () {
		String result = readUserRaw();

		if (result != null) {
			result = filterUserInput(result);
		}

		return result;
	}

	public String filterUserInput (String input) {
		return input.replaceAll("\\s+", "");
	}

	public boolean isFilteredUserInputValid (String input) {
		final BigInteger product;

		try {
			product = new BigInteger(input);

			return product.compareTo(CONST_INPUT_MIN_VALID) >= 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean writeUser (String message) {
		userOut.println(String.valueOf(message));

		return userOut.checkError();
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

			do {
				client.writeUser(CONST_USER_INPUT);
				userInMessage = client.readUserFiltered();

				if (userInMessage != null && client.isFilteredUserInputValid(userInMessage)) {
					serverOutMessage = new ClientToServerMessage(
							new BigInteger(userInMessage),
							CONST_MIN_LOW_BOUND,
							BigMath.sqrt(new BigInteger(userInMessage).add(BigInteger.ONE))
					);
					userOutMessage = new ClientToUserMessage(new BigInteger(userInMessage));
					serverOutMessages = serverOutMessage.partition();
				}
			} while (userInMessage != null);

			client.close();
		} else {
			System.err.format("%s: <server:port>\n", PrimeFactorsClient.class.getSimpleName());
			System.exit(1);
		}
	}

}
