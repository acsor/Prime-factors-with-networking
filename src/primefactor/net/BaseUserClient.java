package primefactor.net;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Created by n0ne on 25/03/17.
 */
public abstract class BaseUserClient extends BaseClient {

	protected Scanner userIn;
	protected PrintStream userOut;

	public BaseUserClient (String server, int port) throws IOException {
		super(server, port);

		userIn = new Scanner(System.in);
		userOut = System.out;
	}

	public abstract boolean isFilteredUserInputValid (String input);

	public abstract String filterUserInput (String input);

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

	@Override
	public void close () throws IOException {
		super.close();
		userOut.close();
		userIn.close();
	}

}
