package primefactor.net;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;

public abstract class BaseClient implements Closeable {

	private static final String CONST_PREFIX = ">>>";

	protected Socket connection;

	public BaseClient (String server, int port) throws IOException {
		connection = new Socket(server, port);

		onConnectServer(connection);
	}

	public abstract String readServer ();

	public abstract boolean writeServer (String message);

	protected abstract void onConnectServer (final Socket connection) throws IOException;

	public void close () throws IOException {
		onCloseServer();

		connection.close();
	}

	protected abstract void onCloseServer() throws IOException;

}
