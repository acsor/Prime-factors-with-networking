package echo.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Scanner;

/**
 * A simple server that will echo client inputs.
 */
public class EchoServer {

	public static int DEF_PORT = 4444;

	private static String LOG_NEXT_CLIENT = "%s connected";
	private static String LOG_READ_MESSAGE = "Read: %s";
	private static String LOG_CLOSE_CLIENT = "Client closed";

	private ServerSocket connection;
	private Socket client;
	private Scanner in;
	private PrintWriter out;

	private boolean logEnabled;

	public EchoServer () throws IOException {
		this(DEF_PORT);
	}

	public EchoServer (int port) throws IOException {
		connection = new ServerSocket(port);
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
		out = new PrintWriter(client.getOutputStream());

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
			server = new EchoServer(parsePort(args[0]));
		} else {
			server = new EchoServer();
		}

		server.setLogEnabled(true);

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
