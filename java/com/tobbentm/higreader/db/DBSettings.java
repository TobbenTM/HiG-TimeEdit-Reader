package com.tobbentm.higreader.db;

/**
 * Created by Tobias on 29.08.13.
 */
public class DBSettings {

    private int _id;
    private String _setting;
    private String _value;

    public DBSettings(){
    }

    public DBSettings(int id, String setting, String value){
        this._id = id;
        this._setting = setting;
        this._value = value;
    }

    public DBSettings(String setting, String value){
        this._setting = setting;
        this._value = value;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getSetting(){
        return this._setting;
    }

    public void setSetting(String setting){
        this._setting = setting;
    }

    public String getValue(){
        return this._value;
    }

    public void setValue(String value){
        this._value = value;
    }

}
