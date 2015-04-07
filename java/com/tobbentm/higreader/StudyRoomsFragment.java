package com.tobbentm.higreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Created by Tobias on 23.02.2015.
 */
public class StudyRoomsFragment extends DialogFragment {

    openTimeTableListener listener;
    List<String[]> studyrooms;
    StudyRoomsArrayAdapter adapter;
    ListView lv;
    ProgressBar pb;

    public StudyRoomsFragment(){}

    public interface openTimeTableListener{
        public void openTimeTable(String name, String ttid);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        try{
            listener = (openTimeTableListener) activity;
        }catch (ClassCastException e){
            throw  new ClassCastException(activity.toString());
        }

        InputStream stream = getResources().openRawResource(R.raw.studyrooms);
        CSVReader reader = new CSVReader(new InputStreamReader(stream), ';', '"');
        try {
            studyrooms = reader.readAll();
            stream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_study_rooms, null);

        builder.setCustomView(view)
                .setTitle(getResources().getString(R.string.sr_title))
                .setIcon(R.drawable.action_search)
                .setNeutralButton(getResources().getString(R.string.sr_close_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }

    @Override
    public void onStart(){
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();


        lv = (ListView)dialog.findViewById(R.id.sr_list);
        pb = (ProgressBar)dialog.findViewById(R.id.sr_pb);

        adapter = new StudyRoomsArrayAdapter(getActivity(), R.layout.studyroom_list_item, studyrooms);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.openTimeTable(studyrooms.get(position)[0], studyrooms.get(position)[1]);
                dismiss();
            }
        });

        String ids = ""; int d = 0;
        for(String[] s : studyrooms){
            if(d>0) ids += ",-1,";
            ids += s[1];
            d++;
        }

        Network.timetable(ids, 0, 3, new AsyncHttpResponseHandler() { // 0 days, sid=3
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                String response = new String(responseBody);
                if (response.length() > 0) {
                    String[][] payload = TimeParser.studyrooms(response);
                    httpComplete(payload);
                }else
                    Toast.makeText(getActivity(), getResources().getString(R.string.sr_network_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                Toast.makeText(getActivity(), getResources().getString(R.string.sr_network_error), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void httpComplete(String[][] payload){
        SimpleDateFormat timef = new SimpleDateFormat("HHmm");
        int cur = Integer.parseInt(timef.format(new Date()));
        for(String[] res : payload){
            int start = Integer.parseInt(res[0].replace(":","").replace(" ",""));
            int end = Integer.parseInt(res[1].replace(":","").replace(" ", ""));
            if(end<cur) continue;

            for(String[] room : studyrooms){
                if(room[2].isEmpty())
                    room[2] = "Available";
                if(res[2].contains(room[0])){
                    if(start <= cur){
                        room[2] = "Taken to " + res[1];
                    }else if(!room[2].startsWith("Taken to")
                            && !room[2].startsWith("< ")){
                        room[2] = "< " + res[0];
                    }
                    //break;
                }
            }
            Log.d("HIGREADER", res[0] + ", " + res[1] + ", " + res[2]);
        }
        adapter.notifyDataSetChanged();
        pb.setVisibility(View.GONE);
        lv.setVisibility(View.VISIBLE);
    }
}
