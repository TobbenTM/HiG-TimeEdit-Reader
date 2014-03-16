package com.tobbentm.higreader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by Tobias on 03.10.13.
 */
public class DSRecent {

    private SQLiteDatabase database;
    private DBHelper helper;
    private String[] allColumns = {DBHelper.COLUMN_DB_ID, DBHelper.COLUMN_CLASS_ID, DBHelper.COLUMN_NAME};

    public DSRecent(Context context){
        helper = DBHelper.getInstance(context);
    }

    public void open() throws SQLException{
        database = helper.getWritableDatabase();
    }

    public void close(){
        helper.close();
    }

    public void addRecent(String classid, String name){
        String delQuery = "DELETE FROM recent WHERE name='"+name+"';";
        String addQuery = "INSERT INTO recent('classid','name') VALUES('"+classid+"','"+name+"');";
        database.execSQL(delQuery);
        database.execSQL(addQuery);
    }

    public Cursor getRecentCursor(){
        return database.query(DBHelper.TABLE_RECENT, allColumns, null, null, null, null, DBHelper.COLUMN_DB_ID+" DESC", "4");
    }

    public int getSize(){
        Cursor cursor = database.rawQuery("SELECT * FROM "+DBHelper.TABLE_RECENT, null);
        int rows = cursor.getCount();
        return rows;
    }

    public boolean isOpen(){
        return database.isOpen();
    }

    public void trimDB(){
        database.rawQuery("DELETE FROM recent WHERE id NOT IN ( SELECT id FROM recent ORDER BY id DESC LIMIT 4 )", null);
    }

    public void truncate(){
        database.execSQL("DELETE FROM recent");
    }

}
