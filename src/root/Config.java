package root;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//NOTE: Possible, in the future, make it so that the bot can handle multiple channels at once.
public class Config {

	public static String BOT_USERNAME = "ERROR";
	public static String CHANNEL_NAME = "ERROR";
	public static String OAUTH_TOKEN = "ERROR";
	public static String CLIENT_ID = "ERROR";
	public static String CLIENT_SECRET = "ERROR";

	private Config(File file) throws FileNotFoundException {
//		initialize(file);
	}

	public static void initialize(File file) throws FileNotFoundException {
		// TODO: Encrypt, decrypt and prompt for the information if not already
		// available.
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

		if (BOT_USERNAME == "ERROR" || CHANNEL_NAME == "ERROR" || OAUTH_TOKEN == "ERROR" || CLIENT_ID == "ERROR"
				|| CLIENT_SECRET == "ERROR") {

			System.out.println("ERROR: tokens.txt is corrupted or missing tokens!");

		}
	}

}
