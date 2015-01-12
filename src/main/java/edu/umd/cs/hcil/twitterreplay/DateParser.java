/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umd.cs.hcil.twitterreplay;

import static java.lang.String.format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author cbuntain
 */
public class DateParser {
    public static final String CREATED_AT_FORMAT = "EEE MMM d HH:mm:ss z yyyy";
    public static final SimpleDateFormat sdf = new SimpleDateFormat(CREATED_AT_FORMAT, Locale.US);
    
    // Static init
    {
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
    }
    
    public static Date getDateFromJson(String jsonString) throws JSONException, ParseException {
        JSONObject status = new JSONObject(jsonString);
        String dateString = status.getString("created_at");
        
        return parseCreatedAt(dateString);
    }
    
    public static Date parseCreatedAt(String date) throws ParseException {
        
        return sdf.parse(date);
    }
}
