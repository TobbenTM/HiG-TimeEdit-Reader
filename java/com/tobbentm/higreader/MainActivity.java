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

public class MainActivity extends Activity implements WelcomeFragment.readyToUpdateListener, AddSubFragment.readyToUpdateListener {

    FragmentManager fm = getFragmentManager();
    SubscriptionsFragment subsFragment;
    AddSubFragment addFragment;
    TimeTableFragment timeTableFragment;
    private DBHelper dbhelper = new DBHelper(this);
    private DSSubscriptions subscriptionsDatasource;
    //private DSSettings settingsDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subscriptionsDatasource = new DSSubscriptions(this);
        //settingsDatasource = new DSSettings(this);

        try {
            subscriptionsDatasource.open();
            //settingsDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
        //settingsDatasource.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            subscriptionsDatasource.open();
            //settingsDatasource.open();
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

    private void showWelcomeDialog() {
        WelcomeFragment welcomeFragment = new WelcomeFragment();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_addsearch:
                showSubsDialog();
                return true;
            case R.id.action_settings:
                showAboutDialog();
                return true;
            case R.id.action_search:
                //TODO: Show search dialog
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dbTruncate(){
        /*try {
            backupDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //dbhelper.truncate(dbhelper.getWritableDatabase(), DBHelper.TABLE_SUBSCRIPTIONS);
        dbhelper.truncate(dbhelper.getWritableDatabase(), DBHelper.TABLE_LECTURES);
    }

    public void wclose(View view){
        finish();
    }

    public void aclose(View view){
        addFragment.getDialog().dismiss();
    }

    public void subAdd(View view){
        subsFragment.getDialog().dismiss();
        showAddDialog();
    }

    @Override
    public void readyToUpdate() {
        timeTableFragment.updateLectures();
    }

    /*
    Yay for commented code!!

    public static void backupDatabase() throws IOException {
        //Open your local db as the input stream
        String inFileName = "/data/data/com.tobbentm.higreader/databases/higreader.db";
        File dbFile = new File(inFileName);
        FileInputStream fis = new FileInputStream(dbFile);

        String outFileName = Environment.getExternalStorageDirectory()
                + "/database.sqlite";
        //Open the empty db as the output stream
        OutputStream output = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer))>0){
            output.write(buffer, 0, length);
        }
        //Close the streams
        output.flush();
        output.close();
        fis.close();
    }*/
}
