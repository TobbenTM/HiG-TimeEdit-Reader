package com.tobbentm.higreader;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Tobias on 26.08.13.
 */
public class Network {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void timetable(String ids, AsyncHttpResponseHandler handler){
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();

        // Generating date strings for setting search period
        String startDate = df.format(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, 14); // Add 14 days
        String endDate = df.format(cal.getTime());

        final String baseURL = "https://web.timeedit.se/hig_no/db1/open/r.csv?sid=3&h=t&p="+startDate+".x%2C"+endDate+".x&objects=";
        final String endURL = "&ox=0&types=0&fe=0&l=en&g=f";

        client.get(baseURL + ids + endURL, handler);
    }

    public static void search(String term, String type, String[] searchArray, AsyncHttpResponseHandler handler){
        final int iType;

        // Really ugly way to determine type, but IDRC...
        if(type.contains(searchArray[0])){
            iType = 182;    // Class
        }else if(type.contains(searchArray[1])){
            iType = 183;    // Course
        }else if(searchArray.length > 2 && type.contains(searchArray[2])){
            iType = 184;    // Lecturer
        }else if(searchArray.length > 2 && type.contains(searchArray[3])){
            iType = 185;    // Room
        }else{
            iType = 0;
        }

        final String baseURL = "https://web.timeedit.se/hig_no/db1/timeedit/p/open/objects.html?max=15&partajax=t&l=en&sid=3&types=" + iType + "&search_text=";

        // TODO: Check for more illegal characters?
        client.get(baseURL + term.replaceAll(" ", "%20"), handler);
    }

    public static void search(String term, String type, AsyncHttpResponseHandler handler){
        final String baseURL = "https://web.timeedit.se/hig_no/db1/timeedit/p/open/objects.html?max=15&partajax=t&l=en&types=" + type + "&search_text=";
        client.get(baseURL + term, handler);
    }
}
