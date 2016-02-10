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
import bg.uni_sofia.fmi.corejava.stuff.Message;
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
	
	private void write(SelectionKey key) {
		Message message = this.read(key);
		this.lastKey = key; // TODO: not used
		if (message!=null) {
			this.write(message);
		}
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

//		System.out.println("Client " + sc + " connected");
		


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
			
			System.out.println("Client " + sc + " wrote " + numBytes + " " + message);
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
	
	/**
	 * Read data from a connection
	 * 
	 * @param key The key for which a data was received
	 */
	private Message read(SelectionKey key) {
		if (key.attachment() == null) {
			key.attach(new User(bareRead(key)));
			return null;
		}
		String message = bareRead(key);
		return new Message((User)key.attachment(), message);
	}
	
	@SuppressWarnings("unused")
	private static void reset()
	        throws IOException
    {
        Files.deleteIfExists(Utility.LOG_FILE);
        Files.deleteIfExists(Utility.ARCHIVE_FILE);
        //Files.deleteIfExists(Paths.get(Utility.LOG_FILE.toString() + ".idx"));
        Files.createDirectories(Utility.LOG_FILE.getParent());
        Files.createDirectories(Utility.ARCHIVE_FILE.getParent());
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
	
	private boolean write(Message message) {
        try {
			logger.log(message.getContent(), message.getAuthor().toString());
			if (message.getContent().equals("shut down")) {
				this.shutDown = true;
			}
			//TODO: author
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        return true;
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