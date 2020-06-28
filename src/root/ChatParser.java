package root;

import java.util.ArrayList;
import java.util.Random;

public class ChatParser {

	private ArrayList<String> drawList = new ArrayList<String>();
	private boolean drawingIsActive = false;
	private String winner;

	public ChatParser() {

	}

	public String parseMessage(String fullMessage) {
		/* 0: User info, 1: Command type, 2: Channel name, 3: Chat message */
		String[] splitMessage = fullMessage.split(" ", 4);
		String chatMessage = splitMessage[3].substring(1);
		// TODO: see if this needs to change to display name later
		// TODO: this should be done elsewhere, sending the tag to somewhere to be
		// parsed
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
						// TODO: Save the winners chat messages in real time to a txt file to display on
						// stream
						return "Congratulations: " + winner + "! You won the drawing!";
					} else {
						return "There is no active drawing.";
					}
				}
				break;

			// TODO: Consider merging this functionality into the !draw command
			case "!redraw":
				if (user.getName().equals("pinkpenguintv")) {
					drawList.remove(winner);
					if (drawList.isEmpty()) {
						return "There are no users in the drawing.";
					}
					Random rng = new Random();
					winner = drawList.get(rng.nextInt(drawList.size()));
					// TODO: Save the winners chat messages in real time to a .txt file to display
					// on stream
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
