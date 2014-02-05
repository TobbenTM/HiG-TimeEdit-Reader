package com.tobbentm.higreader.hsw;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.tobbentm.higreader.db.DBLectures;
import com.tobbentm.higreader.db.DSLectures;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Tobias on 03.02.14.
 */
public class WidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        DSLectures lectures = new DSLectures(getApplicationContext());
        try {
            lectures.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        lectures.deleteOld();
        List<DBLectures> lectureList = lectures.getLectures();
        lectures.close();

        return new WidgetRVFactory(getApplicationContext(), lectureList);
    }

}
