package root;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TwitchIO {

	private PrintWriter scanOut;
	private Scanner scanIn;
	private Socket socket;
	private ChatParser chatParser;

	public TwitchIO(String destination, int port) throws UnknownHostException, IOException {
		try {
			this.socket = new Socket(destination, port);
			this.scanOut = new PrintWriter(socket.getOutputStream(), true);
			this.scanIn = new Scanner(socket.getInputStream());
		} catch (UnknownHostException e) {
			System.out.println("ERROR: Could not resolve the host IP!");
			System.out.println("Destination: " + destination + ", " + port);
			e.printStackTrace();
		}
		chatParser = new ChatParser();
	}

	/*
	 * Connects and holds the streams open
	 */
	public void connect() {
		write("PASS", Config.OAUTH_TOKEN);
		write("NICK", Config.BOT_USERNAME);
		write("JOIN", Config.CHANNEL_NAME);

		write("CAP REQ", ":twitch.tv/tags");

		while (scanIn.hasNext()) {
			String serverMessage = scanIn.nextLine();
			System.out.println("<<< " + serverMessage);

			if (serverMessage.endsWith("NAMES list")) {
				break;
			}
		}

		while (scanIn.hasNext()) {
			String serverMessage = scanIn.nextLine();
			System.out.println("<<< " + serverMessage);

			if (serverMessage.startsWith("PING")) {
				String pingContents = serverMessage.split(" ", 2)[1];
				write("PONG", pingContents);
			} else {

				// NOTE: remember that this is done to the initial twitch server messages as
				// well.
				// TODO: this is only a quick fix, this needs to be fixed
				if (!serverMessage.startsWith(":tmi")) {

					String response = chatParser.parseMessage(serverMessage);
					if (response != null) {
						write("PRIVMSG", response);
					}
				}
			}
		}

		scanIn.close();
		scanOut.close();
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Pushes messages through the output stream. Either API information or direct
	 * messages to the stream IRCchat.
	 */
	private void write(String command, String message) {
		String fullMessage = command + " " + message;

		/* Formating for sending chat messages */
		if (command.equals("PRIVMSG")) {
			scanOut.print(command + " " + Config.CHANNEL_NAME + " :" + message + "\r\n");
			System.out.println(">>> " + command + " " + Config.CHANNEL_NAME + " :" + message);
		} else {
			scanOut.print(fullMessage + "\r\n");
			if (!command.equals("PASS")) {
				System.out.println(">>> " + fullMessage);
			} else {
				System.out.println(">>> " + command + " " + message.replaceAll(".", "*"));

			}
		}
		scanOut.flush();
	}

}
