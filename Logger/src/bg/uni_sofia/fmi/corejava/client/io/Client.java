package bg.uni_sofia.fmi.corejava.client.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
	
	/**
	 * Initialize the client
	 * 
	 * @param host The host of the EchoServer
	 * @param port The port of the EchoServer
	 */
	public Client(String host, int port) {
		remoteHost = host;
		remotePort = port;
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
			
		try (Socket socket = new Socket(remoteHost, remotePort);
				//BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // 4etem ot tuk
				PrintWriter out = new PrintWriter(socket.getOutputStream()); // pi6em tuk
				//File file = new File("source.txt"); TODO
				Scanner console = new Scanner(System.in);
				) {
			System.out.println("Client " + socket + " connected to server");

			// Start a separate thread for reading the response from the server
			//EchoReaderThread reader = new EchoReaderThread(socket);
			//reader.setDaemon(true);
			//reader.start();
			out.print(java.lang.management.ManagementFactory.getRuntimeMXBean().getName());
			out.flush();
			
			String consoleInput = null;
			while ((consoleInput = console.nextLine()) != null) {
				
				// Stop the client
				if ("q".equalsIgnoreCase(consoleInput.trim())) {
					System.out.println("Client stopped");
					return;
				}
				// Send to the server
				if (serverIsAvailable()) {
					out.print(consoleInput);
					out.flush();
					
					// Read the response from the server
					//String response = in.readLine();
					String response = "Message sent.";
					
					// Write to the end user
					System.out.println(response);
				}
				else {
					throw new ServerIsDownException("(Re)connection attempt: ", consoleInput);
				}
			}
		} catch (ServerIsDownException e) {
			System.out.println(e.getMessage());
			
		} 
		catch (IOException e) {
			System.err.println("An error has occured. " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) throws InterruptedException {
		Client ec = new Client(Utility.SERVER_NAME, Utility.SERVER_PORT);
		ec.start();
	}

}
