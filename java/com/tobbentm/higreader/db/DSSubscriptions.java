package com.tobbentm.higreader.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobias on 29.08.13.
 */
public class DSSubscriptions {

    private SQLiteDatabase database;
    private DBHelper helper;
    private String[] allColumns = {DBHelper.COLUMN_DB_ID, DBHelper.COLUMN_CLASS_ID, DBHelper.COLUMN_NAME, DBHelper.COLUMN_EXCLUDED};

    public DSSubscriptions(Context context){
        helper = DBHelper.getInstance(context);
    }

    public void open() throws SQLException{
        database = helper.getWritableDatabase();
    }

    public void close(){
        helper.close();
    }

    public boolean isOpen(){
        return database.isOpen();
    }

    public void addSubscription(String id, String name){
        //Log.d("DB", " Input received: " + id + ", " + name);
        ContentValues values = new ContentValues();

        values.put(DBHelper.COLUMN_NAME, name);
        values.put(DBHelper.COLUMN_CLASS_ID, id);

        //Log.d("DB", values.toString());

        //database.insert(DBHelper.TABLE_SUBSCRIPTIONS, null, values); //Dont work for some reason
        String query = "INSERT INTO subscriptions('classid','name') VALUES('" + id + "', '" + name + "');";
        //Log.d("DB", query);
        database.execSQL(query);
    }

    public void deleteSubscription(String id){
        database.delete(DBHelper.TABLE_SUBSCRIPTIONS, DBHelper.COLUMN_CLASS_ID + " = " + id, null);
    }

    public Cursor getCursor(){
        return database.query(DBHelper.TABLE_SUBSCRIPTIONS, allColumns, null, null, null, null, null);
    }

    public List<DBSubscriptions> getSubscriptions(){
        List<DBSubscriptions> subs = new ArrayList<DBSubscriptions>();
        Cursor cursor = database.query(DBHelper.TABLE_SUBSCRIPTIONS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            DBSubscriptions sub = cursorToSub(cursor);
            subs.add(sub);
            cursor.moveToNext();
        }
        cursor.close();
        return subs;
    }

    public DBSubscriptions cursorToSub(Cursor cursor){
        DBSubscriptions sub = new DBSubscriptions();
        sub.setID(cursor.getInt(0));
        sub.setClassID(cursor.getString(1));
        sub.setName(cursor.getString(2));
        sub.setExcluded(cursor.getString(3));
        return sub;
    }

    public int getSize(){
        Cursor cursor = database.rawQuery("SELECT * FROM "+DBHelper.TABLE_SUBSCRIPTIONS, null);
        int rows = cursor.getCount();
        return rows;
    }
}
