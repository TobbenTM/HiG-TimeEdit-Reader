package com.tobbentm.higreader.db;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;

import com.tobbentm.higreader.DBDoneCallback;
import com.tobbentm.higreader.TimeParser;

import java.sql.SQLException;

/**
 * Created by Tobias on 06.02.14.
 */
public class DBUpdate extends AsyncTask<Void, Void, Cursor> {

    private Context ctx;
    private String csv;
    private boolean room;
    private boolean view;
    DBDoneCallback callback;

    public DBUpdate(Context ctx, String csv, boolean view,
                    boolean room, DBDoneCallback callback){
        this.ctx = ctx;
        this.csv = csv;
        this.room = room;
        this.view = view;
        this.callback = callback;
    }

    @Override
    protected Cursor doInBackground(Void... params) {
        String[][] result = TimeParser.timetable(csv, room);

        DBHelper helper = new DBHelper(ctx);
        DSLectures datasource;
        if(view){
            datasource = new DSLectures(ctx, DBHelper.TABLE_TEMP_LECTURES);
        }else{
            datasource = new DSLectures(ctx);
        }
        DSSettings settings = new DSSettings(ctx);

        try {
            datasource.open();
            settings.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String table;
        if(view){
            table = DBHelper.TABLE_TEMP_LECTURES;
            helper.truncate(helper.getWritableDatabase(), DBHelper.TABLE_TEMP_LECTURES);
        }else{
            table = DBHelper.TABLE_LECTURES;
        }

        helper.truncate(helper.getWritableDatabase(), table);
        for(String[] arr : result){
            datasource.addLecture(arr[2], arr[3], arr[4], arr[0], arr[1]);
        }

        return datasource.getLecturesCursor();
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        callback.DBDone(cursor);
    }
}