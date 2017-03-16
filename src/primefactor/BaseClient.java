package primefactor;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public abstract class BaseClient implements Closeable {

	private static final String CONST_PREFIX = ">>>";

	protected Socket connection;

	protected Scanner serverIn;
	protected PrintStream serverOut;

	protected Scanner userIn;
	protected PrintStream userOut;

	public BaseClient (String server, int port) throws IOException {
		connection = new Socket(server, port);

		serverIn = new Scanner(connection.getInputStream());
		serverOut = new PrintStream(connection.getOutputStream(), true);

		userIn = new Scanner(System.in);
		userOut = System.out;
	}

	public abstract boolean isUserInputValid (String input);

	public abstract String filterUserInput (String input);

	public String readServer () {
		if (serverIn.hasNextLine()) {
			return serverIn.nextLine();
		}
		return null;
	}

	public boolean writeServer (String message) {
		if (isUserInputValid(message)) {
			serverOut.println(filterUserInput(message));
		} else {
			throw new IllegalArgumentException("Messages not adhering to the grammar can not be passed on to the server");
		}

		return serverOut.checkError();
	}

	public String readUser () {
		final String input;

		if (userIn.hasNextLine()) {
			input = userIn.nextLine();

			if (isUserInputValid(input)) {
				return filterUserInput(input);
			}
		}

		return null;
	}

	public boolean writeUser (String message) {
		if (isUserInputValid(message)) {
			userOut.format("%s %s\n", CONST_PREFIX, filterUserInput(message));
		} else {
			throw new IllegalArgumentException("Messages not complying to the grammar can not be written to console");
		}

		return userOut.checkError();
	}

	public void close () throws IOException {
		userOut.close();
		userIn.close();

		serverOut.close();
		serverIn.close();

		connection.close();
	}

}
