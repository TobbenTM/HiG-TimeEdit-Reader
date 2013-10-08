package com.tobbentm.higreader.db;

/**
 * Created by Tobias on 03.10.13.
 */
public class DBRecent {

    private int _id;
    private String _classid;
    private String _name;

    public DBRecent(){
    }

    public DBRecent(int id, String classid, String name){
        this._id = id;
        this._classid = classid;
        this._name = name;
    }

    public DBRecent(String classid, String name){
        this._classid = classid;
        this._name = name;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String getName(){
        return this._name;
    }

    public void setName(String name){
        this._name = name;
    }

    public String getClassId(){
        return this._classid;
    }

    public void setClassId(String classid){
        this._classid = classid;
    }

}
