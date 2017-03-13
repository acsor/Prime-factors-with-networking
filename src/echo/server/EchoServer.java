package echo.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * A simple server that will echo client inputs.
 */
public class EchoServer implements Closeable {

	public static int DEF_PORT = 4444;

	private static String LOG_NEXT_CLIENT = "%s connected";
	private static String LOG_READ_MESSAGE = "Read: %s";
	private static String LOG_CLOSE_CLIENT = "Client disconnected";
	private static String LOG_CLOSE = "Server disconnected";
	private static String LOG_CONSTRUCTOR = "Listening at port";

	private ServerSocket connection;
	private Socket client;
	private Scanner in;
	private PrintStream out;

	private boolean logEnabled;

	public EchoServer (boolean logEnabled) throws IOException {
		this(DEF_PORT, logEnabled);
	}

	public EchoServer (int port, boolean logEnabled) throws IOException {
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

	/**
	 * @param args String array containing Program arguments.  It should only
	 *             contain at most one String indicating the port it should connect to.
	 *             The String should be parseable into an int.
	 *             If no arguments, we default to port 4444.
	 */
	public static void main (String[] args) throws IOException {
		final EchoServer server;
		String message;

		if (args.length > 1) {
			server = new EchoServer(parsePort(args[0]), true);
		} else {
			server = new EchoServer(true);
		}

		while (true) {
			server.nextClient();

			do {
				message = server.readMessage();

				if (message != null) {
					server.writeMessage(message);
				}
			} while (message != null);

			server.closeClient();
		}
	}

	private static int parsePort (String stringPort) {
		try {
			return Integer.parseInt(stringPort);
		} catch (NumberFormatException e) {
			return DEF_PORT;
		}
	}

}
