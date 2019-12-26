////////////////////////////////////////////////////////////////////////////////
//
//  Notes - Personal notes for Android
//
//  Copyright (C) 2017	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.notes;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.text.DateFormat;
import java.util.Date;

// SettingsFragment class
@SuppressWarnings("deprecation")
public class SettingsFragment extends android.preference.PreferenceFragment
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Get folder summary
        EditTextPreference folder =
            (EditTextPreference) findPreference(Settings.PREF_FOLDER);

        // Set folder in text view
        folder.setSummary(preferences.getString(Settings.PREF_FOLDER,
                                                Notes.NOTES_FOLDER));
        // Get template preference
        EditTextPreference entry =
            (EditTextPreference) findPreference(Settings.PREF_TEMPLATE_FILE);

        // Set template in text view
        entry.setSummary(preferences.getString(Settings.PREF_TEMPLATE_FILE,
                                               Notes.NOTES_FILE));
        // Get about summary
        Preference about = findPreference(Settings.PREF_ABOUT);
        String sum = about.getSummary().toString();

        // Set version in text view
        String s = String.format(sum, BuildConfig.VERSION_NAME);
        about.setSummary(s);
    }

    // on Resume
    @Override
    public void onResume()
    {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
            .registerOnSharedPreferenceChangeListener(this);
    }

    // on Pause
    @Override
    public void onPause()
    {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
            .unregisterOnSharedPreferenceChangeListener(this);
    }

    // On preference tree click
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference)
    {
        boolean result =
            super.onPreferenceTreeClick(preferenceScreen, preference);

        // Set home as up
        if (preference instanceof PreferenceScreen)
        {
            Dialog dialog = ((PreferenceScreen) preference).getDialog();
            ActionBar actionBar = dialog.getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
        }

        return result;
    }

    // On shared preference changed
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences,
                                          String key)
    {
        if (key.equals(Settings.PREF_FOLDER))
        {
            // Get folder summary
            EditTextPreference folder =
                (EditTextPreference) findPreference(key);

            // Set folder in text view
            folder.setSummary(preferences.getString(key, Notes.NOTES_FOLDER));
        }

        if (key.equals(Settings.PREF_TEMPLATE_FILE))
        {
            // Get template preference
            EditTextPreference entry =
                (EditTextPreference) findPreference(key);

            
            // Set folder in text view
            entry.setSummary(preferences.getString(key, Notes.NOTES_FILE));
        }

        if (key.equals(Settings.PREF_DARK_THEME))
        {
            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M)
                getActivity().recreate();
        }
    }
}
