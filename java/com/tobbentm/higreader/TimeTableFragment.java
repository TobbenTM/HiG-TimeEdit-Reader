package com.tobbentm.higreader;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
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
import com.tobbentm.higreader.db.DBLectures;
import com.tobbentm.higreader.db.DBSubscriptions;
import com.tobbentm.higreader.db.DBUpdate;
import com.tobbentm.higreader.db.DSLectures;
import com.tobbentm.higreader.db.DSSettings;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created by Tobias on 27.08.13.
 */
public class TimeTableFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener,
        LoaderManager.LoaderCallbacks<DBLectures[]>{

    TextView errortv;
    DSLectures datasource;
    DSSubscriptions subscriptionsDatasource;
    DSSettings settingsDatasource;
    DBHelper helper;
    private PullToRefreshAttacher ptra;
    private Date date = new Date();
    private LectureArrayAdapter adapter;
    private FragmentManager fm;
    private LoaderManager lm;
    private static final int TIMETABLE_LOADER_ID = 1;

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
        datasource = DSLectures.getInstance(getActivity());
        subscriptionsDatasource = new DSSubscriptions(getActivity());
        settingsDatasource = new DSSettings(getActivity());
        helper = DBHelper.getInstance(getActivity());

        fm = getFragmentManager();

        // Main timetable don't need to have custom title, nor to go up
        ActionBar ab = getActivity().getActionBar();
        ab.setTitle(getActivity().getResources().getString(R.string.timetable_title));
        ab.setDisplayHomeAsUpEnabled(false);
        ab.setHomeButtonEnabled(false);

        try {
            datasource.open();
            subscriptionsDatasource.open();
            settingsDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Deletes lectures older than today
        datasource.deleteOld();

        // Setting us up the adapter
        adapter = new LectureArrayAdapter(getActivity(), android.R.id.list, new ArrayList<DBLectures>());
        setListAdapter(adapter);

        // LoaderManager stuff
        LoaderManager.LoaderCallbacks<DBLectures[]> loaderCallbacks = this;
        lm = getLoaderManager();
        lm.initLoader(TIMETABLE_LOADER_ID, null, loaderCallbacks);

        // Check if an update is wanted
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

    public void updateLectures(){
        if(!subscriptionsDatasource.isOpen()){
            try {
                subscriptionsDatasource.open();
            } catch (SQLException e) {
                return;
            }
        }

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

                        httpFinished(response);
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

    // Function to check if an update is wanted, and updates timetable
    private void checkUpdate(){
        // Getting timestamp for the last update
        String lastupdated = settingsDatasource.getSetting(DBHelper.SETTING_LASTUPDATED);
        Long time;
        try{
            time = Long.parseLong(lastupdated);
        }catch (NumberFormatException e){
            // No timestamp for last update, set to zero to force update
            time = 0L;
        }
        if(subscriptionsDatasource.getSize() != 0){
            if(settingsDatasource.getSize() != 0 && time < date.getTime() - (2*60*60*1000)){
                updateLectures();
            }
        }
    }

    // Network finished, call for loader restart
    // in order to do DB and adapter updates
    private void httpFinished(String csv){
        Bundle bundle = new Bundle();
        bundle.putString("csv", csv);
        lm.restartLoader(TIMETABLE_LOADER_ID, bundle, this);
    }

    // Method is called from PullToRefresh thingy,
    // indicates a pulltorefresh action, duh
    @Override
    public void onRefreshStarted(View view) {
        updateLectures();
    }

    // Loader manager callbacks
    @Override
    public Loader<DBLectures[]> onCreateLoader(int id, Bundle args) {
        if(args != null){
            Log.d("higreader", "onCreateLoader(), args != NULL");
            return new DBUpdate(getActivity(), args.getString("csv"));
        }
        return new DBUpdate(getActivity(), null);
    }

    @Override
    public void onLoadFinished(Loader<DBLectures[]> loader, DBLectures[] data) {
        adapter.notifyDataSetChanged(data);
        Long time = date.getTime();
        settingsDatasource.updateSetting(DBHelper.SETTING_LASTUPDATED, time.toString());
        ptra.setRefreshComplete();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        adapter.notifyDataSetInvalidated();
    }
}
