package com.tobbentm.higreader;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DBSubscriptions;
import com.tobbentm.higreader.db.DSLectures;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Tobias on 27.08.13.
 */
public class TimeTableFragment extends ListFragment {

    ProgressBar pb;
    DSLectures datasource;
    DSSubscriptions subscriptionsDatasource;
    DBHelper helper;
    private LectureCursorAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timetable, container, false);
        pb = (ProgressBar) view.findViewById(R.id.timetable_pb);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        datasource = new DSLectures(getActivity());
        subscriptionsDatasource = new DSSubscriptions(getActivity());
        helper = new DBHelper(getActivity());

        //Log.d("FRAG", "Opening datasources");
        try {
            datasource.open();
            subscriptionsDatasource.open();
        } catch (SQLException e) {
            //Log.d("ERROR", "SQException in TimeTableFragment onActivityCreated");
            e.printStackTrace();
        }

        Cursor cursor = datasource.getLecturesCursor();
        //getActivity().startManagingCursor(cursor); //Just caused bugs, hacked around it in onResume and onPause

        adapter = new LectureCursorAdapter(getActivity(), cursor, 0);
        setListAdapter(adapter);

        if(subscriptionsDatasource.getSize() != 0){
            updateLectures();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timetablemenu, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_update:
                Toast.makeText(getActivity(), "Updating..", Toast.LENGTH_SHORT).show();
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.changeCursor(datasource.getLecturesCursor());
        adapter.notifyDataSetChanged();
    }

    public void updateLectures(){
        pb.setVisibility(View.VISIBLE);
        pb.animate().translationY(10.0F).start();

        String ids = "";
        List<DBSubscriptions> list = subscriptionsDatasource.getSubscriptions();
        int d = 0;
        for (DBSubscriptions sub : list){
            if(d>0) ids += ",";
            ids += sub.getClassID();
            d++;
        }

        Network.timetable(ids, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                String[][] result = TimeParser.timetable(response);

                //Log.d("TIMETABLE\t", Arrays.deepToString(result));

                helper.truncate(helper.getWritableDatabase(), DBHelper.TABLE_LECTURES);
                for(String[] arr : result){
                    //Log.d("TIMETABLE\t", "Adding lecture:\t" + Arrays.toString(arr));
                    datasource.addLecture(arr[2], arr[3], arr[4], arr[0], arr[1]);
                }

                adapter.changeCursor(datasource.getLecturesCursor());
                adapter.notifyDataSetChanged();
                pb.animate().translationY(-10.0F).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                    }
                });
            }
            @Override
            public void onFailure(Throwable e, String response){
                pb.animate().translationY(-10.0F).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                    }
                });
                Toast.makeText(getActivity(), "Could not update timetable.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
