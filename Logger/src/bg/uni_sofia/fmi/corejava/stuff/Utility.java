package bg.uni_sofia.fmi.corejava.stuff;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utility {
	/**
	 * Constants
	 */
	public static final int SERVER_PORT = 10514;
	//public static final String SERVER_NAME = "localhost";
	public static final String SERVER_NAME = "192.168.0.102";
	
	public static final String NEW_LINE = "\n";
	public static final String SHUT_DOWN_SERVER = "shut down";
	public static final String DISCONNECT_CLIENT = "q";
	
	public static final Path LOG_FILE = Paths.get("logs", "current.log");
    public static final Path ARCHIVE_FILE = Paths.get("logs", "archive.zip");
}
