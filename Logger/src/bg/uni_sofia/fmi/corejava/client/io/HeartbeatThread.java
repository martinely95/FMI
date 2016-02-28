package bg.uni_sofia.fmi.corejava.client.io;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import bg.uni_sofia.fmi.corejava.stuff.Utility;

public class HeartbeatThread extends Thread {
	
	public void run() {
//        while (tryToReconnect) {
//        	
//        	
//            //send a test signal
//            try {
//                socket.getOutputStream().write(666);
//                sleep(heartbeatDelayMillis);
//            } catch (InterruptedException e) {
//                // You may or may not want to stop the thread here
//                // tryToReconnect = false;
//            } catch (IOException e) {
//                logger.warn("Server is offline");
//                connect(server, port);
//            }
//        }
    }
	
	private boolean serverIsAvailable(Socket s) { 
//		boolean available = false;
		
		return s.isConnected();
//	    try (Socket s = new Socket(Utility.SERVER_NAME, Utility.SERVER_PORT)) {
//	        available = true;
//	    } catch (UnknownHostException e)  { // unknown host 
//	        //available = false;
//        } catch (IOException ex) {
//        	//available = false;
//	    }  catch (NullPointerException e) {
//	    	//available = false;
//	    }
//	    return available;
	}
}
