package com.tobbentm.higreader;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tobbentm.higreader.db.DBHelper;
import com.tobbentm.higreader.db.DBSubscriptions;
import com.tobbentm.higreader.db.DSSubscriptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

public class MainActivity extends Activity {

    FragmentManager fm = getFragmentManager();
    SubscriptionsFragment subsFragment;
    AddSubFragment addFragment;
    private DBHelper dbhelper = new DBHelper(this);
    //private DSLectures lecturesDatasource;
    private DSSubscriptions subscriptionsDatasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subscriptionsDatasource = new DSSubscriptions(this);
        //lecturesDatasource = new DSLectures(this);

        try {
            subscriptionsDatasource.open();
            //lecturesDatasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(subscriptionsDatasource.getSize() == 0){
            dbTruncate();
            showWelcomeDialog();
        }else{
            //updateLectures();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        subscriptionsDatasource.close();
        //lecturesDatasource.close();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void dbToast(){
        String msg = "DB: ";
        List<DBSubscriptions> list = subscriptionsDatasource.getSubscriptions();
        for (DBSubscriptions sub : list){
            msg += sub.toString() + "\t";
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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
    }
}
