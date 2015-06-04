package com.mfedele.lamp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mfedele.lamp.objects.ExtendedLampStatus;
import com.mfedele.lamp.objects.LampStatus;


public class MainActivity extends ActionBarActivity implements ExtendedLampStatus.OnStatusUpdate {

    private static final String TAG = MainFragment.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataUpdate(LampStatus l) {
        // here we receive an object LampStatus every time there is an update in the
        // params. In the class LampStatus there is a parameter
        // which defines the minimum time interval for updates, to avoid too much
        // calls to the network.

        Log.d(TAG, l.toString());
    }
}
