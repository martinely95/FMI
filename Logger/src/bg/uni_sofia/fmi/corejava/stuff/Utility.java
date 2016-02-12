package bg.uni_sofia.fmi.corejava.stuff;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utility {
	
    public static void resetFiles(Path logFile, Path archiveFile) throws IOException {
        Files.deleteIfExists(logFile);
        Files.deleteIfExists(archiveFile);
        //Files.deleteIfExists(Paths.get(Utility.LOG_FILE.toString() + ".idx"));
        Files.createDirectories(logFile.getParent());
        Files.createDirectories(archiveFile.getParent());
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
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

    public static void callStackOverflowForHelp(Exception e) {
    	try {
			Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome http://stackoverflow.com/search?q=[java]+" + e.getMessage()});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
}
