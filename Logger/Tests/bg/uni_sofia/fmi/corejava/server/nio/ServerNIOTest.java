package bg.uni_sofia.fmi.corejava.server.nio;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.junit.Test;

import bg.uni_sofia.fmi.corejava.client.io.Client;
import bg.uni_sofia.fmi.corejava.logger.Logger;
import bg.uni_sofia.fmi.corejava.logger.LoggerWithArchival;
import bg.uni_sofia.fmi.corejava.stuff.Constants;
import bg.uni_sofia.fmi.corejava.stuff.Utility;

public class ServerNIOTest {
	private Thread server;
	private Thread client;

	@Test
	public void startAndStop() {
		try (Logger logger = new LoggerWithArchival(Constants.LOG_FILE, Constants.ARCHIVE_FILE);
				ServerNIO es = new ServerNIO(Constants.SERVER_PORT, logger)) {
			File source = null;
			source = new File(Constants.CLIENT_SOURCE_FILE.toString());
			try (Socket socket = new Socket(Constants.SERVER_NAME, Constants.SERVER_PORT);
					PrintWriter out = new PrintWriter(socket.getOutputStream());
					Scanner console = new Scanner(source);
					){
				
				server = new Thread() {
				    public void run() {
				    	Utility.reflectionCall( es, ServerNIO.class, "start");
				    };
				};
				server.start();
				
				Client ec = new Client(Constants.SERVER_NAME, Constants.SERVER_PORT, socket, out, console, true);
				client = new Thread() {
				    public void run() {
				    	Utility.reflectionCall( ec, Client.class, "start");
				    };
//				    public void sendMessage(String message) {
////		                	Class partypes[] = new Class[1];
////		    	            partypes[0] = Message.class;
////		    	            Object arglist[] = new Object[1];
////		    	            arglist[0] = new Message(new User(Utility.SERVER_NICKNAME), Utility.SHUT_DOWN_SERVER);
////		    				Utility.reflectionCallWithArgs(es, "ServerNIO", "write", partypes, arglist);
////		                	Utility.reflectionCallWithArgs(classInstance, cls, methodName, partypes, arglist);( ec, Client.class, "start");
//				    };
//				    public void stopClient() {
//				    	//Utility.reflectionCall( ec, Client.class, "start");
//				    };
				};
				client.start();
				
				client.join();
				server.join();
				
				assertTrue(!client.isAlive());
				assertTrue(!server.isAlive());
			} catch (NullPointerException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
            		
            
            //
		} catch (Exception e) {
			System.out.println("An error has occured");
			e.printStackTrace();
		}
	}

}
