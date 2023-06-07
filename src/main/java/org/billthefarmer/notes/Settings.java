////////////////////////////////////////////////////////////////////////////////
//
//  Notes - Personal notes for Android
//
//  Copyright (C) 2019	Bill Farmer
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
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

// Settings
public class Settings extends Activity
{
    public final static String PREF_NAME = "pref_name";
    public final static String PREF_THEME = "pref_theme";
    public final static String PREF_ABOUT = "pref_about";
    public final static String PREF_PATHS = "pref_paths";
    public final static String PREF_FOLDER = "pref_folder";
    public final static String PREF_EXTERNAL = "pref_external";
    public final static String PREF_NEW_NAME = "pref_new_name";
    public final static String PREF_NEW_TEMPLATE = "pref_new_template";
    public final static String PREF_USE_TEMPLATE = "pref_use_template";
    public final static String PREF_TEMPLATE_FILE = "pref_template_file";

    // onCreate
    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        int theme = Integer.parseInt(preferences.getString(PREF_THEME, "0"));

        Configuration config = getResources().getConfiguration();
        int night = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;

        switch (theme)
        {
        case Notes.LIGHT:
            setTheme(R.style.AppTheme);
            break;

        case Notes.DARK:
            setTheme(R.style.AppDarkTheme);
            break;

        case Notes.SYSTEM:
            switch (night)
            {
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);
                break;

            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppDarkTheme);
                break;
            }
            break;
        }

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();

        // Enable back navigation on action bar
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.settings);
        }
    }

    // onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Home, finish
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }

        return false;
    }
}
