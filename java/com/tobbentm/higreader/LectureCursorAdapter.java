package com.tobbentm.higreader;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tobbentm.higreader.db.DBHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tobias on 31.08.13.
 */
public class LectureCursorAdapter extends CursorAdapter {

    private LayoutInflater inflater;
    private final SimpleDateFormat orgDate = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat newDate = new SimpleDateFormat("EEE dd/MM");
    private final SimpleDateFormat timef = new SimpleDateFormat("HHmm");

    public LectureCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private static class ViewHolder{
        TextView date;
        TextView name;
        TextView room;
        TextView lecturer;
        TextView time;
        LinearLayout timeContainer;
        LinearLayout textContainer;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.lecture_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder();
        holder.date = (TextView) view.findViewById(R.id.text_date);
        holder.name = (TextView) view.findViewById(R.id.text_name);
        holder.room = (TextView) view.findViewById(R.id.text_room);
        holder.lecturer = (TextView) view.findViewById(R.id.text_lecturer);
        holder.time = (TextView) view.findViewById(R.id.text_time);
        holder.timeContainer = (LinearLayout) view.findViewById(R.id.time_container);
        holder.textContainer = (LinearLayout) view.findViewById(R.id.text_container);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //TODO: Optimize this shit, should not be this much work on one list item

        ViewHolder holder = (ViewHolder) view.getTag();

        Date date = new Date();
        try {
            date = orgDate.parse(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DATE)));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException n) {
            n.printStackTrace();
        }

        String currentDate = newDate.format(date);

        if(currentDate.contains(newDate.format(new Date()))){
            currentDate = context.getResources().getString(R.string.timetable_today);
        }

        if(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)) != null &&
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)).contains("HIGREADER.newDate") ){
            holder.timeContainer.setVisibility(View.GONE);
            holder.textContainer.setVisibility(View.GONE);
            holder.date.setVisibility(View.VISIBLE);
            holder.date.setText(currentDate);

            view.setBackgroundResource(R.drawable.list_date_background);
        }else if(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)) != null &&
                cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)).contains("HIGREADER.clear")){
            holder.timeContainer.setVisibility(View.GONE);
            holder.textContainer.setVisibility(View.GONE);
            holder.date.setVisibility(View.VISIBLE);

            holder.date.setText("\t" + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TIME)).replaceAll("\n", " ") + "\t" + context.getResources().getString(R.string.timetable_clear));
            view.setBackgroundResource(R.drawable.list_clear_background);
        }else{
            holder.date.setVisibility(View.GONE);
            holder.timeContainer.setVisibility(View.VISIBLE);
            holder.textContainer.setVisibility(View.VISIBLE);

            String time = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_TIME));
            String startTime, endTime;
            if(time.split("\n").length >= 3){
                startTime = time.split("\n")[0].replace(":", "");
                endTime = time.split("\n")[2].replace(":", "");
            }else{
                startTime = "0000";
                endTime = "0000";
            }

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

            holder.name.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
            holder.room.setText(context.getResources().getString(R.string.timetable_room) + ":\t\t" + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_ROOM)));
            holder.lecturer.setText(context.getResources().getString(R.string.timetable_lecturer) + ":\t" + cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_LECTURER)));
            holder.time.setText(time);
            view.setBackgroundResource(R.drawable.list_item_background);

            Boolean today = currentDate.contains(context.getResources().getString(R.string.timetable_today));
            if(today &&
                    Integer.parseInt(startTime) <= Integer.parseInt(currenttime) &&
                    Integer.parseInt(endTime) >= Integer.parseInt(currenttime)){
                holder.name.setTextColor(Color.parseColor("#C21B1B"));
            } else if(today &&
                    Integer.parseInt(endTime) < Integer.parseInt(currenttime)){
                holder.name.setTextColor(Color.parseColor("#1ABD1A"));
            } else if(today &&
                    currentclk < startclk &&
                    currentclk > startclk - (15*60*1000)){
                holder.name.setTextColor(Color.parseColor("#F0B12B"));
            } else {
                holder.name.setTextColor(holder.room.getCurrentTextColor());
            }
        }
    }
}