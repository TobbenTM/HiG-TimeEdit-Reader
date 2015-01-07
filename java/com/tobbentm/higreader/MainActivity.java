package com.tobbentm.higreader;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
    private DBHelper dbhelper;

    public static final String SP_NAME = "higreaderprefs";
    public static final String SP_LAST = "lastupdated";
    public static final String SP_RANGE = "range";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbhelper = DBHelper.getInstance(this);
        DSSubscriptions subscriptionsDatasource = new DSSubscriptions(this);

        try {
            subscriptionsDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Options for PullToRefreshAttacher, and setting draw distance to 60% of listview
        PullToRefreshAttacher.Options ptraOpt = new PullToRefreshAttacher.Options();
        ptraOpt.refreshScrollDistance = 0.40F;

        // Necessary to assign in activity, will be fetched from fragments
        ptra = PullToRefreshAttacher.get(this, ptraOpt);

        // Checking if there is no subscriptions (first time)
        if(subscriptionsDatasource.getSize() == 0){
            dbTruncate();
            showWelcomeDialog();
            showTimeTable();
        }else{
            showTimeTable();
        }

    }

    // Fragment transaction for TimeTableFragment
    private void showTimeTable(){
        // If fragment is not null, we will not add it another time
        if(fm.findFragmentByTag("fragment_timetable") == null){
            timeTableFragment = new TimeTableFragment();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.activity_frame, timeTableFragment, "fragment_timetable");
            ft.commit();
        }
    }

    // Fragment transaction for ViewFragment
    private void showViewFragment(String name, String id){
        viewFragment = new ViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("id", id);
        viewFragment.setArguments(bundle);
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
        ft.replace(R.id.activity_frame, viewFragment, "fragment_view");
        ft.addToBackStack("fragment_view");
        ft.commit();
    }

    // Fragment transaction for Welcome Dialog
    private void showWelcomeDialog() {
        welcomeFragment = new WelcomeFragment();
        welcomeFragment.show(fm, "fragment_welcome");
    }

    // Fragment transaction for Subscriptions Dialog
    private void showSubsDialog() {
        subsFragment = new SubscriptionsFragment();
        subsFragment.show(fm, "fragment_subs");
    }

    // Fragment transaction for Add Dialog
    private void showAddDialog(){
        addFragment = new AddSubFragment();
        addFragment.show(fm, "fragment_add");
    }

    // Fragment transaction for About Dialog
    private void showAboutDialog(){
        AboutFragment aboutFragment = new AboutFragment();
        aboutFragment.show(fm, "fragment_about");
    }

    // Fragment transaction for SearchAdv Dialog
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
                // Clears entire backstack
                fm.popBackStack(null, fm.POP_BACK_STACK_INCLUSIVE);
                return true;
            case R.id.action_subs:
                showSubsDialog();
                return true;
            case R.id.action_reserv:
                openReservationsURL();
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

    // Simple function to open browser
    // with URL to timeedit room reservations
    private void openReservationsURL() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("http://web.timeedit.se/hig_no/db1/timeedit/sso/?ssoserver=feide&student"));
        startActivity(i);
    }

    private void dbTruncate(){
        //dbhelper.truncate(dbhelper.getWritableDatabase(), DBHelper.TABLE_SUBSCRIPTIONS);
        // Delete all lectures
        dbhelper.truncate(dbhelper.getWritableDatabase(), DBHelper.TABLE_LECTURES);
    }

    public void wclose(View view){
        // onClick function from welcomedialog
        welcomeFragment.dismiss();
        showWelcomeDialog();
    }

    public void aclose(View view){
        // onClick function from adddialog
        addFragment.getDialog().dismiss();
    }

    public void saclose(View view){
        // onClick function from searchadvdialog
        saFragment.getDialog().dismiss();
    }

    public void subAdd(){
        // onClick function from subscriptionsdialog
        subsFragment.getDialog().dismiss();
        showAddDialog();
    }

    @Override
    public void readyToUpdate() {
        // Listener function
        timeTableFragment.updateLectures();
    }

    @Override
    public void openTimeTable(String name, String ttid) {
        // Listener function
        showViewFragment(name, ttid);
    }

    // Getter for pulltorefreshattacher
    public PullToRefreshAttacher getPtra(){
        // Getter for pulltorefreshattacher
        return ptra;
    }

}
