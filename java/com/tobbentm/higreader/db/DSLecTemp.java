package com.tobbentm.higreader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobias on 29.08.13.
 */

/*public static final String DATABASE_CREATE_2 =
        "create table " + TABLE_LECTURES + "("
        + COLUMN_DB_ID + " integer primary key autoincrement, "
        + COLUMN_LECTURE_ID + " text not null, "
        + COLUMN_NAME + " text not null, "
        + COLUMN_ROOM + " text not null, "
        + COLUMN_LECTURER + " text not null, "
        + COLUMN_DATE + " text not null, "
        + COLUMN_TIME + " text not null"
        + "); ";*/

public class DSLecTemp {

    private SQLiteDatabase database;
    private DBHelper helper;
    private String[] allColumns = {DBHelper.COLUMN_DB_ID, DBHelper.COLUMN_LECTURE_ID, DBHelper.COLUMN_NAME
        , DBHelper.COLUMN_ROOM, DBHelper.COLUMN_LECTURER, DBHelper.COLUMN_DATE, DBHelper.COLUMN_TIME};

    public DSLecTemp(Context context){
        helper = new DBHelper(context);
    }

    public void open() throws SQLException{
        database = helper.getWritableDatabase();
    }

    public boolean isOpen(){
        return database.isOpen();
    }

    public void close(){
        helper.close();
    }

    public void addLecture(String name, String room, String lecturer, String date, String time){
        String query = "INSERT INTO templectures('name','room','lecturer','date','time')"
                + "VALUES('"+name+"','"+room+"','"+lecturer+"','"+date+"','"+time+"');";
        //Log.d("DATABASE", query);
        database.execSQL(query);
    }

    public List<DBLectures> getLectures(){
        List<DBLectures> lectures = new ArrayList<DBLectures>();
        Cursor cursor = database.query(DBHelper.TABLE_TEMP_LECTURES, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            DBLectures lecture = cursorToLecture(cursor);
            lectures.add(lecture);
            cursor.moveToNext();
        }

        cursor.close();
        return lectures;
    }

    public Cursor getLecturesCursor(){
        return database.query(DBHelper.TABLE_TEMP_LECTURES, allColumns, null, null, null, null, null);
    }

    public DBLectures cursorToLecture(Cursor cursor){
        DBLectures lecture = new DBLectures();
        lecture.setID(cursor.getInt(0));
        lecture.set_lecture_id(cursor.getString(1));
        lecture.set_name(cursor.getString(2));
        lecture.set_room(cursor.getString(3));
        lecture.set_lecturer(cursor.getString(4));
        lecture.set_date(cursor.getString(5));
        lecture.set_time(cursor.getString(6));
        return lecture;
    }

}
