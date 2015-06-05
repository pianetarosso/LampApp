package com.mfedele.lamp.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.mfedele.lamp.R;

/**
 * Created by Marco Fedele on 05/06/15.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
