package com.tobbentm.higreader;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tobbentm.higreader.db.DBLectures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Tobias on 13.03.14.
 */
public class LectureArrayAdapter extends ArrayAdapter<DBLectures> {

    private Context context;
    private DBLectures[] list;
    private LayoutInflater inflater;
    private final SimpleDateFormat orgDate = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat newDate = new SimpleDateFormat("EEE dd/MM");
    private final SimpleDateFormat timef = new SimpleDateFormat("HHmm");

    public LectureArrayAdapter(Context context, int resource, ArrayList<DBLectures> objects) {
        super(context, resource, objects);
        this.context = context;
        list = objects.toArray(new DBLectures[objects.size()]);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
     public int getCount(){
        return list!=null ? list.length : 0;
    }

    // ViewHolder to speed up ListViews
    private static class ViewHolder{
        TextView date;
        TextView week;
        TextView name;
        TextView room;
        TextView lecturer;
        TextView time;
        LinearLayout timeContainer;
        LinearLayout textContainer;
    }

    // Updates parent ArrayList with new lectures
    public void notifyDataSetChanged(DBLectures[] lectures) {
        list = lectures;
        clear();
        addAll(Arrays.asList(lectures));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.lecture_list_item, null);
            holder = new ViewHolder();
            holder.date = (TextView) view.findViewById(R.id.text_date);
            holder.week = (TextView) view.findViewById(R.id.text_week);
            holder.name = (TextView) view.findViewById(R.id.text_name);
            holder.room = (TextView) view.findViewById(R.id.text_room);
            holder.lecturer = (TextView) view.findViewById(R.id.text_lecturer);
            holder.time = (TextView) view.findViewById(R.id.text_time);
            holder.timeContainer = (LinearLayout) view.findViewById(R.id.time_container);
            holder.textContainer = (LinearLayout) view.findViewById(R.id.text_container);
            view.setTag(holder);
        }

        bindView(position, view);
        return view;
    }

    private void bindView(int pos, View view) {
        //TODO: Optimize this shit, should not be this much work on one list item

        ViewHolder holder = (ViewHolder) view.getTag();

        // Generating datestamp for current lectures date
        Date date = new Date();
        try {
            date = orgDate.parse(list[pos].get_date());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        // Using said datestamp to generate pretty text
        String currentDate = newDate.format(date);

        // Generating week number for current lecture,
        // only shown on day views
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int weeknr = cal.get(Calendar.WEEK_OF_YEAR);

        // Check if current lecture is today
        if(currentDate.contains(newDate.format(new Date()))){
            currentDate = context.getResources().getString(R.string.timetable_today);
        }

        // True if current 'lecture' flags a new day
        if(list[pos].get_name() != null &&
                list[pos].get_name().contains("HIGREADER.newDate") ){

            holder.timeContainer.setVisibility(View.GONE);
            holder.textContainer.setVisibility(View.GONE);
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(currentDate);

            holder.week.setVisibility(View.VISIBLE);
            holder.week.setText(context.getString(R.string.timetable_week)+weeknr);

            view.setBackgroundResource(R.drawable.list_date_background);

        // True if current 'lecture' flags a available timeslot
        }else if(list[pos].get_name() != null &&
                list[pos].get_name().contains("HIGREADER.clear")){

            holder.timeContainer.setVisibility(View.GONE);
            holder.textContainer.setVisibility(View.GONE);
            holder.week.setVisibility(View.GONE);
            holder.date.setVisibility(View.VISIBLE);

            holder.date.setText("\t" + list[pos].get_time().replaceAll("\n", " ") + "\t" + context.getResources().getString(R.string.timetable_clear));
            view.setBackgroundResource(R.drawable.list_clear_background);

        // Current lecture is a standard lecture
        }else{
            holder.date.setVisibility(View.GONE);
            holder.week.setVisibility(View.GONE);
            holder.timeContainer.setVisibility(View.VISIBLE);
            holder.textContainer.setVisibility(View.VISIBLE);

            // Generate pretty time string for lecture
            String time = list[pos].get_time();
            String startTime, endTime;
            if(time.split("\n").length >= 3){
                startTime = time.split("\n")[0].replace(":", "");
                endTime = time.split("\n")[2].replace(":", "");
            }else{
                startTime = "0000";
                endTime = "0000";
            }

            // Generate timestamps to determine relative time
            date = new Date();
            long currentclk = date.getTime();
            String currenttime = timef.format(new Date());
            try {
                date = timef.parse(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
                date = new Date(0L);
            }
            long startclk = date.getTime();

            holder.name.setText(list[pos].get_name());
            holder.room.setText(context.getResources().getString(R.string.timetable_room) + ":\t\t" + list[pos].get_room());
            holder.lecturer.setText(context.getResources().getString(R.string.timetable_lecturer) + ":\t" + list[pos].get_lecturer());
            holder.time.setText(time);
            view.setBackgroundResource(R.drawable.list_item_background);

            Boolean today = currentDate.contains(context.getResources().getString(R.string.timetable_today));

            // True if lecture is today and lecture is ongoing
            if(today &&
                    Integer.parseInt(startTime) <= Integer.parseInt(currenttime) &&
                    Integer.parseInt(endTime) >= Integer.parseInt(currenttime)){
                holder.name.setTextColor(Color.parseColor("#C21B1B"));

            // True if lecture is today and lecture had passed
            } else if(today &&
                    Integer.parseInt(endTime) < Integer.parseInt(currenttime)){
                holder.name.setTextColor(Color.parseColor("#1ABD1A"));

            // TODO: True if lecture is today and begins within 15 minutes
            } else if(today &&
                    currentclk < startclk &&
                    currentclk > startclk - (15*60*1000)){
                holder.name.setTextColor(Color.parseColor("#F0B12B"));

            // Else just get standard text color
            } else {
                holder.name.setTextColor(holder.room.getCurrentTextColor());
            }
        }
    }
}
