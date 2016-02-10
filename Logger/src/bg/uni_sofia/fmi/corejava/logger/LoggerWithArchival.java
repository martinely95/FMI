package bg.uni_sofia.fmi.corejava.logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class LoggerWithArchival
    implements Logger
{
    protected static final SimpleDateFormat tsFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private static final long MAX_LOG_FILE_SIZE = 1 * 1024 * 1024;
    private static final String ARCHIVES_LOG_FILES_PREFIX = "log.";
    
    protected Path logFile;
    protected PrintWriter logFileWriter;
    private Path archiveFile;

    
    public LoggerWithArchival(Path logFile, Path archiveFile)
        throws IOException
    {
        Files.createDirectories(logFile.getParent());
        Files.createDirectories(archiveFile.getParent());
        
        this.logFile = logFile;
        this.open();
        this.archiveFile = archiveFile;

    }
    
    protected void open()
        throws IOException
    {
        FileOutputStream logFileOS = new FileOutputStream(this.logFile.toString(), true);
        this.logFileWriter = new PrintWriter(logFileOS);
    }
    
    public void close()
        throws IOException
    {
        this.logFileWriter.close();
    }
    
    public void log(String msg)
            throws IOException
        {
            Calendar now = Calendar.getInstance();
            this.preHook(now); //Used in the inherited classes
            
            String nowAsString = tsFormat.format(now.getTime());
            this.logFileWriter.println(nowAsString + " " + msg);
            
            this.archiveIfNecessary();
        }
    
    public void log(String msg, String author)
        throws IOException
    {
        Calendar now = Calendar.getInstance();
        this.preHook(now); //Used in the inherited classes
        
        String nowAsString = tsFormat.format(now.getTime());
        this.logFileWriter.println(nowAsString + " " + author + ": " + msg);
        
        this.archiveIfNecessary();
    }
    
    protected void preHook(Calendar now)
        throws IOException
    {
    	//TODO
    }
    
    private void archiveIfNecessary()
        throws IOException
    {
        long currentSize = Files.size(this.logFile);
        
        if (currentSize > MAX_LOG_FILE_SIZE)
        {
            this.close();
            
            Map<String, String> env = new HashMap<String, String>();
            env.put("create", "true");
            
            String uri = this.archiveFile.toAbsolutePath().toUri().toString();
            uri = "jar:" + uri;
            
            try (FileSystem zipFS = FileSystems.newFileSystem(URI.create(uri), env))
            {
                Calendar now = Calendar.getInstance();
                String year = Integer.toString(now.get(Calendar.YEAR));
                String month = Integer.toString(now.get(Calendar.MONTH) + 1);
                String day = Integer.toString(now.get(Calendar.DAY_OF_MONTH));
                
                Path archiveDir = zipFS.getPath(year, month, day);
                Files.createDirectories(archiveDir);
                
                int maxNum = 0;
                try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(archiveDir, ARCHIVES_LOG_FILES_PREFIX + "*"))
                {
                    for (Path oldLogFile: dirStream)
                    {
                        String numAsString = oldLogFile.getFileName().toString().substring(ARCHIVES_LOG_FILES_PREFIX.length());
                        int num = Integer.parseInt(numAsString);
                        if (num > maxNum) 
                        {
                            maxNum = num;
                        }
                    }
                }
                
                int newNum = maxNum + 1;
                String newName = ARCHIVES_LOG_FILES_PREFIX + newNum;
                
                Path newArchivedLogFile = archiveDir.resolve(newName);
                Files.move(this.logFile, newArchivedLogFile);
            }

            this.open();
        }
    }

    public String getFirstMessageAfter(Calendar moment)
            throws IOException
    {
        throw new UnsupportedOperationException();
    }
}
