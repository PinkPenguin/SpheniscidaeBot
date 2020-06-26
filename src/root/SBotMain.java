package root;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SBotMain {

	public static String BOT_USERNAME = "ERROR";
	public static String CHANNEL_NAME = "ERROR";
	public static String OAUTH_TOKEN = "ERROR";
	public static String CLIENT_ID = "ERROR";
	public static String CLIENT_SECRET = "ERROR";

	private static PrintWriter out;
	private static Scanner in;

	private static ArrayList<String> drawList = new ArrayList<String>();
	private static boolean drawingIsActive = false;
	private static String winner;

	public static void main(String[] args) {

		try {
			Scanner tokenScanner = new Scanner(new File("tokens.txt"));
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
		} catch (FileNotFoundException e1) {
			// TODO: Auto-generated catch block
			System.out.println("ERROR: tokens.txt file not found!");
			e1.printStackTrace();
		}

		// TODO: Move all this code into separate class, just here for now to get things
		// going fast
		try {
			Socket socket = new Socket("irc.twitch.tv", 6667);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());

			write("PASS", OAUTH_TOKEN);
			write("NICK", BOT_USERNAME);
			write("JOIN", CHANNEL_NAME);
			
			//TODO: You know, this damn shit
			//write("CAP REQ", ":twitch.tv/tags");

			// TODO: This is just a quick fix to not break my shit on opening server
			// messages.
			// fix it properly!

			while (in.hasNext()) {
				String serverMessage = in.nextLine();
				System.out.println("<<< " + serverMessage);

				if (serverMessage.endsWith("NAMES list")) {
//					write("PRVMSG", "");
					break;
				}
			}

			while (in.hasNext()) {
				String serverMessage = in.nextLine();
				System.out.println("<<< " + serverMessage);

				if (serverMessage.startsWith("PING")) {
					String pingContents = serverMessage.split(" ", 2)[1];
					write("PONG", pingContents);
				} else {

					// NOTE: remember that this is done to the initial twitch server messages as
					// well.
					String response = parseMessage(serverMessage);
					if (response != null) {
						write("PRIVMSG", response);
					}
				}
			}

			in.close();
			out.close();
			socket.close();

			System.out.println("End!");

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void write(String command, String message) {
		String fullMessage = command + " " + message;

		if (command.equals("PRIVMSG")) {
			out.print(command + " " + CHANNEL_NAME + " :" + message + "\r\n");
			System.out.println(">>> " + command + " " + CHANNEL_NAME + " :" + message);
		} else {
			out.print(fullMessage + "\r\n");
			System.out.println(">>> " + fullMessage);
		}
		out.flush();
	}

	private static String parseMessage(String fullMessage) {
		/* 0: User info, 1: Command type, 2: Channel name, 3: Chat message */
		String[] splitMessage = fullMessage.split(" ", 4);
		String chatMessage = splitMessage[3].substring(1);
		// TODO: see if this needs to change to display name later
		User user = new User(splitMessage[0].substring(1).split("!", 2)[0]);

		String chatCommand = null;
		String chatCommandArgument = null;
		if (chatMessage.startsWith("!")) {
			String splitChatCommand[] = chatMessage.split(" ");
			chatCommand = splitChatCommand[0];
			try {
				chatCommandArgument = splitChatCommand[1];
			} catch (IndexOutOfBoundsException e) {
				/* The chat command did not have an argument */
			}

		}

//		System.out.println(user.getName());
//		System.out.println(chatCommand);

		// TODO: Eventually fix user handling with new user tags so I can differentiate
		// mod users
		if (chatCommand != null) {
			switch (chatCommand) {

			case "!drawing":
				if (user.getName().equals("pinkpenguintv")) {
					drawList = new ArrayList<String>();
					drawingIsActive = true;

					return "Starting roulette drawing. Type !enter to enter into the drawing.";
				}
				break;

			case "!enter":
				if (!user.getName().equals("pinkpenguintv")) {
					if (drawList.add(user.getName())) {
						return "@" + user.getName() + " Your entry has been submitted.";
					} else {
						return "@" + user.getName() + " You have already been entered.";
					}
				}
				break;

			case "!draw":
				if (user.getName().equals("pinkpenguintv")) {
					if (drawingIsActive = true) {
						if (drawList.isEmpty()) {
							return "There are no users in the drawing yet.";
						}
						Random rng = new Random();
//						rng.nextInt(drawList.size());
						winner = drawList.get(rng.nextInt(drawList.size()));
						drawingIsActive = false;
						//TODO: Save the winners chat messages in real time to a txt file to display on stream
						return "Congratulations: " + winner + "! You won the drawing!";
					} else {
						return "There is no active drawing.";
					}
				}
				break;
				
			//TODO: Consider merging this functionality into the !draw command
			case "!redraw":
				if(user.getName().equals("pinkpenguintv")) {
					drawList.remove(winner);
					if (drawList.isEmpty()) {
						return "There are no users in the drawing.";
					}
					Random rng = new Random();
					winner = drawList.get(rng.nextInt(drawList.size()));
					//TODO: Save the winners chat messages in real time to a .txt file to display on stream
					return "Congratulations: " + winner + "! You won the drawing!";
					
				}
				break;
				
				
			case "!roulette":
				return "PoE Build Roulette is a build randomizer for deciding what build I play next after a death. "
						+ "This is done with the help of you fine viewers. All you have to do is type \"!enter\" in "
						+ "chat to be entered into the drawing and whoever wins gets one free reroll if they're not "
						+ "happy with the first skill. After that whatever the wheel lands on is final.";
//				break;

			case "!rip":
				return "We haven't died yet PogChamp";
//				break;
			}
			

		}
		/*
		 * Reaching this part means the parsed message was not in any list of commands
		 */
		return null;
	}

}
