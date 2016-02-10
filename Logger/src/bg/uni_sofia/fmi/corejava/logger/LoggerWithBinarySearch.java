package bg.uni_sofia.fmi.corejava.logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;

public class LoggerWithBinarySearch
    extends LoggerWithArchival
{
    private class IndexElement
    {
        public long timestamp;
        public long offset;
    }
    private static final int INDEX_EL_SIZE = 8 /* timestamp size */ + 8 /* offset size */;
    
    private Path indexFile;
    private SeekableByteChannel indexChannel;

    
    public LoggerWithBinarySearch(Path logFile, Path archiveFile)
            throws IOException
    {
        super(logFile, archiveFile);
    }

    protected void open()
        throws IOException
    {
        super.open();
        this.indexFile = Paths.get(this.logFile.toString() + ".idx");
        this.indexChannel = Files.newByteChannel(this.indexFile,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE);
    }
    
    public void close()
        throws IOException
    {
        super.close();
        this.indexChannel.close();
    }
    
    protected void preHook(Calendar now)
        throws IOException
    {
        long timestamp = now.getTimeInMillis();  // TODO: NANO
        this.logFileWriter.flush();
        long offset = Files.size(this.logFile);
        
        this.indexChannel.position(this.indexChannel.size());
        
        ByteBuffer buf = ByteBuffer.allocate(INDEX_EL_SIZE);
        buf.putLong(timestamp);
        buf.putLong(offset);
        buf.flip();
        
        this.indexChannel.write(buf);
    }
    
    public String getFirstMessageAfter(Calendar moment)
        throws IOException
    {
        long count = this.indexChannel.size() / INDEX_EL_SIZE; // count of rows
        long found = binSearch(moment, 0, count);
        if (found == count)
        {
            return null;  // constant
        }
        
        IndexElement indexEl = this.readIndexElement(found);
        
        try (FileInputStream fis = new FileInputStream(this.logFile.toString());
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader logFileReader = new BufferedReader(isr))
        {
            fis.skip(indexEl.offset);
            return logFileReader.readLine();
        }

    }
    
    private long binSearch(Calendar moment, long left, long right)
        throws IOException
    {
        if (left == right)
        {
            return left;
        }
        
        long middle = (right - left) / 2 + left;
        IndexElement indexEl = this.readIndexElement(middle);
        if (moment.getTimeInMillis() <= indexEl.timestamp)
        {
            return binSearch(moment, left, middle);
        }
        else
        {
            return binSearch(moment, middle + 1, right);
        }
    }
    
    private IndexElement readIndexElement(long number)
        throws IOException
    {
        this.indexChannel.position(number * INDEX_EL_SIZE);
        ByteBuffer buf = ByteBuffer.allocate(INDEX_EL_SIZE);
        this.indexChannel.read(buf);
        buf.flip();
        
        IndexElement result = new IndexElement();
        result.timestamp = buf.getLong();
        result.offset = buf.getLong();
        return result;
    }
}
