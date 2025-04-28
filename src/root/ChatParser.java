package root;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ChatParser {

	private String commandDir = "commands.txt";
	private HashMap<String, String> simpleCommands;

	private ArrayList<User> drawList;
	private boolean drawingIsActive = false;
	private String drawingKeyword;
	private User winner;

	private HashMap<User, Long> activeUserList;
	private long activityTimeDelta;
	private long lastTime;
	/* 900000 ms is 15 minutes */
	private final long activityCleanTime = 900000;


	public ChatParser() {
		this.activityTimeDelta = 0;
		this.lastTime = System.currentTimeMillis();
		this.activeUserList = new HashMap<User, Long>();
		this.drawList = new ArrayList<User>();
		this.drawingKeyword = "!enter";

		this.simpleCommands = new HashMap<String, String>();
		loadCommands(new File(commandDir));
	}

	public String parseMessage(String fullMessage) {

		/*
		 * 0: User tag, 1: User info 2: Command type, 3: Channel name, 4: Chat message
		 */
		String[] splitMessage = fullMessage.split(" ", 5);
		String chatMessage = splitMessage[4].substring(1);
		User chatter = new User(splitMessage[0].substring(1).split("!", 2)[0]);

		long currentTime = System.currentTimeMillis();
		activityTimeDelta += (currentTime - lastTime);
		if (!chatter.isBroadcaster()) {
			activeUserList.put(chatter, currentTime);
		}

		/* Clean activity list if it's been more than 15 min since last clean. */
		if (activityTimeDelta > activityCleanTime) {
			Iterator<User> itr = activeUserList.keySet().iterator();
			while (itr.hasNext()) {
				if (currentTime - activeUserList.get(itr.next()) > activityCleanTime) {
					itr.remove();
				}
			}
			activityTimeDelta = 0;
		}

		try {
			LogHandler.logEvent(chatter, chatMessage);
		} catch (IOException e1) {
			System.out.println("ERROR!: Couldnt write to file!");
			e1.printStackTrace();
		}

		/* Identify and parse commands. */
		if (chatMessage.startsWith("!")) {

			Command command = new Command(chatMessage);

			// TODO: Save commands to file so they can be edited.
			if (command != null) {
//				System.out.println(chatCommand);
				switch (command.name) {

				case "!drawing":
					if (chatter.isMod()) {
						if (command.argumentList.contains("-clear")) {
							drawList.clear();
							return "Drawing list has been cleared.";
						} else if (command.argumentList.contains("-end")) {
							drawList.clear();
							drawingIsActive = false;
							return "Drawing has ended.";
						} else {
							drawList = new ArrayList<User>();
							drawingIsActive = true;
							// TODO: Set argument to change the keyword.
							return "Starting roulette drawing. Type \"" + drawingKeyword
									+ "\" to enter into the drawing.";
						}
					}
					break;

				case "!draw":
					if (chatter.isMod()) {
						if (command.argumentList.contains("-active")) {
							if (activeUserList.isEmpty())
								return "There are no active chatters.";

							Random rng = new Random();
							List<User> list = new ArrayList<User>(activeUserList.keySet());
							winner = list.get(rng.nextInt(list.size()));

							return "Congratulations @" + winner.getName() + "! You won the drawing!";

						} else if (drawingIsActive = true) {
							drawList.remove(winner);
							if (drawList.isEmpty()) {
								return "There are no users in the drawing yet.";
							} else {
								Random rng = new Random();
								winner = drawList.get(rng.nextInt(drawList.size()));

								// TODO: Save the winners chat messages in a txt file to display on stream
								return "Congratulations @" + winner.getName() + "! You won the drawing!";
							}
						} else {
							return "There is no active drawing.";
						}
					}
					break;

				case "!clearactive":
					if (chatter.isMod()) {
						activeUserList.clear();
						return "Active user list cleared.";
					}
					break;

				case "!add":
					if (chatter.isMod()) {
						if (!command.argumentList.isEmpty()) {

							String argument = command.argumentList.get(0).substring(1);

							if (argument.startsWith("!")) {
								try {
									BufferedWriter fileWriter = new BufferedWriter(
											new FileWriter(new File(commandDir), true));
									fileWriter.newLine();
									fileWriter.write(command.name + ":" + command.body);
									fileWriter.close();
									simpleCommands.put(command.name, command.body);
								} catch (IOException e) {
									e.printStackTrace();
									return "There was en error in trying to write command to file.";
								}
								return "Command " + argument + " has been added.";
							} else {
								return "\"" + argument + "\"" + " is not a valid command name.";
							}
						} else {
							return "Syntax: \"!add -!command Text.";
						}
					}
					break;

				case "!edit":
					if (chatter.isMod()) {
						if (!command.argumentList.isEmpty()) {

							String argument = command.argumentList.get(0).substring(1);
							if (simpleCommands.containsKey(argument)) {
								try {
									BufferedReader reader = new BufferedReader(new FileReader(commandDir));

									String currentLine = reader.readLine();
									String fullText = "";

									while (currentLine != null) {
										if (currentLine.startsWith(argument)) {
											fullText += argument + ":" + command.body + "\n";
										} else {
											fullText += currentLine + "\n";
										}
										currentLine = reader.readLine();
									}

									BufferedWriter writer = new BufferedWriter(new FileWriter(commandDir));
									writer.write(fullText);
									writer.close();
									reader.close();

								} catch (IOException e) {
									e.printStackTrace();
									return "There was an error reading from the command file.";
								}
								simpleCommands.put(argument, command.body);
								return "Command: \"" + argument + "\" was edited.";
							}
						} else {
							return "Please specify which command to edit. Syntax: \"!edit -!command Text.";
						}
					}
					break;

				case "!remove":
					if (chatter.isMod()) {
						if (!command.argumentList.isEmpty()) {
							String argument = command.argumentList.get(0).substring(1);
							if (simpleCommands.containsKey(argument)) {
								try {
									BufferedReader reader = new BufferedReader(new FileReader(commandDir));

									String currentLine = reader.readLine();
									String fullText = "";

									while (currentLine != null) {
										if (!currentLine.startsWith(argument)) {
											fullText += currentLine + "\n";
										}
										currentLine = reader.readLine();
									}

									BufferedWriter writer = new BufferedWriter(new FileWriter(commandDir));
									writer.write(fullText);
									writer.close();
									reader.close();

								} catch (IOException e) {
									e.printStackTrace();
									return "There was an error reading from the command file.";
								}
								simpleCommands.remove(argument);
								return "Command: \"" + argument + "\" was removed.";
							} else {
								return "That command does not exist.";
							}
						} else {
							return "Syntax: \"!remove -!command";
						}
					}
					break;
				}
			}

			/* Entry into an active drawing. Default keyword is "!enter" */
			if (command.name.equals(drawingKeyword)) {
				if (!chatter.isBroadcaster()) {
					drawList.add(chatter);
				}
			}

			/* Handle simple response commands. */
			if (simpleCommands.containsKey(command.name)) {
				return simpleCommands.get(command.name);
			}
		}
		/*
		 * Reaching this part means the parsed message was not in any list of commands
		 */
		return null;

	}

	private void loadCommands(File file) {
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String[] line = scan.nextLine().split(":", 2);
				simpleCommands.put(line[0], line[1]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("ERROR: Could not read from commands.txt!");
			e.printStackTrace();
		}

	}
}
