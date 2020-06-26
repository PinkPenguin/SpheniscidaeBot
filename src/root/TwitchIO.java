package root;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TwitchIO {

	private PrintWriter out;
	private Scanner in;

	public TwitchIO(String destination, int port) throws UnknownHostException, IOException {
		Socket socket = new Socket(destination, port);

		out = new PrintWriter(socket.getOutputStream(), true);
		in = new Scanner(socket.getInputStream());
	}

	public void connect() {
		write("PASS", Config.OAUTH_TOKEN);
		write("NICK", Config.BOT_USERNAME);
		write("JOIN", Config.CHANNEL_NAME);
	}

	private void write(String command, String message) {
		String fullMessage = command + " " + message;

		/* Formating for sending chat messages */
		if (command.equals("PRIVMSG")) {
			// TODO: As the TODO in Config.java says, the CHANNEL_NAME should be something
			// native to the TwitchIO class and should therefore be passed to it on creation
			out.print(command + " " + Config.CHANNEL_NAME + " :" + message + "\r\n");
			System.out.println(">>> " + command + " " + Config.CHANNEL_NAME + " :" + message);
		} else {
			out.print(fullMessage + "\r\n");
			System.out.println(">>> " + fullMessage);
		}
		out.flush();
	}

}
