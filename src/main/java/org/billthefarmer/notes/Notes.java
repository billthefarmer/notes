////////////////////////////////////////////////////////////////////////////////
//
//  Notes - Notebook for Android
//
//  Copyright © 2019  Bill Farmer
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
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
////////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.notes;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

public class Notes extends Activity
{
    public final static String TAG = "Notes";
    public final static String CHANGED = "changed";
    public final static String CONTENT = "content";
    public final static String PREF_DARK = "dark";

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean dark =
                preferences.getBoolean(PREF_DARK, false);

        if (dark)
            setTheme(R.style.AppDarkTheme);

        setContentView(R.layout.editor);
    }
}
