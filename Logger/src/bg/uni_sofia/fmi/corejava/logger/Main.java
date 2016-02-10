package bg.uni_sofia.fmi.corejava.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;


public class Main
{
    private static final Path logFile = Paths.get("logs", "current.log");
    private static final Path archiveFile = Paths.get("logs", "archive.zip");

    
    public static void main(String[] args)
        throws Exception
    {
        testArchival();
        testLinearSearch();
        testBinarySearch();
    }
    
    private static void reset()
        throws IOException
    {
        Files.deleteIfExists(logFile);
        Files.deleteIfExists(archiveFile);
        Files.deleteIfExists(Paths.get(logFile.toString() + ".idx"));
        Files.createDirectories(logFile.getParent());
        Files.createDirectories(archiveFile.getParent());
    }
    
    private static void testArchival()
        throws Exception
    {
        reset();
        
        try (Logger logger = new LoggerWithArchival(logFile, archiveFile))
        {
            for (int i = 0; i < 100000; i++)
            {
                logger.log("Alabala " + i);
            }
        }
    }

    private static void testLinearSearch()
        throws Exception
    {
        reset();
        
        try (Logger logger = new LoggerWithLinearSearch(logFile, archiveFile))
        {
            Calendar moment = null;
            
            for (int i = 0; i < 10000; i++)
            {
                if (i == 5000)
                {
                    moment = Calendar.getInstance();
                }
                logger.log("Portokala " + i);
            }
            
            long t0 = System.nanoTime();
            String msg = logger.getFirstMessageAfter(moment);
            long t1 = System.nanoTime();
            long t = t1 - t0;
            System.out.println(msg);
            System.out.println("" + t + " ns");
            System.out.println(moment.get(Calendar.MILLISECOND));
        }
    }

    private static void testBinarySearch()
            throws Exception
        {
            reset();
            
            try (Logger logger = new LoggerWithBinarySearch(logFile, archiveFile))
            {
                Calendar moment = null;
                
                for (int i = 0; i < 10000; i++)
                {
                    if (i == 5000)
                    {
                        moment = Calendar.getInstance();
                    }
                    logger.log("Portokala " + i);
                }
                
                long t0 = System.nanoTime();
                String msg = logger.getFirstMessageAfter(moment);
                long t1 = System.nanoTime();
                long t = t1 - t0;
                System.out.println(msg);
                System.out.println("" + t + " ns");
                System.out.println(moment.get(Calendar.MILLISECOND));
            }
        }
}
