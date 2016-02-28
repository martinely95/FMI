package bg.uni_sofia.fmi.corejava.stuff;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Constants {
	/**
	 * Constants
	 */
	public static final int SERVER_PORT = 10514;
	public static final String LOCALHOST = "localhost";
	public static final String SERVER_NAME = "localhost";
	
	public static final String NEW_LINE = "\n";
	public static final String SHUT_DOWN_SERVER = "shut down";
	public static final String DISCONNECT_CLIENT = "q";
	public static final String SERVER_NICKNAME = "Server";

    public static final String MESSAGE_SENT = "Message sent.";
    public static final String CLIENT_STOPPED = "Client stopped.";

    public static final String CLIENT_NAME_SENT = "Client name sent: ";
	
	public static final Path LOG_FILE = Paths.get("logs", "current.log");
    public static final Path ARCHIVE_FILE = Paths.get("logs", "archive.zip");
    public static final Path CLIENT_SOURCE_FILE = Paths.get("Text files", "clientSource.txt");
    
    public static final int NUMBER_OF_RETRIES = 10;
    public static final String RECONNECTION_MESSAGE = "(Re)connection attempt: ";
    
    public static final int BUFFER_SIZE = 1024;
    
	
}
