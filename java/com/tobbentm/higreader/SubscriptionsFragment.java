package com.tobbentm.higreader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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
    public SubscriptionsFragment(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        //Dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_subs, null);

        builder.setView(view)
                .setTitle("Subscriptions")
                .setNeutralButton("Close", new DialogInterface.OnClickListener() {
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

        SubsCursorAdapter adapter = new SubsCursorAdapter(getActivity(), datasource.getCursor(), 0);
        list.setAdapter(adapter);
        datasource.close();
    }
}
