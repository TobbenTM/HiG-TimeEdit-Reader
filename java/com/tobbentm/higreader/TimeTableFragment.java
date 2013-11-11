package com.tobbentm.higreader;

import android.app.ActionBar;
import android.app.ListFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DBSubscriptions;
import com.tobbentm.higreader.db.DSLectures;
import com.tobbentm.higreader.db.DSSettings;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created by Tobias on 27.08.13.
 */
public class TimeTableFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {

    TextView errortv;
    DSLectures datasource;
    DSSubscriptions subscriptionsDatasource;
    DSSettings settingsDatasource;
    DBHelper helper;
    private PullToRefreshAttacher ptra;
    private Date date = new Date();
    private LectureCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        errortv = (TextView) view.findViewById(R.id.timetable_tv);

        // Get pullToRefreshAttacher from activity and attach to list
        ptra = ((MainActivity) getActivity()).getPtra();
        ptra.addRefreshableView(view.findViewById(android.R.id.list), this);

        // Need to set to true in order to add fragmentspecific items (action_update)
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Initiating datasources and helper
        datasource = new DSLectures(getActivity());
        subscriptionsDatasource = new DSSubscriptions(getActivity());
        settingsDatasource = new DSSettings(getActivity());
        helper = new DBHelper(getActivity());

        // Main timetable dont need to have custom title, and to go up
        ActionBar ab = getActivity().getActionBar();
        ab.setTitle(getActivity().getResources().getString(R.string.timetable_title));
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setHomeButtonEnabled(false);

        try {
            datasource.open();
            subscriptionsDatasource.open();
            settingsDatasource.open();
        } catch (SQLException e) {
            //Log.d("ERROR", "SQException in TimeTableFragment onActivityCreated");
            e.printStackTrace();
        }

        Cursor cursor = datasource.getLecturesCursor();
        //getActivity().startManagingCursor(cursor); //Just caused bugs, hacked around it in onResume and onPause

        adapter = new LectureCursorAdapter(getActivity(), cursor, 0);
        setListAdapter(adapter);

        checkUpdate();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Add fragment specific items to actionbar (action_update)
        inflater.inflate(R.menu.timetablemenu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_update:
                //Toast.makeText(getActivity(), "Updating..", Toast.LENGTH_SHORT).show();
                updateLectures();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Empty function to remove onClick animation/color on lectures
    }

    @Override
    public void onPause(){
        super.onPause();
        datasource.close();
        settingsDatasource.close();
        subscriptionsDatasource.close();
        adapter.notifyDataSetInvalidated();
        adapter.changeCursor(null);
    }

    @Override
    public void onResume(){
        super.onResume();
        try {
            datasource.open();
            subscriptionsDatasource.open();
            settingsDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.changeCursor(datasource.getLecturesCursor());
        adapter.notifyDataSetChanged();
    }

    public void updateLectures(){
        ptra.setRefreshing(true);
        String ids = "";
        List<DBSubscriptions> list = subscriptionsDatasource.getSubscriptions();
        int d = 0;
        for (DBSubscriptions sub : list){
            if(d>0) ids += ",-1,";
            ids += sub.getClassID();
            d++;
        }
        ids += ",-1,1.182";
            //Needed to get lecture name for some lectures.
            //Don't know why. It just works (tm).

        Network.timetable(ids, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if(datasource.isOpen()){
                    if(response != null && response.length() > 0){
                        errortv.setVisibility(View.GONE);
                        String[][] result = TimeParser.timetable(response, false);

                        helper.truncate(helper.getWritableDatabase(), DBHelper.TABLE_LECTURES);
                        for(String[] arr : result){
                            datasource.addLecture(arr[2], arr[3], arr[4], arr[0], arr[1]);
                        }

                        adapter.changeCursor(datasource.getLecturesCursor());
                        adapter.notifyDataSetChanged();
                        Long time = date.getTime();
                        settingsDatasource.updateSetting(DBHelper.SETTING_LASTUPDATED, time.toString());
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

    private void checkUpdate(){
        String lastupdated = settingsDatasource.getSetting(DBHelper.SETTING_LASTUPDATED);
        Long time;
        try{
            time = Long.parseLong(lastupdated);
        }catch (NumberFormatException e){
            time = 0L;
        }
        if(subscriptionsDatasource.getSize() != 0){
            if(settingsDatasource.getSize() != 0 && time < date.getTime() - (2*60*60*1000)){
                updateLectures();
            }
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        updateLectures();
    }
}
