package com.tobbentm.higreader;

import android.app.ActionBar;
import android.app.ListFragment;
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
import com.tobbentm.higreader.db.DBLectures;

import org.apache.http.Header;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created by Tobias on 26.09.13.
 */
public class ViewFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener {

    TextView errortv;
    String id, name;
    private DBLectures[] lectures;
    private PullToRefreshAttacher ptra;
    private boolean room = false;
    private LectureArrayAdapter adapter;

    public ViewFragment(){

    }

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

        Bundle args = this.getArguments();
        this.name = args.getString("name");
        this.id = args.getString("id");

        // savedInstanceState is not null if this fragment has existed before,
        // and if it has this will get name, id and lectures from last time
        if(savedInstanceState != null){
            this.name = savedInstanceState.getString("name");
            this.id = savedInstanceState.getString("id");
            this.lectures = (DBLectures[]) savedInstanceState.getParcelableArray("lectures");
        }

        // ActionBar stuff, custom title for ViewFragment (whatever you are searching for)
        ActionBar ab = getActivity().getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
        ab.setTitle(this.name);

        // Adapter init
        adapter = new LectureArrayAdapter(getActivity(), android.R.id.list, new ArrayList<DBLectures>());
        setListAdapter(adapter);

        // If list of lectures is null or empty; download timetable,
        // else just use what's stored
        if(this.lectures==null || this.lectures.length == 0)
            updateLectures();
        else
            adapter.notifyDataSetChanged(lectures);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        // Generating a state bundle to preserve variables and lecture
        // array across configuration changes etc
        state.putString("name", this.name);
        state.putString("id", this.id);
        state.putParcelableArray("lectures", this.lectures);
        super.onSaveInstanceState(state);
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

    public void updateLectures(){
        ptra.setRefreshing(true);
        id += ",-1,1.182";
        //Needed to get lecture name for some lectures.
        //Don't know why. It just works (tm).

        Network.timetable(id, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody){
                String response = new String(responseBody);
                if (response != null && response.length() > 0) {
                    errortv.setVisibility(View.GONE);
                    if (id.contains(".185"))
                        room = true;

                    httpFinished(response);
                } else {
                    if (adapter.isEmpty())
                        errortv.setVisibility(View.VISIBLE);
                    else
                        Toast.makeText(getActivity(), getResources().getString(R.string.timetable_update_error), Toast.LENGTH_SHORT).show();
                    onFailure(0, null, null, null);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error){
                if (isAdded()) {
                    ptra.setRefreshComplete();
                    if (adapter.isEmpty())
                        errortv.setVisibility(View.VISIBLE);
                    else
                        Toast.makeText(getActivity(), getResources().getString(R.string.timetable_update_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Network finished, update adapter with new list
    private void httpFinished(String csv){
        this.lectures = TimeParser.lectures(csv, room);
        adapter.notifyDataSetChanged(lectures);
        ptra.setRefreshComplete();
    }

    // Method is called from PullToRefresh thingy,
    // indicates a pulltorefresh action, duh
    @Override
    public void onRefreshStarted(View view) {
        updateLectures();
    }
}
