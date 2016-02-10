package bg.uni_sofia.fmi.corejava.logger;

import java.io.IOException;
import java.util.Calendar;


public interface Logger
    extends AutoCloseable
{
	void log(String msg)
	        throws IOException;
	
    void log(String msg, String author)
        throws IOException;
    
    String getFirstMessageAfter(Calendar moment)
        throws IOException;
}
