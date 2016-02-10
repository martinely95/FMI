package bg.uni_sofia.fmi.corejava.stuff;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utility {
	/**
	 * Constants
	 */
	public static final int SERVER_PORT = 10514;
	//public static final String SERVER_NAME = "localhost";
	public static final String SERVER_NAME = "192.168.0.100";
	
	public static final Path LOG_FILE = Paths.get("logs", "current.log");
    public static final Path ARCHIVE_FILE = Paths.get("logs", "archive.zip");
}
