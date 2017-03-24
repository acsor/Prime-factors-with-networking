package primefactor.net;

import primefactor.net.message.ClientToServerMessage;
import primefactor.net.message.ServerToClientMessage;
import primefactor.net.message.ServerToClientMessage.SpawnMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by n0ne on 23/03/17.
 */
public final class MasterServer extends BaseServer {

	public static final int CONST_DEF_PORT = PrimeFactorsServer.CONST_DEF_PORT - 1;
	public static final int CONST_MAX_PORT_TRIES = 100;

	public MasterServer (int port, boolean logEnabled) throws IOException {
		super(port, logEnabled);
	}

	public ClientToServerMessage.SpawnMessage readSpawnMessage () throws IOException {
		final ObjectInputStream in = new ObjectInputStream(client.getInputStream());
		ClientToServerMessage.SpawnMessage result;

		try {
			result = (ClientToServerMessage.SpawnMessage) in.readObject();

			if (logEnabled) {
				log(result.toString());
			}
		} catch (ClassNotFoundException e) {
			result = null;
		}

		return result;
	}

	public void writeMessage (SpawnMessage message) throws IOException {
		final ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());

		if (logEnabled) {
			log(message.toString());
		}

		out.writeObject(message);
	}

	public static PrimeFactorsServer primeFactorsServerFactory (int port, boolean logEnabled) {
		PrimeFactorsServer result = null;

		for (int i = 0; i < CONST_MAX_PORT_TRIES || result == null; i++) {
			try {
				result = new PrimeFactorsServer(port + i, logEnabled);
			} catch (IOException e) {
				result = null;
			}
		}

		return result;
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
		final MasterServer server;
		List<PrimeFactorsServer> workerServers = new LinkedList<>();
		ThreadPoolExecutor threadPool;
		ClientToServerMessage.SpawnMessage inMessage;
		ServerToClientMessage.SpawnMessage outMessage;

		if (args.length > 0) {
			server = new MasterServer(BaseServer.parsePort(args[0], CONST_DEF_PORT), true);
		} else {
			server = new MasterServer(CONST_DEF_PORT, true);
		}

		while (true) {
			server.nextClient();
			inMessage = server.readSpawnMessage();
			/*
			For as many times as indicated by inMessage:
				* start a worker server s[i]
				* reply the client that a server s[i] has been started with an assigned port p[i]
				* wait for an exit status message from each of the worker servers, act accordingly when receiving it
					and eventually terminate the server
			 */
			threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(inMessage.getServersNumber());

			for (int i = 0; i < inMessage.getServersNumber(); i++) {
				workerServers.add(primeFactorsServerFactory(PrimeFactorsServer.CONST_DEF_PORT, server.logEnabled));
				threadPool.submit(workerServers.get(workerServers.size() - 1));
				outMessage = new SpawnMessage(workerServers.get(workerServers.size() - 1).connection.getLocalPort());
				server.writeMessage(outMessage);
			}

			server.closeClient();
		}
	}

}
