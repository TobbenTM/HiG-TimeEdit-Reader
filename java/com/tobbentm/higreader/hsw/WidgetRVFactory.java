package com.tobbentm.higreader.hsw;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.tobbentm.higreader.R;
import com.tobbentm.higreader.db.DBLectures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Tobias on 04.02.14.
 */
public class WidgetRVFactory implements RemoteViewsService.RemoteViewsFactory {

    Context ctx;
    List<DBLectures> list;
    private final SimpleDateFormat orgDate = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat newDate = new SimpleDateFormat("EEE dd/MM");

    public WidgetRVFactory(Context ctx, List<DBLectures> list){
        this.ctx = ctx;
        this.list = list;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews views = new RemoteViews(ctx.getPackageName(), R.layout.widget_list_item);
        String name = list.get(position).get_name();
        Date date = new Date();

        try {
            date = orgDate.parse(list.get(position).get_date());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String currentDate = newDate.format(date);
        if(currentDate.contains(newDate.format(new Date()))){
            currentDate = ctx.getResources().getString(R.string.timetable_today);
        }

        if(name != null && name.contains("HIGREADER.newDate")){
            views.setViewVisibility(R.id.widget_sec_tv, View.GONE);
            views.setInt(R.id.widget_item_container, "setBackgroundResource", R.color.widget_bg);
            views.setTextViewText(R.id.widget_main_tv, "\t"+currentDate);
        }else{
            views.setViewVisibility(R.id.widget_sec_tv, View.VISIBLE);
            views.setInt(R.id.widget_item_container, "setBackgroundResource", 0);
            views.setTextViewText(R.id.widget_main_tv, name);
            views.setTextViewText(R.id.widget_sec_tv, list.get(position).get_time().replaceAll("\\n", " ")
                    + "\t-\t" + list.get(position).get_room());
        }

        Intent mainIntent = new Intent();
        views.setOnClickFillInIntent(R.id.widget_item_container, mainIntent);

        return views;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
