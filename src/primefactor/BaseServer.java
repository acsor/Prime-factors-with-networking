package primefactor;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * A base server class to be subclassed by more specialized types of servers.
 */
public abstract class BaseServer implements Closeable {

	protected static String LOG_NEXT_CLIENT = "%s connected";
	protected static String LOG_READ_MESSAGE = "Read: %s";
	protected static String LOG_CLOSE_CLIENT = "Client disconnected";
	protected static String LOG_CLOSE = "Server disconnected";
	protected static String LOG_CONSTRUCTOR = "Listening at port";

	protected ServerSocket connection;
	protected Socket client;
	protected Scanner in;
	protected PrintStream out;

	protected boolean logEnabled;

	public BaseServer (int port, boolean logEnabled) throws IOException {
		connection = new ServerSocket(port);
		this.logEnabled = logEnabled;

		if (logEnabled) {
			log(String.format("%s %d", LOG_CONSTRUCTOR, port));
		}
	}

	public void setLogEnabled (boolean enabled) {
		logEnabled = enabled;
	}

	public boolean isLogEnabled () {
		return logEnabled;
	}

	public void nextClient () throws IOException {
		client = connection.accept();
		in = new Scanner(client.getInputStream());
		out = new PrintStream(client.getOutputStream());

		if (logEnabled) {
			log(String.format(LOG_NEXT_CLIENT, client));
		}
	}

	public Socket getClientSocket () {
		return client;
	}

	public String readMessage () {
		String result = null;

		if (client.isConnected()) {
			if (in.hasNextLine()) {
				result = in.nextLine();
			}
		}

		if (logEnabled) {
			if (result != null) {
				log(
						String.format(LOG_READ_MESSAGE, result)
				);
			}
		}

		return result;
	}

	public boolean writeMessage (String message) {
		out.println(message);

		return out.checkError();
	}

	public void closeClient () throws IOException {
		out.close();
		in.close();
		client.close();

		if (logEnabled) {
			log(LOG_CLOSE_CLIENT);
		}
	}

	public void close () throws IOException {
		if (client != null && !client.isClosed()) {
			closeClient();
		}
		connection.close();

		if (logEnabled) {
			log(LOG_CLOSE);
		}
	}

	private void log (String output) {
		System.out.format(
				"[%s] %s\n", new Date(System.currentTimeMillis()), String.valueOf(output)
		);
	}

	protected static int parsePort (String stringPort, int defaultPort) {
		try {
			return Integer.parseInt(stringPort);
		} catch (NumberFormatException e) {
			return defaultPort;
		}
	}

}
