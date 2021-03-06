package com.tobbentm.higreader.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tobbentm.higreader.MainActivity;

/**
 * Created by Tobias on 29.08.13.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    public static final String TABLE_LECTURES = "lectures";
    public static final String TABLE_TEMP_LECTURES = "templectures";
    public static final String TABLE_SUBSCRIPTIONS = "subscriptions";
    public static final String TABLE_RECENT = "recent";

    public static final String COLUMN_DB_ID = "_id";
    public static final String COLUMN_LECTURE_ID = "lectureid";
    public static final String COLUMN_CLASS_ID = "classid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_LECTURER = "lecturer";
    public static final String COLUMN_ROOM = "room";
    public static final String COLUMN_EXCLUDED = "excluded";

    public static final String DATABASE_NAME = "higreader.db";
    public static final int DATABASE_VERSION = 11;

    public static final String DATABASE_CREATE_1 =
            "create table " + TABLE_SUBSCRIPTIONS + "("
            + COLUMN_DB_ID + " integer primary key autoincrement, "
            + COLUMN_CLASS_ID + " text not null, "
            + COLUMN_NAME + " text, "
            + COLUMN_EXCLUDED + " text"
            + "); ";
    public static final String DATABASE_CREATE_2 =
            "create table " + TABLE_LECTURES + "("
            + COLUMN_DB_ID + " integer primary key autoincrement, "
            + COLUMN_LECTURE_ID + " text, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_ROOM + " text, "
            + COLUMN_LECTURER + " text, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_TIME + " text not null"
            + "); ";
    public static final String DATABASE_CREATE_4 =
            "create table " + TABLE_RECENT + "("
            + COLUMN_DB_ID + " integer primary key autoincrement, "
            + COLUMN_CLASS_ID + " text not null, "
            + COLUMN_NAME + " text"
            + "); ";

    public static final String DATABASE_MOD_1 =
            "DROP TABLE IF EXISTS " + TABLE_TEMP_LECTURES;

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getInstance(Context context){
        if(instance == null){
            instance = new DBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE_1);
        sqLiteDatabase.execSQL(DATABASE_CREATE_2);
        sqLiteDatabase.execSQL(DATABASE_CREATE_4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldV, int newV) {
        Log.d("DB", " Upgrading DB from " + oldV);

        switch (oldV){
            case 8:
                sqLiteDatabase.execSQL(DATABASE_CREATE_4);
            case 9:
                sqLiteDatabase.execSQL(DATABASE_MOD_1);
            case 10:
                // No real need for settings migration
        }
    }

    public void truncate(SQLiteDatabase sqLiteDatabase, String table){
        sqLiteDatabase.execSQL("DELETE FROM " + table);
    }
}
