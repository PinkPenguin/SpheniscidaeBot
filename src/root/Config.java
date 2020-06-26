package root;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//TODO: Make class effectively static after fixing CHANNEL_NAME TODO
public class Config {

	public static String BOT_USERNAME = "ERROR";
	// TODO: CHANNEL_NAME should likely not be here if the idea is to let the bot
	// join multiple channels with multiple instances of TwitchIO.
	public static String CHANNEL_NAME = "ERROR";
	public static String OAUTH_TOKEN = "ERROR";
	public static String CLIENT_ID = "ERROR";
	public static String CLIENT_SECRET = "ERROR";

	public Config(File file) throws FileNotFoundException {
		initialize(file);
	}

	private void initialize(File file) throws FileNotFoundException {
		Scanner tokenScanner = new Scanner(file);
		while (tokenScanner.hasNextLine()) {
			String line[] = tokenScanner.nextLine().split(":", 2);
			if (line[0].startsWith("BOT_USERNAME")) {
				BOT_USERNAME = line[1];
			} else if (line[0].startsWith("CHANNEL_NAME")) {
				CHANNEL_NAME = line[1];
			} else if (line[0].startsWith("OAUTH_TOKEN")) {
				OAUTH_TOKEN = line[1];
			} else if (line[0].startsWith("CLIENT_ID")) {
				CLIENT_ID = line[1];
			} else if (line[0].startsWith("CLIENT_SECRET")) {
				CLIENT_SECRET = line[1];
			}
		}
		
		tokenScanner.close();
	}

}
