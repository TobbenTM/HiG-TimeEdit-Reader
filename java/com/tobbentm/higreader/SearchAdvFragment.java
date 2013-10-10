package com.tobbentm.higreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DSRecent;

import java.sql.SQLException;

/**
 * Created by Tobias on 26.09.13.
 */
public class SearchAdvFragment extends DialogFragment {

    private openTimeTableListener listener;
    private DSRecent datasource;
    private SimpleCursorAdapter adapter;
    private Boolean recent = false;

    public SearchAdvFragment(){}

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
        datasource = new DSRecent(getActivity());
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(datasource.getSize() > 0)
            recent = true;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_search_adv, null);

        Spinner spinner = (Spinner) view.findViewById(R.id.sa_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sa_search_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setView(view)
                .setTitle(getResources().getString(R.string.sa_title))
                .setNeutralButton(getResources().getString(R.string.sa_search_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();

    }

    @Override
    public void onPause(){
        super.onPause();
        datasource.close();
        if(recent){
            adapter.notifyDataSetInvalidated();
            adapter.changeCursor(null);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(recent){
            adapter.changeCursor(datasource.getRecentCursor());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog){
        super.onDismiss(dialog);
        datasource.close();
    }

    @Override
    public void onStart(){
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();

        final EditText et = (EditText)dialog.findViewById(R.id.sa_edittext);
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.sa_spinner);
        final ListView recentList = (ListView)dialog.findViewById(R.id.sa_recent_list);
        final ProgressBar pb = (ProgressBar)dialog.findViewById(R.id.sa_progressBar);
        final ListView lv = (ListView)dialog.findViewById(R.id.sa_result_list);
        final TextView aerror = (TextView)dialog.findViewById(R.id.sa_error_text);
        final Button abutton = (Button)dialog.findViewById(R.id.sa_nores_button);
        final TextView infotv = (TextView) dialog.findViewById(R.id.sa_info_text);
        final Button neutButton = dialog.getButton(Dialog.BUTTON_NEUTRAL);
        final ImageButton clearbtn = (ImageButton)dialog.findViewById(R.id.sa_recent_btn);
        final LinearLayout recentcontainer = (LinearLayout)dialog.findViewById(R.id.sa_recent_title_container);

        if(!datasource.isOpen()){
            try {
                datasource.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        final Cursor cursor = datasource.getRecentCursor();
        final Cursor listCursor = datasource.getRecentCursor();

        if(recent){
            recentList.setVisibility(View.VISIBLE);
            recentcontainer.setVisibility(View.VISIBLE);
            String[] col = {DBHelper.COLUMN_NAME};
            int[] bind = {android.R.id.text1};
            adapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_1, listCursor, col, bind, 0);
            recentList.setAdapter(adapter);
        }

        clearbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!datasource.isOpen()){
                    try {
                        datasource.open();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetInvalidated();
                datasource.truncate();
                recentList.setVisibility(View.GONE);
                recentcontainer.setVisibility(View.GONE);
            }
        });

        clearbtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getActivity(), clearbtn.getContentDescription(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                cursor.moveToPosition(i);
                datasource.addRecent(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CLASS_ID)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)));
                listener.openTimeTable(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_CLASS_ID)));
                dismiss();
            }
        });

        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    neutButton.performClick();
                    return true;
                }
                return false;
            }
        });

        assert neutButton != null;

        //Set onClick Listener for dialog button
        neutButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(et.getText().length() > 0){
                    //Used to close dialog
                    Boolean closeDialog = false;

                    //Manager to close keyboard after searching
                    InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imgr.hideSoftInputFromWindow(et.getWindowToken(), 0);

                    //Hide old shit, show progressbar, disable search button
                    spinner.setVisibility(View.GONE);
                    et.setVisibility(View.GONE);
                    infotv.setVisibility(View.GONE);
                    recentList.setVisibility(View.GONE);
                    recentcontainer.setVisibility(View.GONE);
                    pb.setVisibility(View.VISIBLE);
                    neutButton.setEnabled(false);

                    //Get string from search field
                    final String term = et.getText().toString();

                    //Send to network class
                    Network.search(term, spinner.getSelectedItem().toString(), getResources().getStringArray(R.array.sa_search_array), new AsyncHttpResponseHandler(){
                        @Override
                        public void onSuccess(String response){
                            Log.d("DIALOG", "onSuccess starting");

                            //Get parsed results from parser
                            final String[][] results = TimeParser.search(response, term);
                            String[] names = new String[results.length];

                            //Create separate (1D) array of names of classes/courses
                            for(int i = 0; i < results.length; i++){
                                names[i] = results[i][1];
                            }

                            pb.setVisibility(View.GONE);

                            //Arrayadapter for populating the listview
                            final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, names);
                            lv.setAdapter(adapter);
                            lv.setVisibility(View.VISIBLE);

                            //onClick Listener for listview
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    datasource.addRecent(results[position][0], results[position][1]);
                                    listener.openTimeTable(results[position][1], results[position][0]);
                                    dismiss();
                                }
                            });

                            if(lv.getCount() == 0){
                                aerror.setVisibility(View.VISIBLE);
                                abutton.setVisibility(View.VISIBLE);
                            }

                        }
                        @Override
                        public void onFailure(Throwable e, String response){
                            Toast.makeText(getActivity(), getResources().getString(R.string.add_net_failure), Toast.LENGTH_LONG).show();
                            Log.d("NET", e.toString());
                        }
                    });

                    if(closeDialog)
                        dismiss();
                }else{
                    //If nothing is written in search field, this will show up
                    Toast.makeText(getActivity(), getResources().getString(R.string.add_field_empty), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
