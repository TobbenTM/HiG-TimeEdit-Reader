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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DSLectures;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;

/**
 * Created by Tobias on 31.08.13.
 */
public class SubscriptionsFragment extends DialogFragment {

    private readyToUpdateListener listener;
    private boolean update = false;
    private SubsCursorAdapter adapter;

    public SubscriptionsFragment(){}

    public interface readyToUpdateListener{
        public void readyToUpdate();
    }

    @Override
     public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            listener = (readyToUpdateListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        if(adapter.update())
            listener.readyToUpdate();
        super.onDismiss(dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        //Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_subs, null);

        builder.setView(view)
                .setTitle(getResources().getString(R.string.subs_title))
                .setNeutralButton(getResources().getString(R.string.subs_close_btn), new DialogInterface.OnClickListener() {
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
        ListView list = (ListView)dialog.findViewById(R.id.subs_list);
        DSSubscriptions datasource = new DSSubscriptions(getActivity());

        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        adapter = new SubsCursorAdapter(getActivity(), datasource.getCursor(), 0);
        list.setAdapter(adapter);
        datasource.close();
    }
}
