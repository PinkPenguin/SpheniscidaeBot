package root;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LogHandler {
	private static File chatLog;
	private static FileWriter writer;
	private static LocalDateTime now;
	private static DateTimeFormatter dtFormat;

	private LogHandler(File file) throws IOException {
//		chatLog = file;
//		writer = new FileWriter(file, true);
//
//		dtFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//
//		// now = LocalDateTime.now();
//		writer.append("\n[" + dtFormat.format(LocalDateTime.now()) + "]  " + "[BOT STARTED] \n\n");
//		writer.flush();
	}

	public static void init() throws IOException {
		writer = new FileWriter(chatLog, true);

		dtFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		writer.append("\n[" + dtFormat.format(LocalDateTime.now()) + "]  " + "[BOT STARTED] \n\n");
		writer.flush();
	}

	public static void setFile(File file) {
		chatLog = file;
	}

	// TODO: Again, figure out how to fix servermessages being logged. THis has to
	// do with parsing those messages in the first place
	public static void logEvent(User user, String message) throws IOException {
//		System.out.println("[" + dtFormat.format(now) + "] " + user.getName() + ": " + message + "\n");
		writer.append("[" + dtFormat.format(LocalDateTime.now()) + "] " + user.getName() + ": " + message + "\n");
		writer.flush();
	}
}
