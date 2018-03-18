package com.sielski.marcin.bakingapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.sielski.marcin.bakingapp.util.BakingAppUtils;


public class BakingAppSettingsFragment extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {


    public BakingAppSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_baking_app);
        Preference preference = findPreference(getString(R.string.key_cse_api_key));
        preference.setSummary(BakingAppUtils.getCSEApiKey(getContext()));
        preference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        findPreference(key).setSummary(sharedPreferences.getString(key,""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String value = (String)newValue;
        if (value.length() != getResources().getInteger(R.integer.length_cse_api_key)) {
            BakingAppUtils.showSnackBar(getContext(),
                    getActivity().findViewById(R.id.fragment_baking_app_settings),
                    getString(R.string.snackbar_settings_cse_api_key));
            return false;
        }
        value = value.toLowerCase();
        if (!value.matches(BakingAppUtils.REGEX)) {
            BakingAppUtils.showSnackBar(getContext(),
                    getActivity().findViewById(R.id.fragment_baking_app_settings),
                    getString(R.string.snackbar_settings_cse_api_key));
            return false;
        }
        return true;
    }
}
