package com.tobbentm.higreader.db;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.tobbentm.higreader.TimeParser;

import java.sql.SQLException;

/**
 * Created by Tobias on 06.02.14.
 */
public class DBUpdate extends AsyncTaskLoader<DBLectures[]> {

    private Context ctx;
    private String csv;
    DSLectures datasource;
    DBLectures[] lectures;

    public DBUpdate(Context ctx, String csv){
        super(ctx);
        this.ctx = ctx.getApplicationContext();
        this.csv = csv;
    }

    // Main async worker
    @Override
    public DBLectures[] loadInBackground() {
        if(datasource == null){
            datasource = DSLectures.getInstance(ctx);
        }

        if(!datasource.isOpen()){
            try {
                datasource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // If csv is null we do not have any updated data,
        // and thus only need to fetch from db
        if(csv != null){
            String[][] result = TimeParser.timetable(csv, false);

            DBHelper helper = DBHelper.getInstance(ctx);

            // Always truncate before adding new data
            helper.truncate(helper.getWritableDatabase(), DBHelper.TABLE_LECTURES);
            for(String[] arr : result){
                datasource.addLecture(arr[2], arr[3], arr[4], arr[0], arr[1]);
            }
        }

        lectures = datasource.getLectures().toArray(new DBLectures[datasource.getSize()]);
        return lectures;
    }

    @Override
    public void deliverResult(DBLectures[] lectures) {
        super.deliverResult(lectures);
    }

    @Override
    public void onCanceled(DBLectures[] lectures) {

    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }

    @Override
    protected void onStartLoading() {
        if (lectures != null) {
            deliverResult(lectures);
        }
        if (takeContentChanged() || lectures == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

}