package primefactor.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

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
		onNextClient(client);

		if (logEnabled) {
			log(String.format(LOG_NEXT_CLIENT, client));
		}
	}

	/**
	 * Invoked as a callback method when a connection with a new client has been established.<br>
	 * This method can be used to create input or output stream instances to communicate with the client.
	 * @param client Socket instance associated with the client.
	 */
	protected abstract void onNextClient (final Socket client) throws IOException;

	public Socket getClientSocket () {
		return client;
	}

	public abstract String readMessage ();

	public abstract boolean writeMessage (String message);

	public void close () throws IOException {
		if (client != null && !client.isClosed()) {
			closeClient();
		}
		connection.close();

		if (logEnabled) {
			log(LOG_CLOSE);
		}
	}

	public void closeClient () throws IOException {
		onCloseClient();
		client.close();

		if (logEnabled) {
			log(LOG_CLOSE_CLIENT);
		}
	}

	/**
	 * Called right <i>before</i> the connection with a client is closed.
	 */
	protected abstract void onCloseClient() throws IOException;

	protected void log (String output) {
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
