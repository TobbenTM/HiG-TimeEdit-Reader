package com.tobbentm.higreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;

/**
 * Created by Tobias on 27.08.13.
 * Welcome to the messiest fragment ever.
 */
public class WelcomeFragment extends DialogFragment {

    private DSSubscriptions datasource;
    private readyToUpdateListener listener;

    public WelcomeFragment(){} //Required

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Dialog builder
        CustomDialogBuilder builder = new CustomDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_welcome, null); //Create view finding views

        //Spinner adapter and shit
        Spinner spinner = (Spinner) view.findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Builder for dialog
        builder.setCustomView(view)
                .setTitle(getResources().getString(R.string.welcome_title))
                .setNeutralButton(getResources().getString(R.string.welcome_search_btn), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //We wont be using this listener, but the one in onStart()
                        //This is required for older versions of Android
                        //Which I'm not targeting anyway
                        //I don't know why this is here
                    }
                });

        //setCancelable(false);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();


        //Initialize views
        final EditText et = (EditText)dialog.findViewById(R.id.search_edittext);
        final Spinner spinner = (Spinner)dialog.findViewById(R.id.search_spinner);
        final ProgressBar pb = (ProgressBar)dialog.findViewById(R.id.progressBar2);
        final ListView lv = (ListView)dialog.findViewById(R.id.search_result_list);
        final TextView werror = (TextView)dialog.findViewById(R.id.werror_text);
        final Button wbutton = (Button)dialog.findViewById(R.id.welcome_nores_button);



        //Didnt work:
        //et.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if(dialog != null)
        {
            //Initialize views
            final Button neutButton = (Button) dialog.getButton(Dialog.BUTTON_NEUTRAL);

            //Set onClick Listener for action_done on keyboard to search directly
            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if(i == EditorInfo.IME_ACTION_DONE){
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
                        if(!et.getText().toString().matches("[^a-zA-Z0-9\\s-]")){
                            //Manager to close keyboard after searching
                            InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imgr.hideSoftInputFromWindow(et.getWindowToken(), 0);

                            //Toast.makeText(getActivity(), et.getText().toString() + ", " + spinner.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                            //Hide old shit, show progressbar, disable search button
                            spinner.setVisibility(View.GONE);
                            et.setVisibility(View.GONE);
                            pb.setVisibility(View.VISIBLE);
                            neutButton.setEnabled(false);
                            //neutButton.setVisibility(View.GONE);

                            //Get string from search field
                            final String term = et.getText().toString();

                            //Send to network class
                            Network.search(term, spinner.getSelectedItem().toString(),getResources().getStringArray(R.array.search_array) , new AsyncHttpResponseHandler(){
                                @Override
                                public void onSuccess(String response){

                                    //Get parsed results from parser
                                    final String[][] results = TimeParser.search(response, term);
                                    String[] names = new String[results.length];

                                    //Create separate (1D) array of names of classes/courses
                                    for(int i = 0; i < results.length; i++){
                                        names[i] = results[i][1];
                                    }

                                    pb.setVisibility(View.GONE);
                                    //Log.d("DIALOG", "Populating ListView");

                                    //Arrayadapter for populating the listview
                                    final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1, names);
                                    //Log.d("DIALOG", "Populating ListView - Adapter set up");
                                    //Log.d("RES", names.toString());
                                    lv.setAdapter(adapter);
                                    lv.setVisibility(View.VISIBLE);

                                    //onClick Listener for listview
                                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            datasource = new DSSubscriptions(getActivity());
                                            try {
                                                datasource.open();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            datasource.addSubscription(results[position][0], results[position][1]);
                                            datasource.close();
                                            listener.readyToUpdate();
                                            dismiss();
                                        }
                                    });

                                    // This block checks number of items in the listview, if empty show error,
                                    // if one item, choose that automatically.
                                    if(lv.getCount() == 0){
                                        werror.setVisibility(View.VISIBLE);
                                        wbutton.setVisibility(View.VISIBLE);
                                    }else if(lv.getCount() == 1){
                                        datasource = new DSSubscriptions(getActivity());
                                        try {
                                            datasource.open();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        datasource.addSubscription(results[0][0], results[0][1]);
                                        datasource.close();
                                        listener.readyToUpdate();
                                        Toast.makeText(getActivity(), getResources().getString(R.string.welcome_auto_pick) + results[0][1], Toast.LENGTH_SHORT).show();
                                        dismiss();
                                    }

                                }
                                @Override
                                public void onFailure(Throwable e, String response){
                                    Toast.makeText(getActivity(), getResources().getString(R.string.welcome_net_failure), Toast.LENGTH_LONG).show();
                                    Log.d("NET", e.toString());
                                    getActivity().finish();
                                }
                            });
                        }else{
                            //If search string contains illegal characters, this will show up
                            Toast.makeText(getActivity(), getResources().getString(R.string.add_field_illegal), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        //If nothing is written in search field, this will show up
                        Toast.makeText(getActivity(), getResources().getString(R.string.welcome_field_empty), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

