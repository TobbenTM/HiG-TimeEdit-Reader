package com.tobbentm.higreader;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.sql.SQLException;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

public class MainActivity extends Activity implements
        WelcomeFragment.readyToUpdateListener,
        AddSubFragment.readyToUpdateListener,
        SubscriptionsFragment.readyToUpdateListener,
        SearchAdvFragment.openTimeTableListener {

    FragmentManager fm = getFragmentManager();
    SubscriptionsFragment subsFragment;
    AddSubFragment addFragment;
    SearchAdvFragment saFragment;
    TimeTableFragment timeTableFragment;
    WelcomeFragment welcomeFragment;
    ViewFragment viewFragment;
    private PullToRefreshAttacher ptra;
    private DBHelper dbhelper = new DBHelper(this);
    private DSSubscriptions subscriptionsDatasource;
    //private DSRecent recentDatasource;
    //private DSSettings settingsDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subscriptionsDatasource = new DSSubscriptions(this);

        try {
            subscriptionsDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ptra = PullToRefreshAttacher.get(this);

        if(subscriptionsDatasource.getSize() == 0){
            dbTruncate();
            showWelcomeDialog();
            showTimeTable();
        }else{
            showTimeTable();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        subscriptionsDatasource.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            subscriptionsDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showTimeTable(){
        findViewById(R.id.activity_pb).setVisibility(View.GONE);
        timeTableFragment = new TimeTableFragment();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_frame, timeTableFragment, "fragment_timetable");
        ft.commit();
    }

    private void showViewFragment(String name, String id){
        viewFragment = new ViewFragment(name, id);
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.activity_frame, viewFragment, "fragment_view");
        ft.addToBackStack("fragment_view");
        ft.commit();
    }

    private void showWelcomeDialog() {
        welcomeFragment = new WelcomeFragment();
        welcomeFragment.show(fm, "fragment_welcome");
    }

    private void showSubsDialog() {
        subsFragment = new SubscriptionsFragment();
        subsFragment.show(fm, "fragment_subs");
    }

    private void showAddDialog(){
        addFragment = new AddSubFragment();
        addFragment.show(fm, "fragment_add");
    }

    private void showAboutDialog(){
        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.show(fm, "fragment_about");
    }

    private void showSearchAdvDialog(){
        saFragment = new SearchAdvFragment();
        saFragment.show(fm, "fragment_search_adv");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                return true;
            case R.id.action_subs:
                showSubsDialog();
                return true;
            case R.id.action_about:
                showAboutDialog();
                return true;
            case R.id.action_search:
                showSearchAdvDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dbTruncate(){
        //dbhelper.truncate(dbhelper.getWritableDatabase(), DBHelper.TABLE_SUBSCRIPTIONS);
        dbhelper.truncate(dbhelper.getWritableDatabase(), DBHelper.TABLE_LECTURES);
    }

    public void wclose(View view){
        welcomeFragment.dismiss();
        showWelcomeDialog();
    }

    public void aclose(View view){
        addFragment.getDialog().dismiss();
    }

    public void saclose(View view){
        saFragment.getDialog().dismiss();
    }

    public void subAdd(View view){
        subsFragment.getDialog().dismiss();
        showAddDialog();
    }

    @Override
    public void readyToUpdate() {
        timeTableFragment.updateLectures();
    }

    @Override
    public void openTimeTable(String name, String ttid) {
        showViewFragment(name, ttid);
    }

    public PullToRefreshAttacher getPtra(){
        return ptra;
    }

}
