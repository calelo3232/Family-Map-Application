package com.example.familymapclient.userinterface.activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.example.familymapclient.R;
import com.example.familymapclient.application.DataCache;
import com.example.familymapclient.userinterface.activities.MainActivity;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        DataCache dataCache = DataCache.getInstance();

        SwitchPreferenceCompat lifeStorySwitch = findPreference("lifeStoryLines");
        lifeStorySwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setLifeStorySwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        SwitchPreferenceCompat familyTreeSwitch = findPreference("familyTreeLines");
        familyTreeSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setFamilyTreeSwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        SwitchPreferenceCompat spouseSwitch = findPreference("spouseLines");
        spouseSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setSpouseSwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        SwitchPreferenceCompat paternalSwitch = findPreference("paternalFilter");
        paternalSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setPaternalSwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        SwitchPreferenceCompat maternalSwitch = findPreference("maternalFilter");
        maternalSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setMaternalSwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        SwitchPreferenceCompat maleSwitch = findPreference("maleFilter");
        maleSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setMaleSwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        SwitchPreferenceCompat femaleSwitch = findPreference("femaleFilter");
        femaleSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            dataCache.setFemaleSwitch((Boolean) newValue);
            dataCache.setSettingsChangedFlag(true);
            return true;
        });

        Preference logout = findPreference("logout");
        logout.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        });
    }
}