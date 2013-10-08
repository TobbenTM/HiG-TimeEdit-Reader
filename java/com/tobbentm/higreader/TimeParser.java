package com.tobbentm.higreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobias on 26.08.13.
 * Welcome to my halv-assed attempt at an parser
 */
public class TimeParser {

    public static String[][] timetable(String html){
        String[] split1 = html
                .replaceAll("<span class=\"tesprite tesprite-floatright tesprite-new\"></span>", "")
                .split("/table");
        String[] split2 = split1[0].split("changeDateLink headline leftRounded t .*\">");

        String[][] results = new String[split2.length-1][6];
        ArrayList<ArrayList<String>> master = new ArrayList<ArrayList<String>>();

        String currentdate = "";

        for(int i = 1; i < split2.length -1; i++){
            currentdate = split2[i].split("</td>")[0].replaceAll(" Today", "");
            String[] split3 = split2[i].split("</tr>");
            //Log.d("PARSER", currentdate);
            master.add(dateEntry(currentdate));
            for(int j = 1; j < split3.length -1; j++){
                ArrayList<String> inner = new ArrayList<String>();
                String[] data = tagData(split3[j]);
                inner.add(currentdate);
                for(String s : data){
                    inner.add(s);
                }
                master.add(inner);
            }
        }
        //Log.d("MASTER", master.toString());
        return dimensionalPortal(master);
    }

    public static String[][] search(String html, String term){
        //Log.d("PARSING", "Starting parser");
        List<String> id = new ArrayList<String>();
        List<String> name = new ArrayList<String>();

        //The reason for removing whitespace is to better readability when debugging
        //The reason for splitting at data-id="-1" is that there is no valuable data
        // after it, and it would just cause problems.
        //Still not bug-free though, and should be replaced with a better regex-based
        // solution.
        String[] split = html.replace(" ", "").split("data-id=\"-1\"");
        String[] parsed = split[0].split("\"");

        //Gather valuable data into the two arrays
        for (String s : parsed){
            if(s.contains(".182") || s.contains(".183") || s.contains(".184") || s.contains(".185")){
                id.add(s);
            }else if(s.toUpperCase().contains(term.toUpperCase()) && !s.contains(">")){
                name.add(s);
            }
        }

        String[][] result = new String[id.size()][2];

        Log.d("HIG.SEARCH.ARRAY", name.toString() + id.toString());

        //Join the two arrays into one 2D array
        for(int i=0; i < id.size(); i++){
            result[i][0] = id.get(i);
            result[i][1] = name.get(i);
        }
        return result;
    }

    private static String[] tagData(String html){
        String[] split = html.replaceAll("(?m:</tr>|^[\\s]*)", "").split("[\\s]*<[^<]+?>");
        List<String> tagdata = new ArrayList<String>();
        //Log.d("TAG DATA \t", Arrays.toString(split));
        for(int i = 4; i < split.length -4; i += 1){
            if(i == 4)tagdata.add(split[i].replace(" ", "\n"));
            if(i == 6 || i == 8 || i == 10 || i == 12) tagdata.add(split[i]);
        }
        String[] tagdata2 = new String[tagdata.size()];
        tagdata2 = tagdata.toArray(tagdata2);
        return tagdata2;
    }

    private static String[][] dimensionalPortal(ArrayList<ArrayList<String>> arraylist){
        final int size = arraylist.size();
        String[][] sarr = new String[size][];
        for(int i = 0; i < size; i++) {
            ArrayList<String> innerlist = arraylist.get(i);
            final int innerSize = innerlist.size();
            sarr[i] = new String[innerSize];
            for(int j = 0; j < innerSize; j++) {
                sarr[i][j] = innerlist.get(j);
            }
        }
        return sarr;
    }

    private static ArrayList<String> dateEntry(String date){
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(date);
        arr.add("");
        arr.add("HIGREADER.newDate");
        arr.add("");
        arr.add("");
        arr.add("");
        return arr;
    }

}
