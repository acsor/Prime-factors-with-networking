package primefactor.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public abstract class BaseClient implements Closeable {

	private static final String CONST_PREFIX = ">>>";

	protected Socket connection;

	protected Scanner userIn;
	protected PrintStream userOut;

	public BaseClient (String server, int port) throws IOException {
		connection = new Socket(server, port);

		onConnectServer(connection);

		userIn = new Scanner(System.in);
		userOut = System.out;
	}

	public abstract boolean isFilteredUserInputValid (String input);

	public abstract String filterUserInput (String input);

	public abstract String readServer ();

	public abstract boolean writeServer (String message);

	protected abstract void onConnectServer (final Socket connection) throws IOException;

	public final String readUserRaw () {
		if (userIn.hasNextLine()) {
			return userIn.nextLine();
		}

		return null;
	}

	public final String readUserFiltered () {
		String result = readUserRaw();

		if (result != null) {
			result = filterUserInput(result);
		}

		return result;
	}

	public boolean writeUser (String message) {
		userOut.println(String.valueOf(message));

		return userOut.checkError();
	}

	public void close () throws IOException {
		userOut.close();
		userIn.close();
		onCloseServer();

		connection.close();
	}

	protected abstract void onCloseServer() throws IOException;

}
