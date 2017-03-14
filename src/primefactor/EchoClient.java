package primefactor;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * A simple primefactor that will interact with an BaseServer.
 */
public class EchoClient implements Closeable {

	private static final String CONST_PREFIX = ">>>";

	private Socket connection;

	private Scanner socketIn;
	private PrintStream socketOut;

	private Scanner consoleIn;
	private PrintStream consoleOut;

	public EchoClient (String server, int port) throws IOException {
		connection = new Socket(server, port);

		socketIn = new Scanner(connection.getInputStream());
		socketOut = new PrintStream(connection.getOutputStream(), true);

		consoleIn = new Scanner(System.in);
		consoleOut = System.out;
	}

	public static EchoClient clientFactory (String address) throws IOException {
		final String[] splitAddress = address.split(":");

		return new EchoClient(splitAddress[0], Integer.valueOf(splitAddress[1]));
	}

	public boolean isInputValid (String input) {
		return input != null && filterInput(input).length() > 0;
	}

	public String filterInput (String input) {
		return input.replaceAll("\\n", "");
	}

	public String readNetwork () {
		if (socketIn.hasNextLine()) {
			return socketIn.nextLine();
		}
		return null;
	}

	public boolean writeNetwork (String message) {
		if (isInputValid(message)) {
			socketOut.println(filterInput(message));
		} else {
			throw new IllegalArgumentException("Messages not adhering to the grammar can not be passed on to the server");
		}

		return socketOut.checkError();
	}

	public String readConsole () {
		final String input;

		if (consoleIn.hasNextLine()) {
			input = consoleIn.nextLine();

			if (isInputValid(input)) {
				return filterInput(input);
			}
		}

		return null;
	}

	public boolean writeConsole (String message) {
		if (isInputValid(message)) {
			consoleOut.format("%s %s\n", CONST_PREFIX, filterInput(message));
		} else {
			throw new IllegalArgumentException("Messages not complying to the grammar can not be written to console");
		}

		return consoleOut.checkError();
	}

	public void close () throws IOException {
		consoleOut.close();
		consoleIn.close();

		socketOut.close();
		socketIn.close();

		connection.close();
	}

	/**
	 * @param args String array containing Program arguments.  It should only
	 *             contain exactly one String indicating which server to connect to.
	 *             We require that this string be in the form hostname:portnumber.
	 */
	public static void main (String[] args) throws IOException {
		final EchoClient client;
		String consoleIn, networkIn;

		if (args.length > 0) {
			client = EchoClient.clientFactory(args[0]);

			do {
				consoleIn = client.readConsole();

				if (consoleIn != null) {
					client.writeNetwork(consoleIn);

					networkIn = client.readNetwork();
					client.writeConsole(networkIn);
				}
			} while (consoleIn != null);

			client.close();
		} else {
			System.err.format("%s: <server:port>\n", EchoClient.class.getSimpleName());
			System.exit(1);
		}

	}

}
