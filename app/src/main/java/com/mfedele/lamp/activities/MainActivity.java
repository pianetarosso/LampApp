package com.mfedele.lamp.activities;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mfedele.lamp.R;
import com.mfedele.lamp.fragments.MainFragment;
import com.mfedele.lamp.fragments.SettingsFragment;
import com.mfedele.lamp.objects.ExtendedLampStatus;
import com.mfedele.lamp.objects.LampStatus;


public class MainActivity extends Activity implements
        ExtendedLampStatus.OnStatusUpdate,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = MainFragment.class.getSimpleName();

    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(
                        getString(R.string.sp_url),
                        getString(R.string.url_default_value)
                );

        getFragmentManager().beginTransaction().add(R.id.fragment_container, new MainFragment()).commit();
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

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, new SettingsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(getString(R.string.sp_url))) {
            url = sharedPreferences.getString(s, url);
            Log.d(TAG, url);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////


    // HERE YOU CAN PUT YOUR NETWORK CODE //////////////////////////////////////////////////////////

    // String "url" is always updated from the settings ;-)

    @Override
    public void onDataUpdate(LampStatus l) {
        // here we receive an object LampStatus every time there is an update

        Log.d(TAG, l.toString());
    }
}
