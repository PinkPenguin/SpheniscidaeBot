package root;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class LogHandler {
	private File chatLog;
	private FileWriter writer;
	private LocalDateTime now;
	private DateTimeFormatter dtFormat;

	public LogHandler(File file) throws IOException {
		this.chatLog = file;
		this.writer = new FileWriter(file, true);

		dtFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

		now = LocalDateTime.now();
		writer.append("\n[" + dtFormat.format(now) + "]  " + "[BOT STARTED] \n\n");
		writer.flush();
	}

	// TODO: Again, figure out how to fix servermessages being logged. THis has to
	// do with parsing those messages in the first place
	public void logEvent(User user, String message) throws IOException {
//		System.out.println("[" + dtFormat.format(now) + "] " + user.getName() + ": " + message + "\n");
		writer.append("[" + dtFormat.format(now) + "] " + user.getName() + ": " + message + "\n");
		writer.flush();
	}
}
