package bg.uni_sofia.fmi.corejava.server.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;

import bg.uni_sofia.fmi.corejava.logger.Logger;
import bg.uni_sofia.fmi.corejava.logger.LoggerWithArchival;
import bg.uni_sofia.fmi.corejava.stuff.MessageTransaction;
import bg.uni_sofia.fmi.corejava.stuff.User;
import bg.uni_sofia.fmi.corejava.stuff.Utility;

/**
 * An EchoServer developed with the non-blocking NIO API
 * The server has two functions:
 *  - To accept new clients
 *  - To echo back everything a client sends
 *
 */
public class ServerNIO implements AutoCloseable {
	
	//public static final int SERVER_PORT = 4444;
	
	private SelectionKey lastKey;
	private Selector selector;
	private ByteBuffer echoBuffer;
	private Logger logger;
	private boolean shutDown = false;

	public ServerNIO(int port, Logger logger) throws IOException {
		// Create a new selector
		selector = Selector.open();

		// Open a listener on each port, and register each one with the selector
		ServerSocketChannel ssc = ServerSocketChannel.open(); // Receiving
		ssc.configureBlocking(false);
		ServerSocket ss = ssc.socket();  // We become the one who sends messages, sends
		InetSocketAddress address = new InetSocketAddress(port);
		ss.bind(address);

		ssc.register(selector, SelectionKey.OP_ACCEPT);
		
		echoBuffer = ByteBuffer.allocate(1024);

		System.out.println("EchoServer NIO listening on port " + port);
		
		this.logger = logger;
		
	}

	private void start() throws IOException {
		while (this.shutDown != true) {
			int num = selector.select();
			
			if (num == 0) {
				continue;
			}
			
			Set<SelectionKey> selectedKeys = selector.selectedKeys();
//			if (selectedKeys.contains(this.lastKey)) {
//				write(lastKey);
//				continue;
//			}
			Iterator<SelectionKey> it = selectedKeys.iterator();

			while (it.hasNext()) {
				SelectionKey key = it.next();
				try {
					if (key.isAcceptable()) {
						this.accept(key);
						//key.attach(new User(bareRead(key)));
						
					} else if (key.isReadable()) {
						write(key);
					}
				} catch(IOException e) {
					System.err.println("An error has occured. " + e.getMessage());
					e.printStackTrace();
				}
				it.remove();
			}
		}
	}
	
	private void stop() {
		this.shutDown = true;
	}


	/**
	 * Accept a new connection
	 * 
	 * @param key The key for which an accept was received
	 * @throws IOException In case of problems with the accept
	 */
	private void accept(SelectionKey key) throws IOException {
		// Accept the new connection
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		
		SocketChannel sc = ssc.accept();
		sc.configureBlocking(false);

		// Add the new connection to the selector
		sc.register(selector, SelectionKey.OP_READ);
	}
	
	private void write(SelectionKey key) {
		MessageTransaction message = this.read(key);
		this.lastKey = key; // TODO: not used
		if (message!=null && message.getContent()!=null) {
			//logClientMessageToConsole(message);
			this.write(message);
		}
	}

	/**
	 * Read data from a connection
	 * 
	 * @param key The key for which a data was received
	 */
	private MessageTransaction read(SelectionKey key) {
		MessageTransaction mt = null;
		String fullMessage = bareRead(key);

		String[] messages = null;
		if (fullMessage != null) {
			messages = splitMessages(fullMessage);
		}
		
		User user = null;
		user = (User)key.attachment();
		if (user == null && messages != null) {
			
			String clientName = messages[0];
			if (clientName!=null) {
				System.out.println("Client " + clientName + " connected.");
				user = new User(clientName);
				key.attach(user);
			}
		}
		
		if (messages != null) {
			mt = new MessageTransaction(user, messages);
		}
		
		return mt;
	}

	@Deprecated
	private String bareRead(SelectionKey key) {
		SocketChannel sc = (SocketChannel) key.channel();
		try {
			// Echo data
			echoBuffer.clear();  // pos = 0; pos is the positioon in the buffer from where we start reading
			int numBytes = sc.read(echoBuffer);  // puts the content from the channel into the buffer
			if (numBytes == -1) {  // TODO: extract as a constant
				// The channel is broken. Close it and cancel the key
				throw new IOException("Broken channel");
			}

			echoBuffer.flip();  // finds the limit of the data that was read

			//sc.write(echoBuffer);  // reads from the buffer from the pos to the limit
			//String message = new String(echoBuffer.array());
			String message = new String(echoBuffer.array(),
					echoBuffer.position(),
					echoBuffer.remaining());
			
			//System.out.println("Client " + sc + " wrote " + numBytes + " " + message);
			return message;
		} catch (IOException ioe) {
			// The channel is broken. Close it and cancel the key
			try {
				sc.close();
			} catch (IOException e) {
				e.printStackTrace();
				// Nothing that we can do
			}
			key.cancel();
			return null; // constant
		}
	}
	
	private String[] splitMessages(String message) {
		return message.split(Utility.NEW_LINE);
	}
	
	@Deprecated
	private boolean write(String message) {
        try {
			logger.log(message);
			//TODO: author
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
	}

	private boolean write(MessageTransaction messages) {
        try {
        	for (String message : messages.getContent()) {
        		logger.log(message, messages.getAuthor().toString());
        		
        		logClientMessageToConsole(messages.getAuthor(), message);
        		
    			if (message.equals(Utility.DISCONNECT_CLIENT)) {
    				System.out.println("Client " + messages.getAuthor() + " disconnected.");
    			}
    			
    			if (message.trim().equals(Utility.SHUT_DOWN_SERVER)) {
    				System.out.println("Server is shutting down.");
    				this.stop();
    				break;
    			}
    			//TODO: author
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
	}

	private void logClientMessageToConsole(User author, String message) {
		System.out.println("Client " + author + " wrote: " + message);
	}
	
	@Override
	public void close() throws Exception {
		if (selector != null) {
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
				// Nothing that we can do
			}
		}
	}

	public static void main(String args[]) throws Exception {
		try (Logger logger = new LoggerWithArchival(Utility.LOG_FILE, Utility.ARCHIVE_FILE);
				ServerNIO es = new ServerNIO(Utility.SERVER_PORT, logger)) {
			es.start();
		} catch (Exception e) {
			System.out.println("An error has occured");
			e.printStackTrace();
		}
	}
}