package edu.umd.cs.hcil.twitterreplay.apps;

import edu.umd.cs.hcil.twitterreplay.DateParser;
import edu.umd.cs.hcil.twitterreplay.GzippedFileReader;
import edu.umd.cs.hcil.twitterreplay.ProducerTask;
import edu.umd.cs.hcil.twitterreplay.senders.Sender;
import edu.umd.cs.hcil.twitterreplay.senders.SocketSender;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class SocketApp 
{
    private static final Logger LOGGER = 
            Logger.getLogger(SocketApp.class.getName());
    
    private static Date getFirstTime(GzippedFileReader reader) {
        Date firstTime = null;
        
        try {
            reader.open();

            for ( String jsonString : reader ) {
                firstTime = DateParser.getDateFromJson(jsonString);
                
                break;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error getting first time: {0}", e);
        }
        
        return firstTime;
    }
    
    public static void main( String[] args )
    {
        if ( args.length < 3 ) {
            System.err.println("Usage: App <host> <port> <input_file>");
            
            System.exit(-1);
        }
        
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String filename = args[2];
        
        LOGGER.log(Level.INFO, "Sending on socket: {0}:{1}", new Object[]{host, port});
        
        try {
            SocketSender sender = new SocketSender(host, port);
            
            LOGGER.log(Level.INFO, "Waiting for connection...");
            sender.accept();
            
            LOGGER.log(Level.INFO, "Reading File: {0}", filename);
            createProducer(filename, sender);
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Failed to create socket...");
            LOGGER.log(Level.SEVERE, ioe.getMessage());
        }
    }
    
    public static void createProducer(String filename, Sender s) throws IOException {
        GzippedFileReader reader = new GzippedFileReader(filename);
        Date firstTime = getFirstTime(reader);
        reader.close();
        
        reader = new GzippedFileReader(filename);
        reader.open();
        
        ProducerTask task = new ProducerTask(firstTime, 1, Calendar.SECOND, reader, s);
        
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(4);
        scheduler.scheduleAtFixedRate(task, 1, 1, TimeUnit.SECONDS);        
    }
}
