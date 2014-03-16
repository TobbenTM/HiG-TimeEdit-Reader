package com.tobbentm.higreader.db;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tobias on 29.08.13.
 */

public class DBLectures implements Parcelable {

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

    public DBLectures(String[] array) {
        this._date = array[0];
        this._time = array[1];
        this._name = array[2];
        this._room = array[3];
        this._lecturer = array[4];
    }

    public DBLectures(Parcel in){
        this._id = in.readInt();
        this._lecture_id = in.readString();
        this._name = in.readString();
        this._room = in.readString();
        this._lecturer = in.readString();
        this._date = in.readString();
        this._time = in.readString();
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

    public String toString(){
        return "(" + this._date + ") " + this._name + ", " + this._lecturer + " - " + this._room;
    }

    public final Parcelable.Creator<DBLectures> CREATOR = new Parcelable.Creator<DBLectures>() {
        public DBLectures createFromParcel(Parcel in) {
            return new DBLectures(in);
        }

        public DBLectures[] newArray(int size) {
            return new DBLectures[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_id);
        dest.writeString(_lecture_id);
        dest.writeString(_name);
        dest.writeString(_room);
        dest.writeString(_lecturer);
        dest.writeString(_date);
        dest.writeString(_time);
    }
}
