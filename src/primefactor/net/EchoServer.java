package primefactor.net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by n0ne on 13/03/17.
 */
public class EchoServer extends BaseServer {

	public static int CONST_DEF_PORT = 4444;

	protected Scanner in;
	protected PrintStream out;

	public EchoServer (boolean logEnabled) throws IOException {
		this(CONST_DEF_PORT, logEnabled);
	}

	public EchoServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
	}

	@Override
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

	@Override
	public boolean writeMessage (String message) {
		out.println(message);

		return out.checkError();
	}

	@Override
	protected void onNextClient (final Socket client) throws IOException {
		in = new Scanner(client.getInputStream());
		out = new PrintStream(client.getOutputStream());
	}

	@Override
	protected void onCloseClient () {
		out.close();
		in.close();
	}

	/**
	 * @param args String array containing Program arguments.  It should only
	 *             contain at most one String indicating the port it should connect to.
	 *             The String should be parseable into an int.
	 *             If no arguments, we default to port 4444.
	 */
	public static void main (String[] args) throws IOException {
		final BaseServer server;
		String message;

		if (args.length > 0) {
			server = new EchoServer(parsePort(args[0], CONST_DEF_PORT), true);
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

}
