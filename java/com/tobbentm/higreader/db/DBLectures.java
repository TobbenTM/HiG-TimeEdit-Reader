package com.tobbentm.higreader.db;

/**
 * Created by Tobias on 29.08.13.
 */

public class DBLectures {

    private int _id;
    private String _lecture_id;
    private String _name;
    private String _room;
    private String _lecturer;
    private String _date;
    private String _time;

    public DBLectures(){
    }

    public DBLectures(int id, String lecture_id, String name, String room, String lecturer, String date, String time){
        this._id = id;
        this._lecture_id = lecture_id;
        this._name = name;
        this._room = room;
        this._lecturer = lecturer;
        this._date = date;
        this._time = time;
    }

    public DBLectures(String name, String room, String lecturer, String date, String time){
        this._name = name;
        this._room = room;
        this._lecturer = lecturer;
        this._date = date;
        this._time = time;
    }

    public int getID(){
        return this._id;
    }

    public void setID(int id){
        this._id = id;
    }

    public String get_lecture_id() {
        return this._lecture_id;
    }

    public void set_lecture_id(String lecture_id) {
        this._lecture_id = lecture_id;
    }

    public String get_name() {
        return this._name;
    }

    public void set_name(String name) {
        this._name = name;
    }

    public String get_room() {
        return this._room;
    }

    public void set_room(String room) {
        this._room = room;
    }

    public String get_lecturer() {
        return this._lecturer;
    }

    public void set_lecturer(String lecturer) {
        this._lecturer = lecturer;
    }

    public String get_date() {
        return this._date;
    }

    public void set_date(String date) {
        this._date = date;
    }

    public String get_time() {
        return this._time;
    }

    public void set_time(String time) {
        this._time = time;
    }

}
