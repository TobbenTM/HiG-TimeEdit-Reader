package com.tobbentm.higreader.db;

/**
 * Created by Tobias on 29.08.13.
 */
public class DBSubscriptions {

    private int _id;
    private String _class_id;
    private String _name;
    private String _excluded;

    public DBSubscriptions(){
    }

    public DBSubscriptions(int id, String class_id, String name, String excluded){
        this._id = id;
        this._class_id = class_id;
        this._name = name;
        this._excluded = excluded;
    }

    public DBSubscriptions(String class_id, String name, String excluded){
        this._class_id = class_id;
        this._name = name;
        this._excluded = excluded;
    }

    public DBSubscriptions(String class_id, String name){
        this._class_id = class_id;
        this._name = name;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getClassID(){
        return this._class_id;
    }

    public void setClassID(String classID){
        this._class_id = classID;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getExcluded(){
        return this._excluded;
    }

    public void setExcluded(String excluded){
        this._excluded = excluded;
    }

    @Override
    public String toString(){
        return this._id + ", " + this._class_id + ", " + this._name + ", " + this._excluded;
    }

}
