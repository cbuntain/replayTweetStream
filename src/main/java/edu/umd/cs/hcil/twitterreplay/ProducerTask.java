/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umd.cs.hcil.twitterreplay;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;

/**
 *
 * @author cbuntain
 */
public class ProducerTask implements Runnable {
    
    private static final Logger LOGGER = Logger.getLogger(ProducerTask.class.getName());
    
    private Date mCurrentTime;
    private Date mNextTime;
    private Pair<Date, String> mNextItem;
    
    private final GzippedFileReader mInputReader;
    private final DateFormat mDateFormat;
    private final Calendar mCalendar;
    private final int mInterval;
    private final int mIntervalUnit;
    
    public ProducerTask(Date firstTime, int interval, int unit, GzippedFileReader input) {
        
        mInterval = interval;
        mIntervalUnit = unit;
        
        mDateFormat = DateFormat.getDateTimeInstance();
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(firstTime);
        mCalendar.add(mIntervalUnit, mInterval);
        
        mCurrentTime = firstTime;
        mNextTime = mCalendar.getTime();
        
        mNextItem = null;
        
        mInputReader = input;
    }

    public void run() {
        System.out.println("Running with Current Time: " + mDateFormat.format(mCurrentTime));
        
        try {
            // Find all tweets in the current interval
            List<String> messages = new ArrayList<String>();

            if ( mNextItem == null || mNextItem.getLeft().compareTo(mCurrentTime) == 0 ) {

                System.out.println("Checking for more messages...");

                if ( mNextItem != null ) {
                    messages.add(mNextItem.getRight());
                }

                for ( String tweetJson : mInputReader ) {
                    try {
                        Date tweetTime = DateParser.getDateFromJson(tweetJson);

                        if ( tweetTime.compareTo(mCurrentTime) < 0 ) {
                            System.err.println("Out of order tweet:");
                            System.err.println(tweetJson);
                            
                            continue;
//                            throw new Error("Encountered Out-of-Order Tweet: " + tweetJson);
                        }

                        // Is this tweet the same as or AFTER the current time?
                        if ( tweetTime.compareTo(mCurrentTime) == 0 ) {
                            messages.add(tweetJson);
                        } else {
                            mNextItem = new ImmutablePair<Date, String>(tweetTime, tweetJson);

                            break;
                        }
                    } catch (JSONException ex) {
    //                    LOGGER.log(Level.INFO, "JSON Exception: {0}", ex);
    //                    LOGGER.log(Level.INFO, "Tweet JSON: {0}", tweetJson);
                    } catch (ParseException ex) {
                        LOGGER.log(Level.SEVERE, "Parse Exception: {0}", ex);
                        LOGGER.log(Level.SEVERE, "Tweet JSON: {0}", tweetJson);
                    }
                }
            } else {
                LOGGER.log(Level.INFO, "No messages during time: {0}", mDateFormat.format(mCurrentTime));
            }

            // Publish the messages for this interval to Kafka
            System.out.println("Message Count: " + messages.size());
    //        for ( String msg : messages ) {
    //            System.out.println(msg);
    //        }
        } catch ( Exception e ) {
            LOGGER.log(Level.SEVERE, "Unknown Exception: {0}", e);
        }
        
        mCurrentTime = mNextTime;
        mCalendar.add(mIntervalUnit, mInterval);
        mNextTime = mCalendar.getTime();
    }
    
}
