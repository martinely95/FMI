package bg.uni_sofia.fmi.corejava.stuff;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import bg.uni_sofia.fmi.corejava.server.nio.ServerNIO;

public class Utility {
	/**
	 * Constants
	 */
	public static final int SERVER_PORT = 10514;
	public static final String LOCALHOST = "localhost";
	public static final String SERVER_NAME = "192.168.0.100";
	
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
    
	
    public static void resetFiles(Path logFile, Path archiveFile) throws IOException {
        Files.deleteIfExists(logFile);
        Files.deleteIfExists(archiveFile);
        //Files.deleteIfExists(Paths.get(Utility.LOG_FILE.toString() + ".idx"));
        Files.createDirectories(logFile.getParent());
        Files.createDirectories(archiveFile.getParent());
    }
    
    public static void reflectionCall(Object classInstance, Class cls, String methodName) throws NullPointerException{
    	Method method = null;
		try {
			method = cls.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  // , new Class[] { }
		method.setAccessible(true);
		try {
			method.invoke(classInstance);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace(); 
			// TODO: 
//			java.lang.reflect.InvocationTargetException
//			at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
//			at sun.reflect.NativeMethodAccessorImpl.invoke(Unknown Source)
//			at sun.reflect.DelegatingMethodAccessorImpl.invoke(Unknown Source)
//			at java.lang.reflect.Method.invoke(Unknown Source)
//			at bg.uni_sofia.fmi.corejava.stuff.Utility.reflectionCall(Utility.java:40)
//			at bg.uni_sofia.fmi.corejava.server.nio.ServerNIOTest$1.run(ServerNIOTest.java:27)
//		Caused by: java.nio.channels.ClosedSelectorException
//			at sun.nio.ch.SelectorImpl.lockAndDoSelect(Unknown Source)
//			at sun.nio.ch.SelectorImpl.select(Unknown Source)
//			at sun.nio.ch.SelectorImpl.select(Unknown Source)
//			at bg.uni_sofia.fmi.corejava.server.nio.ServerNIO.start(ServerNIO.java:62)
//			... 6 more

		}
    }

    public static void reflectionCallWithArgs(Object classInstance, Class cls, String methodName, Class[] partypes, Object arglist[]) throws NullPointerException{
    	Method method = null;
		try {
			method = cls.getDeclaredMethod(methodName, partypes);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}  // , new Class[] { }
		method.setAccessible(true);
		try {
			method.invoke(classInstance, arglist);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
    }
}
