package root;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

public class ChatParser {

	private ArrayList<User> drawList;
	private boolean drawingIsActive = false;
	private String drawingKeyword;
	private User winner;

	private HashMap<User, Long> activeUserList;
	private long timeDelta;
	private long lastTime;
	/* 900000 ms is 15 minutes */
	private final long activityCleanTime = 900000;

	// private HashMap<String, String> commandMap = new HashMap<String, String>();

	public ChatParser() {
		this.timeDelta = System.currentTimeMillis();
		this.activeUserList = new HashMap<User, Long>();
		this.drawList = new ArrayList<User>();
		this.drawingKeyword = "!enter";
	}

	public String parseMessage(String fullMessage) {
		/*
		 * 0: User tag, 1: User info 2: Command type, 3: Channel name, 4: Chat message
		 */
		String[] splitMessage = fullMessage.split(" ", 5);
		String chatMessage = splitMessage[4].substring(1);
		User chatter = new User(splitMessage[0].substring(1).split("!", 2)[0]);

		long currentTime = System.currentTimeMillis();
		timeDelta += (currentTime - lastTime);
		if (!chatter.isBroadcaster()) {
			activeUserList.put(chatter, currentTime);
		}

		/* Clean activity list if it's been more than 15 min since last time. */
		if (timeDelta > activityCleanTime) {
			Iterator<User> itr = activeUserList.keySet().iterator();
			while (itr.hasNext()) {
				if (currentTime - activeUserList.get(itr.next()) > activityCleanTime) {
					itr.remove();
				}
			}
			timeDelta = 0;
		}

		try {
			LogHandler.logEvent(chatter, chatMessage);
		} catch (IOException e1) {
			System.out.println("ERROR!: Couldnt write to file!");
			e1.printStackTrace();
		}

		/* Identify and parse commands. */
		String chatCommand = null;
		String chatCommandArgument = null;
		if (chatMessage.startsWith("!")) {

			String splitChatCommand[] = chatMessage.split(" ");
			chatCommand = splitChatCommand[0].toLowerCase();
			try {
				// TODO: make sure to further process this in case multiple arguments etc
				if (splitChatCommand[1].startsWith("-")) {
					chatCommandArgument = splitChatCommand[1];
				}
			} catch (IndexOutOfBoundsException e) {
				/* The chat command did not have an argument */
			}
		}

		// TODO: Save commands to file so they can be edited.
		// TODO: Add functionality to edit, add or remove commands.

		if (chatCommand != null) {
			System.out.println(chatCommand);
			switch (chatCommand) {

			case "!drawing":
				if (chatter.isMod()) {
					if (chatCommandArgument.equals("-clear")) {
						drawList.clear();
						return "Drawing list has been cleared.";
					} else if (chatCommandArgument.equals("-end")){
						drawList.clear();
						drawingIsActive = false;
						return "Drawing has ended.";
					} else {
						drawList = new ArrayList<User>();
						drawingIsActive = true;

						return "Starting roulette drawing. Type !enter to enter into the drawing.";
					}
				}
				break;

			case "!draw":
				if (chatter.isMod()) {
					if (drawingIsActive = true) {
						drawList.remove(winner);
						if (drawList.isEmpty()) {
							return "There are no users in the drawing yet.";
						}
						Random rng = new Random();
						winner = drawList.get(rng.nextInt(drawList.size()));
//						drawingIsActive = false;
						// TODO: Save the winners chat messages in real time to a txt file to display on
						// stream
						return "Congratulations @" + winner.getName() + "! You won the drawing!";
					} else {
						return "There is no active drawing.";
					}
				}
				break;

			// TODO: case Draw from activity list.

			case "!clearactive":
				activeUserList.clear();
				return "Active user list cleared.";
//				break;

			// TODO: Like before, add separate functionality to read/edit these from a file
			// for now
			case "!roulette":
				return "PoE Build Roulette is a build randomizer for deciding what build I play next after a death. "
						+ "This is done with the help of you fine viewers. All you have to do is type \"!enter\" in "
						+ "chat to be entered into the drawing and whoever wins gets one free reroll if they're not "
						+ "happy with the first skill. After that whatever the wheel lands on is final.";
//				break;

			case "!rip":
				return "We haven't died yet PogChamp";
//				break;

			case "!test":
				return "I'm alive!";

			case "!discord":
				return "Here is the link to our discord: https://discord.gg/D2ezMPd";
//				break;

			case "!camera":
				return "Unfortunately the camera is acting up due to a cable issue. Hopefully I can get it fixed soonTM.";

			case "!cam":
				return "Unfortunately the camera is acting up due to a cable issue. Hopefully I can get it fixed soonTM.";

			case "!schedule":
				return "I don't have a set schedule though streams tend to start at around 10pm CET (around 4pm EST)";

			case "!profile":
				return "Here is the link to my PoE profile page: https://www.pathofexile.com/account/view-profile/PinkPenguin/characters";

			case "!build":
				return "Check the stream...";
			}

		}
		
		/*Entry into an active drawing. Default keyword is "!enter"*/
		if(chatCommand.equals(drawingKeyword)) {
			if (!chatter.isBroadcaster()) {
				drawList.add(chatter);
			}
		}
		
		/*
		 * Reaching this part means the parsed message was not in any list of commands
		 */
		return null;
	}
}
