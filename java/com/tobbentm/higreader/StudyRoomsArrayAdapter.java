package com.tobbentm.higreader;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Tobias on 23.02.2015.
 */
public class StudyRoomsArrayAdapter extends ArrayAdapter<String[]> {

    List<String[]> studyrooms;
    LayoutInflater inflater;
    int res;

    public StudyRoomsArrayAdapter(Context context, int resource, List<String[]> studyrooms) {
        super(context, resource, studyrooms);
        this.studyrooms = studyrooms;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.res = resource;
    }

    @Override
    public int getCount(){
        return studyrooms.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(res, parent, false);

        String[] room = studyrooms.get(position);
        if(room != null){
            TextView tvRoom = (TextView)view.findViewById(R.id.text_room);
            TextView tvStat = (TextView)view.findViewById(R.id.text_status);
            tvRoom.setText(room[0]);
            tvStat.setText(room[2]);

            if(room[2].startsWith("<") || room[2].equals("Available")){
                view.setBackgroundResource(R.drawable.list_clear_background);
            }
        }

        return view;
    }

}
