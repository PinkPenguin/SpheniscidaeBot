package root;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

public class SBotMain {

	public static void main(String[] args) {

		/* Initialize the Log Handler */
		LogHandler.setFile(new File("chat_log.txt"));
		try {
			LogHandler.init();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/* Initialize tokens and pass-codes etc */
		try {
			Config.initialize(new File("tokens.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not configure tokens!");
			e.printStackTrace();
		}

		/* Initialize the API connection */
		//TODO: Add these to the config file instead?
		String url = "irc.twitch.tv";
		int port = 6667;

		try {
			TwitchIO twitchIO = new TwitchIO(url, port);
			twitchIO.connect();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
