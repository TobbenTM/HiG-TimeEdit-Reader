package com.tobbentm.higreader;

import android.app.ActionBar;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DSLecTemp;

import java.sql.SQLException;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created by Tobias on 26.09.13.
 */
public class ViewFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {

    TextView errortv;
    String id, name;
    DBHelper helper;
    DSLecTemp datasource;
    private PullToRefreshAttacher ptra;
    private boolean room = false;
    private LectureCursorAdapter adapter;

    public ViewFragment(String name, String id){
        this.name = name;
        this.id = id;
    }

    public ViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        errortv = (TextView) view.findViewById(R.id.timetable_tv);
        ptra = ((MainActivity) getActivity()).getPtra();
        ptra.addRefreshableView(view.findViewById(android.R.id.list), this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        datasource = new DSLecTemp(getActivity());
        helper = new DBHelper(getActivity());
        ActionBar ab = getActivity().getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(this.name);

        //Log.d("FRAG", "Opening datasources");
        try {
            datasource.open();
        } catch (SQLException e) {
            //Log.d("ERROR", "SQException in TimeTableFragment onActivityCreated");
            e.printStackTrace();
        }

        helper.truncate(helper.getWritableDatabase(), DBHelper.TABLE_TEMP_LECTURES);
        Cursor cursor = datasource.getLecturesCursor();
        //getActivity().startManagingCursor(cursor); //Just caused bugs, hacked around it in onResume and onPause

        adapter = new LectureCursorAdapter(getActivity(), cursor, 0);
        setListAdapter(adapter);
        updateLectures();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timetablemenu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_update:
                updateLectures();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
    }

    @Override
    public void onPause(){
        super.onPause();
        datasource.close();
        adapter.notifyDataSetInvalidated();
        adapter.changeCursor(null);
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.changeCursor(datasource.getLecturesCursor());
        adapter.notifyDataSetChanged();
    }

    public void updateLectures(){
        ptra.setRefreshing(true);
        id += ",-1,1.182";
        //Needed to get lecture name for some lectures.
        //Don't know why. It just works (tm).

        Network.timetable(id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if(datasource.isOpen()){
                    if(response != null && response.length() > 0){
                        errortv.setVisibility(View.GONE);
                        if(id.contains(".185"))
                            room = true;
                        String[][] result = TimeParser.timetable(response, room);

                        helper.truncate(helper.getWritableDatabase(), DBHelper.TABLE_TEMP_LECTURES);
                        for(String[] arr : result){
                            datasource.addLecture(arr[2], arr[3], arr[4], arr[0], arr[1]);
                        }

                        adapter.changeCursor(datasource.getLecturesCursor());
                        adapter.notifyDataSetChanged();
                        ptra.setRefreshComplete();
                    }else{
                        if(adapter.isEmpty())
                            errortv.setVisibility(View.VISIBLE);
                        else
                            Toast.makeText(getActivity(), getResources().getString(R.string.timetable_update_error), Toast.LENGTH_SHORT).show();
                        onFailure(null, null);
                    }
                }
            }
            @Override
            public void onFailure(Throwable e, String response){
                if(isAdded() && datasource.isOpen()){
                    ptra.setRefreshComplete();
                    if(adapter.isEmpty())
                        errortv.setVisibility(View.VISIBLE);
                    else
                        Toast.makeText(getActivity(), getResources().getString(R.string.timetable_update_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRefreshStarted(View view) {
        updateLectures();
    }
}
