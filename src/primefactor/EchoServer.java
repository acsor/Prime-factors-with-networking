package primefactor;

import java.io.IOException;

/**
 * Created by n0ne on 13/03/17.
 */
public class EchoServer extends BaseServer {

	public static int CONST_DEF_PORT = 4444;

	public EchoServer (boolean logEnabled) throws IOException {
		this(CONST_DEF_PORT, logEnabled);
	}

	public EchoServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
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
