package primefactor.net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * A simple echo client that will interact with an EchoServer.
 */
public class EchoClient extends BaseClient {

	protected Scanner serverIn;
	protected PrintStream serverOut;

	public EchoClient (String server, int port) throws IOException {
		super(server, port);
	}

	public static EchoClient clientFactory (String address) throws IOException {
		final String[] splitAddress = address.split(":");

		return new EchoClient(splitAddress[0], Integer.valueOf(splitAddress[1]));
	}

	public boolean isFilteredUserInputValid (String input) {
		return input != null && filterUserInput(input).length() > 0;
	}

	public String filterUserInput (String input) {
		return input.replaceAll("\\n", "");
	}

	@Override
	public String readServer () {
		if (serverIn.hasNextLine()) {
			return serverIn.nextLine();
		}
		return null;
	}

	@Override
	public boolean writeServer (String message) {
		serverOut.println(String.valueOf(message));

		return serverOut.checkError();
	}

	@Override
	protected void onConnectServer (Socket connection) throws IOException {
		serverIn = new Scanner(connection.getInputStream());
		serverOut = new PrintStream(connection.getOutputStream(), true);
	}

	@Override
	protected void onCloseServer () throws IOException {
		if (serverIn != null) {
			serverIn.close();
		}
		if (serverOut != null) {
			serverOut.close();
		}
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
				consoleIn = client.readUserRaw();

				if (consoleIn != null && client.isFilteredUserInputValid(consoleIn)) {
					client.writeServer(consoleIn);

					networkIn = client.readServer();
					client.writeUser(networkIn);
				}
			} while (consoleIn != null);

			client.close();
		} else {
			System.err.format("%s: <server:port>\n", EchoClient.class.getSimpleName());
			System.exit(1);
		}

	}
}
