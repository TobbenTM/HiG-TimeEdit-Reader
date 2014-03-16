package com.tobbentm.higreader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Tobias on 29.08.13.
 */
public class DSSettings {

    private SQLiteDatabase database;
    private DBHelper helper;

    public DSSettings(Context context){
        helper  = DBHelper.getInstance(context);
    }

    public void open() throws SQLException{
        database = helper.getWritableDatabase();
    }

    public void close(){
        helper.close();
    }

    public void updateSetting(String setting, String value){
        database.execSQL("UPDATE settings SET value='"+value+"' WHERE setting='"+setting+"'");
    }

    public String getSetting(String setting){
        Cursor cursor = database.rawQuery("SELECT * FROM settings WHERE setting='"+setting+"'", null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_VALUE));
    }

    public int getSize(){
        Cursor cursor = database.rawQuery("SELECT * FROM "+DBHelper.TABLE_SETTINGS, null);
        return cursor.getCount();
    }

}
