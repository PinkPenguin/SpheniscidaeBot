package root;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SBotMain {
	
	public static String BOT_USERNAME = "ERROR";
	public static String CHANNEL_NAME = "ERROR";
	public static String OAUTH_TOKEN = "ERROR";
	public static String CLIENT_ID = "ERROR";
	public static String CLIENT_SECRET = "ERROR";
	
	private static PrintWriter out;
	private static Scanner in;
	

	public static void main(String[] args) {
		
		try {
			Scanner tokenScanner = new Scanner (new File("tokens.txt"));
			while(tokenScanner.hasNextLine()) {
				String line[] = tokenScanner.nextLine().split(":", 2);
				if(line[0].startsWith("BOT_USERNAME")) {
					BOT_USERNAME = line[1];
				}
				else if(line[0].startsWith("CHANNEL_NAME")){
					CHANNEL_NAME = line[1];
				}
				else if(line[0].startsWith("OAUTH_TOKEN")) {
					OAUTH_TOKEN = line[1];
				}
				else if(line[0].startsWith("CLIENT_ID")) {
					CLIENT_ID = line[1];
				}
				else if(line[0].startsWith("CLIENT_SECRET")) {
					CLIENT_SECRET = line[1];
				}
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: tokens.txt file not found!");
			e1.printStackTrace();
		}
		
		//TODO: Move all this code into seperate class, just here for now to get things going fast
		try {
			Socket socket = new Socket("irc.twitch.tv", 6667);

			out = new PrintWriter(socket.getOutputStream(), true);
			in = new Scanner(socket.getInputStream());
			
			
			write("PASS ", OAUTH_TOKEN);
			write("NICK ", BOT_USERNAME);
			write("JOIN ", CHANNEL_NAME);
			
			while(in.hasNext()) {
				String serverMessage = in.nextLine();
				System.out.println("<<< " + serverMessage);
				
				if (serverMessage.startsWith("PING")) {
					String pingContents = serverMessage.split(" ", 2)[1];
					write("PONG ", pingContents);					
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
		System.out.println(">>> " + fullMessage);
		out.print(fullMessage + "\r\n");
		out.flush();
	}

}
