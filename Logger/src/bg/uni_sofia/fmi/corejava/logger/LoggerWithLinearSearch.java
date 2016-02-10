package bg.uni_sofia.fmi.corejava.logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


public class LoggerWithLinearSearch
    extends LoggerWithArchival
{
    public LoggerWithLinearSearch(Path logFile, Path archiveFile)
        throws IOException
    {
        super(logFile, archiveFile);
    }

    public String getFirstMessageAfter(Calendar moment)
        throws IOException
    {
        this.logFileWriter.flush();
        
        try (BufferedReader logFileReader = new BufferedReader(new FileReader(this.logFile.toString())))
        {
            String line;
            while ((line = logFileReader.readLine()) != null)
            {
                try
                {
                    Date lineTimeStamp = tsFormat.parse(line);
                    if (lineTimeStamp.getTime() >= moment.getTimeInMillis())
                    {
                        return line;
                    }
                }
                catch (ParseException e)
                {
                    // This line is corrupted; ignore it.
                }
            }
        }
        return null;
    }
}
