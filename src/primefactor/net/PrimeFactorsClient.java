package primefactor.net;

import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ClientToServerMessage.FactorMessage;
import primefactor.net.message.ClientToUserMessage;
import primefactor.net.message.ServerToClientMessage;
import primefactor.net.message.UserToClientMessage;
import primefactor.util.BigMath;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

		try {
			return (ServerToClientMessage) serverIn.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public void writeClientToServerMessage (int server, FactorMessage message) throws IOException {
		final ObjectOutputStream serverOut = new ObjectOutputStream(servers.get(server).getOutputStream());

		serverOut.writeObject(message);
	}

	public boolean writeUserFactoringResult (ClientToUserMessage message) {
		//This method previously contained what is now in the ClientToUserMessage.toString() method.
		//TO-DO Check the refactoring works well.
		return writeUser(message.toString());
	}

	public String readUserRaw () {
		if (userIn.hasNextLine()) {
			return userIn.nextLine();
		}

		return null;
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

		FactorMessage serverOutMessage;
		ClientToServerMessage.SpawnMessage serverOutSpawnMessage;
		List<FactorMessage> serverOutMessages;
		ServerToClientMessage.SpawnMessage serverInSpawnMessage;

		ClientToUserMessage userOutMessage;
		UserToClientMessage.FactorMessage userInMessage;

		/*
		if (args.length > 0) {
			client = PrimeFactorsClient.clientFactory(args[0]);

			do {
				client.writeUser(CONST_USER_INPUT);
				userInMessage = UserToClientMessage.FactorMessage.factorMessageFactory(client.readUserRaw());

				if (userInMessage != null) {
					serverOutMessage = new FactorMessage(
							userInMessage.getN(),
							FactorMessage.CONST_MIN_LOW_BOUND,
							BigMath.sqrt(userInMessage.getN().add(BigInteger.ONE))
					);
					userOutMessage = new ClientToUserMessage(userInMessage.getN());
					serverOutMessages = serverOutMessage.partition();
					serverOutSpawnMessage = new ClientToServerMessage.SpawnMessage(serverOutMessages.size());

					writeSpawnMessage(serverOutSpawnMessage);

					for (int server = 0; server < serverOutSpawnMessage.getServersNumber(); server++) {
						serverInSpawnMessage = readServerToClientMessage();

						/*
						Now that we have a server to assign a partition to factor, we:
							* Invoke a new thread in which to handle the communication between that server and the client;
						 * /
					}
				}
			} while (userInMessage != null); //Until the user input is valid

			client.close();
		} else {
			System.err.format("%s: <server:port>\n", PrimeFactorsClient.class.getSimpleName());
			System.exit(1);
		}
		*/
	}

}
