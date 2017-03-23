package primefactor.net;

import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ServerToClientMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

/**
 * Created by n0ne on 23/03/17.
 */
public class MasterServer extends BaseServer {

	public static final int CONST_DEF_PORT = 4444;

	public MasterServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
	}

	public ClientToServerMessage.SpawnMessage readSpawnMessage () throws IOException {
		final ObjectInputStream in = new ObjectInputStream(client.getInputStream());

		try {
			return (ClientToServerMessage.SpawnMessage) in.readObject();
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public void writeMessage (ServerToClientMessage.SpawnMessage message) throws IOException {
		final ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

		out.writeObject(message);
	}

	public Callable primeFactorsServerRunnableFactory (int port, boolean logEnabled) {
		return new Callable() {

			@Override
			public Object call () throws Exception {
				return null;
			}

		};
	}

	@Override
	protected void onNextClient (Socket client) throws IOException {

	}

	@Override
	public String readMessage () {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean writeMessage (String message) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void onCloseClient () throws IOException {

	}

	public static void main (String[] args) throws IOException {
		final MasterServer server = new MasterServer(BaseServer.parsePort(args[0], CONST_DEF_PORT), true);
		ClientToServerMessage.SpawnMessage inMessage;
		ServerToClientMessage.SpawnMessage outMessage;

		while (true) {
			inMessage = server.readSpawnMessage();

			/*
			For as many times as indicated by inMessage:
				* start a worker server s[i]
				* reply the client that a server s[i] has been started with an assigned port p[i]
				* wait for an exit status message from each of the worker servers, act accordingly when receiving it
					and eventually terminate the server
			 */
		}
	}

}
