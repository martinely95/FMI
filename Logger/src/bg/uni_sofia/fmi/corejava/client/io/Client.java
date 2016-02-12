package bg.uni_sofia.fmi.corejava.client.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Scanner;

import bg.uni_sofia.fmi.corejava.exceptions.ServerIsDownException;
//import bg.uni_sofia.fmi.corejava.exceptions.ServerIsDownException;
import bg.uni_sofia.fmi.corejava.stuff.Utility;


/**
 * This is a client written with the blocking API of java.net.*
 * The client connects to the server and waits for information from the
 * user. When the user enters data it sends it to the server and waits for
 * a response. When the response is received it is printed to the user and the
 * user can enter new data.
 * 
 * Hint: The client can be improved by enabling him to simultaneously read from the
 * console/send to the server and receive information from the server, by using a separate
 * thread for reading from the server and printing the information to the user.
 *
 */
public class Client {
	
	private String remoteHost = null;
	private int remotePort = 0;
	private String myName;
	
	private Socket socket;
	private PrintWriter out;
	private Scanner console;
	
	private boolean readingFromFile;
	
	public boolean isReadingFromFile() {
		return readingFromFile;
	}

	private void setReadingFromFile(boolean readingFromFile) {
		this.readingFromFile = readingFromFile;
	}

	/**
	 * Initialize the client
	 * 
	 * @param host The host of the EchoServer
	 * @param port The port of the EchoServer
	 */
	public Client(String host, int port, Socket socket, PrintWriter out, Scanner console) {
		remoteHost = host;
		remotePort = port;
		myName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
		this.socket = socket;
		this.out = out;
		this.console = console;
		this.setReadingFromFile(false);
	}
	
	public Client(String host, int port, Socket socket, PrintWriter out, Scanner console, boolean readingFromFile) {
		this( host,  port,  socket,  out,  console);
		this.setReadingFromFile(readingFromFile);
	}
	
	/**
	 * check if server is down
	 */
	public static boolean serverIsAvailable() { 
	    try (Socket s = new Socket(Utility.SERVER_NAME, Utility.SERVER_PORT)) {
	        return true;
	    } catch (IOException ex) {
	        /* ignore */
	    }
	    return false;
	}
	
	/**
	 * Start the client.
	 * @throws InterruptedException 
	 */
	public void start() throws InterruptedException {
		try {
			System.out.println("Client " + socket + " connected to server");

			// Start a separate thread for reading the response from the server
			//EchoReaderThread reader = new EchoReaderThread(socket);
			//reader.setDaemon(true);
			//reader.start();
			sendMessage(out, this.myName, Utility.CLIENT_NAME_SENT + this.myName, false);
			
			if (this.isReadingFromFile()) {
				startSendingMessagesFromFile(console, out);
			} else {
				startSendingMessagesFromConsole(console, out);
			}
			
		} catch (ServerIsDownException e) {
			System.out.println(e.getMessage());
			
		} 
	}
	
	private void startSendingMessagesFromConsole(Scanner console, PrintWriter out) throws ServerIsDownException {
		String consoleInput = null;
		while ((consoleInput = console.nextLine()) != null) {
			
			readAndSendMessage(out, consoleInput);
		}
	}
	
	private void startSendingMessagesFromFile(Scanner console, PrintWriter out) throws ServerIsDownException {
		String consoleInput = null;
		while (console.hasNextLine()) {
			consoleInput = console.nextLine();
			readAndSendMessage(out, consoleInput);
		}
	}

	private void readAndSendMessage(PrintWriter out, String consoleInput) throws ServerIsDownException {
		// Stop the client
		if (Utility.DISCONNECT_CLIENT.equalsIgnoreCase(consoleInput.trim())) {
			sendMessage(out, Utility.DISCONNECT_CLIENT, Utility.CLIENT_STOPPED, true);
			return;
		}
		// Send to the server
		if (serverIsAvailable()) {
			//int i = 0;
//				for (; i < 100000; i++) {
				sendMessage(out, consoleInput, Utility.MESSAGE_SENT, true);
//				}
		}
		else {
			throw new ServerIsDownException("(Re)connection attempt: ", consoleInput);
		}
	}
	
	private void sendMessage(PrintWriter out, String consoleInput, String logToConsole, boolean addNewLine) {
		if (addNewLine) {
			consoleInput += Utility.NEW_LINE;
		}
		out.print(consoleInput);
		out.flush();
		System.out.println(logToConsole);
	}

	
	public static void main(String[] args) throws InterruptedException {
		//TODO: read from file
		//InputStream source = System.in;
		//InputStream source = null;
		File source = null;
		try {
			Files.createDirectories(Utility.CLIENT_SOURCE_FILE.getParent());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		source = new File(Utility.CLIENT_SOURCE_FILE.toString());
		try (Socket socket = new Socket(Utility.SERVER_NAME, Utility.SERVER_PORT);
				//BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 4etem ot tuk
				PrintWriter out = new PrintWriter(socket.getOutputStream()); // pi6em tuk
				//File file = new File("source.txt"); TODO
				Scanner console = new Scanner(source);
				){
			//Client ec = new Client(Utility.SERVER_NAME, Utility.SERVER_PORT, socket, out, console);
			Client ec = new Client(Utility.SERVER_NAME, Utility.SERVER_PORT, socket, out, console, true);
			ec.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
