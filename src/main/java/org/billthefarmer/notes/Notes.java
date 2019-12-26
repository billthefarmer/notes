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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.ViewSwitcher;

import android.support.v4.content.FileProvider;

import org.billthefarmer.markdown.MarkdownView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.lang.ref.WeakReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Notes extends Activity
{
    public final static String TAG = "Notes";

    public final static String CHANGED = "changed";
    public final static String CONTENT = "content";

    private final static String STYLES = "file:///android_asset/styles.css";
    private final static String SCRIPT = "file:///android_asset/script.js";
    private final static String CSS_STYLES = "css/styles.css";
    private final static String TEXT_CSS = "text/css";
    private final static String JS_SCRIPT = "js/script.js";
    private final static String TEXT_JAVASCRIPT = "text/javascript";

    public final static String DOCUMENTS = "Documents";
    public final static String FOLDER = "Folder:  ";

    public final static String NOTES_FILE = "Notes.md";
    public final static String NOTES_FOLDER = "Notes";

    private static final int EDIT_TEXT = 0;
    private static final int MARKDOWN = 1;
    private static final int ACCEPT = 0;
    private static final int EDIT = 1;

    private final static int REQUEST_READ = 1;
    private final static int REQUEST_SAVE = 2;
    private final static int REQUEST_OPEN = 3;

    private final static int POSITION_DELAY = 128;
    private final static int MAX_PATHS = 10;

    private EditText textView;
    private ScrollView scrollView;

    private MarkdownView markdownView;
    private ViewSwitcher viewSwitcher;
    private ViewSwitcher buttonSwitcher;

    private SearchView searchView;
    private MenuItem searchItem;

    private View accept;
    private View edit;

    private Map<String, Integer> pathMap;
    private List<String> removeList;

    private String folder = NOTES_FOLDER;

    private File file;
    private String path;
    private Uri readUri;
    private Uri content;
    private String append;

    private boolean changed = false;
    private boolean shown = true;

    private long modified;

    // onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Get preferences
        SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(this);

        boolean dark =
                preferences.getBoolean(Settings.PREF_DARK_THEME, false);

        if (dark)
            setTheme(R.style.AppDarkTheme);

        setContentView(R.layout.main);

        textView = findViewById(R.id.text);
        scrollView = findViewById(R.id.scroll);
        markdownView = findViewById(R.id.markdown);

        accept = findViewById(R.id.accept);
        edit = findViewById(R.id.edit);

        viewSwitcher = findViewById(R.id.view_switcher);
        buttonSwitcher = findViewById(R.id.button_switcher);

        WebSettings settings = markdownView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        setListeners();
    }

    // onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        markdownView.restoreState(savedInstanceState);
    }

    // onResume
    @Override
    protected void onResume()
    {
        super.onResume();

        // Clear cache
        markdownView.clearCache(true);

        if (changed)
            loadMarkdown();

        setVisibility();
    }

    // onSaveInstanceState
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        markdownView.saveState(outState);
    }

    // onPause
    @Override
    public void onPause()
    {
        super.onPause();

        if (changed)
            save();
    }

    // onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        searchItem = menu.findItem(R.id.search);

        // Set up search view
        if (searchItem != null)
            searchView = (SearchView) searchItem.getActionView();

        // Set up search view options and listener
        if (searchView != null)
        {
            searchView.setSubmitButtonEnabled(true);
            searchView.setImeOptions(EditorInfo.IME_ACTION_GO);
            searchView.setOnQueryTextListener(new QueryTextListener());
        }

        return true;
    }

    // onOptionsItemSelected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        case android.R.id.home:
            onBackPressed();
            break;
        case R.id.settings:
            settings();
            break;
        default:
            return super.onOptionsItemSelected(item);
        }

        // Close text search
        if (searchItem.isActionViewExpanded())
            searchItem.collapseActionView();

        return true;
    }

    // onBackPressed
    @Override
    public void onBackPressed()
    {
        // External
        if (markdownView.canGoBack())
            markdownView.goBack();

        else
            super.onBackPressed();
    }

    private void setListeners()
    {
        if (textView != null)
            textView.addTextChangedListener(new TextWatcher()
        {
            // afterTextChanged
            @Override
            public void afterTextChanged(Editable s)
            {
                // Text changed
                changed = true;
            }

            // beforeTextChanged
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after)
            {
            }

            // onTextChanged
            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count)
            {
            }
        });

        if (markdownView != null)
        {
            // On long click
            markdownView.setOnLongClickListener(v ->
                {
                    // Reveal button
                    edit.setVisibility(View.VISIBLE);
                    return false;
                });
        }

        if (accept != null)
        {
            // On click
            accept.setOnClickListener(v ->
            {
                // Check flag
                if (changed)
                {
                    // Save text
                    save();
                    // Get text
                    loadMarkdown();
                    // Clear flag
                    changed = false;
                }

                // Animation
                animateAccept();

                // Close text search
                if (searchItem.isActionViewExpanded())
                    searchItem.collapseActionView();

                shown = true;
            });

            // On long click
            accept.setOnLongClickListener(v ->
            {
                // Hide button
                v.setVisibility(View.INVISIBLE);
                return true;
            });
        }

        if (edit != null)
        {
            // On click
            edit.setOnClickListener(v ->
            {
                // Animation
                animateEdit();

                // Close text search
                if (searchItem.isActionViewExpanded())
                    searchItem.collapseActionView();

                // Scroll after delay
                edit.postDelayed(() ->
                {
                    // Get selection
                    int selection = textView.getSelectionStart();

                    // Get text position
                    int line = textView.getLayout().getLineForOffset(selection);
                    int position = textView.getLayout().getLineBaseline(line);

                    // Scroll to it
                    int height = scrollView.getHeight();
                    scrollView.smoothScrollTo(0, position - height / 2);
                }, POSITION_DELAY);

                shown = false;
            });

            // On long click
            edit.setOnLongClickListener(v ->
            {
                // Hide button
                v.setVisibility(View.INVISIBLE);
                return true;
            });
        }

        if (textView != null)
        {
            // onFocusChange
            textView.setOnFocusChangeListener((v, hasFocus) ->
            {
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager)
                    getSystemService(INPUT_METHOD_SERVICE);
                if (!hasFocus)
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            });

            // On long click
            textView.setOnLongClickListener(v ->
            {
                // Reveal button
                accept.setVisibility(View.VISIBLE);
                return false;
            });
        }
    }

    // animateAccept
    public void animateAccept()
    {
        // Animation
        viewSwitcher.setDisplayedChild(MARKDOWN);
        buttonSwitcher.setDisplayedChild(EDIT);
    }

    // animateEdit
    private void animateEdit()
    {
        // Animation
        viewSwitcher.setDisplayedChild(EDIT_TEXT);
        buttonSwitcher.setDisplayedChild(ACCEPT);
    }

    // loadMarkdown
    private void loadMarkdown()
    {
        CharSequence text = textView.getText();
        loadMarkdown(text);
    }

    // loadMarkdown
    private void loadMarkdown(CharSequence text)
    {
        markdownView.loadMarkdown(getBaseUrl(), markdownCheck(text),
                                  getStyles(), getScript());
    }

    // getBaseUrl
    private String getBaseUrl()
    {
        return Uri.fromFile(getHome()).toString() + File.separator;
    }

    // settings
    private void settings()
    {
        startActivity(new Intent(this, Settings.class));
    }

    // getHome
    private File getHome()
    {
        File file = new File(folder);
        if (file.isAbsolute() && file.isDirectory() && file.canWrite())
            return file;

        return new File(Environment.getExternalStorageDirectory(), folder);
    }

    // markdownCheck
    private String markdownCheck(CharSequence text)
    {
        return text.toString();
    }

    // getStyles
    private String getStyles()
    {
        File cssFile = new File(getHome(), CSS_STYLES);

        if (cssFile.exists())
            return Uri.fromFile(cssFile).toString();

        return STYLES;
    }

    // getScript
    private String getScript()
    {
        File jsFile = new File(getHome(), JS_SCRIPT);

        if (jsFile.exists())
            return Uri.fromFile(jsFile).toString();

        return null;
    }

    // setVisibility
    private void setVisibility()
    {
        buttonSwitcher.setVisibility(View.VISIBLE);
        // Check if shown
        if (shown)
        {
            viewSwitcher.setDisplayedChild(MARKDOWN);
            buttonSwitcher.setDisplayedChild(EDIT);
        }
        else
        {
            viewSwitcher.setDisplayedChild(EDIT_TEXT);
            buttonSwitcher.setDisplayedChild(ACCEPT);
        }
    }

    // editStyles
    public void editStyles()
    {
        File file = new File(getHome(), CSS_STYLES);

        // Get file provider uri
        Uri uri = FileProvider.getUriForFile
            (this, "org.billthefarmer.notes.fileprovider", file);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Path " + uri.getPath());

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(uri, TEXT_CSS);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(intent);
    }

    // editScript
    public void editScript()
    {
        File file = new File(getHome(), JS_SCRIPT);

        // Get file provider uri
        Uri uri = FileProvider.getUriForFile
            (this, "org.billthefarmer.notes.fileprovider", file);
        if (BuildConfig.DEBUG)
            Log.d(TAG, "Path " + uri.getPath());

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(uri, TEXT_JAVASCRIPT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(intent);
    }

    // openFile
    private void openFile()
    {
        // Check if file changed
        if (changed)
            alertDialog(R.string.open, R.string.modified,
                        R.string.save, R.string.discard, (dialog, id) ->
        {
            switch (id)
            {
            case DialogInterface.BUTTON_POSITIVE:
                saveFile();
                getFile();
                break;

            case DialogInterface.BUTTON_NEGATIVE:
                changed = false;
                getFile();
                break;
            }
        });

        else
            getFile();

    }

    // alertDialog
    private void alertDialog(int title, int message,
                             int positiveButton, int negativeButton,
                             DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        // Add the buttons
        builder.setPositiveButton(positiveButton, listener);
        builder.setNegativeButton(negativeButton, listener);

        // Create the AlertDialog
        builder.show();
    }

    // alertDialog
    private void alertDialog(int title, String message, int neutralButton)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);

        // Add the buttons
        builder.setNeutralButton(neutralButton, null);

        // Create the AlertDialog
        builder.show();
    }

    // getFile
    private void getFile()
    {
        // Check permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_OPEN);
                return;
            }
        }

        // Open parent folder
        File dir = file.getParentFile();
        getFile(dir);
    }

    // getFile
    private void getFile(File dir)
    {
        // Get list of files
        List<File> list = getList(dir);
        if (list == null)
            return;

        // Pop up dialog
        String title = FOLDER + dir.getPath();
        openDialog(title, list, (dialog, which) ->
            {
                File selection = list.get(which);
                if (selection.isDirectory())
                    getFile(selection);

                else
                    readFile(Uri.fromFile(selection));
            });
    }

    // getList
    private List<File> getList(File dir)
    {
        List<File> list = null;
        File[] files = dir.listFiles();
        // Check files
        if (files == null)
        {
            // Create a list with just the parent folder and the
            // external storage folder
            list = new ArrayList<File>();

            if (dir.getParentFile() == null)
                list.add(dir);

            else
                list.add(dir.getParentFile());

            list.add(Environment.getExternalStorageDirectory());
            return list;
        }

        // Sort the files
        Arrays.sort(files);
        // Create a list
        list = new ArrayList<File>(Arrays.asList(files));
        // Remove hidden files
        Iterator<File> iterator = list.iterator();
        while (iterator.hasNext())
        {
            File item = iterator.next();
            if (item.getName().startsWith("."))
                iterator.remove();
        }

        // Add parent folder
        if (dir.getParentFile() == null)
            list.add(0, dir);

        else
            list.add(0, dir.getParentFile());

        return list;
    }

    // openDialog
    private void openDialog(String title, List<File> list,
                            DialogInterface.OnClickListener listener)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Add the adapter
        FileAdapter adapter = new FileAdapter(builder.getContext(), list);
        builder.setAdapter(adapter, listener);

        // Add the button
        builder.setNegativeButton(R.string.cancel, null);

        // Create the Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // onRequestPermissionsResult
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults)
    {
        switch (requestCode)
        {
        case REQUEST_SAVE:
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission
                                          .WRITE_EXTERNAL_STORAGE) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    // Granted, save file
                    saveFile();
            break;

        case REQUEST_READ:
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission
                                          .READ_EXTERNAL_STORAGE) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    // Granted, read file
                    readFile(readUri);
            break;

        case REQUEST_OPEN:
            for (int i = 0; i < grantResults.length; i++)
                if (permissions[i].equals(Manifest.permission
                                          .READ_EXTERNAL_STORAGE) &&
                    grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    // Granted, open file
                    getFile();
            break;
        }
    }

    // readFile
    private void readFile(Uri uri)
    {
        if (uri == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ);
                readUri = uri;
                return;
            }
        }

        content = null;

        // Attempt to resolve content uri
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
            uri = resolveContent(uri);

        // Read into default file if unresolved
        if (CONTENT.equalsIgnoreCase(uri.getScheme()))
        {
            content = uri;
            file = getDefaultFile();
            Uri defaultUri = Uri.fromFile(file);
            path = defaultUri.getPath();

            String title = uri.getLastPathSegment();
            setTitle(title);
        }

        // Read file
        else
        {
            path = uri.getPath();
            file = new File(path);

            String title = uri.getLastPathSegment();
            setTitle(title);
        }

        textView.setText(R.string.loading);

        ReadTask read = new ReadTask(this);
        read.execute(uri);

        changed = false;
        modified = file.lastModified();
        savePath(path);
        invalidateOptionsMenu();
    }

    // resolveContent
    private Uri resolveContent(Uri uri)
    {
        String path = FileUtils.getPath(this, uri);

        if (path != null)
        {
            File file = new File(path);
            if (file.canRead())
                uri = Uri.fromFile(file);
        }

        return uri;
    }

    // getDefaultFile
    private File getDefaultFile()
    {
        return new File(getHome(), NOTES_FILE);
    }

    // defaultFile
    private void defaultFile(String text)
    {
        file = getDefaultFile();

        Uri uri = Uri.fromFile(file);
        path = uri.getPath();

        if (file.exists())
        {
            readFile(uri);
            append = text;
        }

        else
        {
            if (text != null)
                textView.append(text);

            String title = uri.getLastPathSegment();
            setTitle(title);
        }
    }

    // savePath
    private void savePath(String path)
    {
        // Save the current position
        pathMap.put(path, scrollView.getScrollY());

        // Get a list of files
        List<Long> list = new ArrayList<>();
        Map<Long, String> map = new HashMap<>();
        for (String name : pathMap.keySet())
        {
            File file = new File(name);
            list.add(file.lastModified());
            map.put(file.lastModified(), name);
        }

        // Sort in reverse order
        Collections.sort(list);
        Collections.reverse(list);

        int count = 0;
        for (long date : list)
        {
            String name = map.get(date);

            // Remove old files
            if (count >= MAX_PATHS)
            {
                pathMap.remove(name);
                removeList.add(name);
            }

            count++;
        }
    }

    // save
    private void save()
    {
    }

    // saveFile
    private void saveFile()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                     Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SAVE);
                return;
            }
        }

        if (file.lastModified() > modified)
            alertDialog(R.string.appName, R.string.changedOverwrite,
                        R.string.overwrite, R.string.cancel, (dialog, id) ->
        {
            switch (id)
            {
            case DialogInterface.BUTTON_POSITIVE:
                saveFile(file);
                break;
            }
        });

        else
        {
            if (content == null)
                saveFile(file);

            else
                saveFile(content);
        }
    }

    // saveFile
    private void saveFile(File file)
    {
        CharSequence text = textView.getText();
        write(text, file);
    }

    // saveFile
    private void saveFile(Uri uri)
    {
        CharSequence text = textView.getText();
        try (OutputStream outputStream =
             getContentResolver().openOutputStream(uri))
        {
            write(text, outputStream);
        }

        catch (Exception e)
        {
            alertDialog(R.string.appName, e.getMessage(), R.string.ok);
            e.printStackTrace();
            return;
        }
    }

    // write
    private void write(CharSequence text, File file)
    {
        file.getParentFile().mkdirs();
        try (FileWriter fileWriter = new FileWriter(file))
        {
            fileWriter.append(text);
        }

        catch (Exception e)
        {
            alertDialog(R.string.appName, e.getMessage(), R.string.ok);
            e.printStackTrace();
            return;
        }

        changed = false;
        invalidateOptionsMenu();
        modified = file.lastModified();
        savePath(file.getPath());
    }

    // write
    private void write(CharSequence text, OutputStream os)
    {
        try (OutputStreamWriter writer = new OutputStreamWriter(os))
        {
            writer.append(text);
        }

        catch (Exception e)
        {
            alertDialog(R.string.appName, e.getMessage(), R.string.ok);
            e.printStackTrace();
            return;
        }

        changed = false;
        invalidateOptionsMenu();
    }

    // loadText
    private void loadText(CharSequence text)
    {
        if (textView != null)
            textView.setText(text);

        if (append != null)
        {
            textView.append(append);
            append = null;
            changed = true;
        }

        else
            changed = false;

        // Check for saved position
        if (pathMap.containsKey(path))
            textView.postDelayed(() ->
                                 scrollView.smoothScrollTo
                                 (0, pathMap.get(path)),
                                 POSITION_DELAY);
        else
            textView.postDelayed(() ->
                                 scrollView.smoothScrollTo(0, 0),
                                 POSITION_DELAY);

        // Dismiss keyboard
        textView.clearFocus();

        // Update menu
        invalidateOptionsMenu();
    }

    // QueryTextListener
    private class QueryTextListener
        implements SearchView.OnQueryTextListener
    {
        private BackgroundColorSpan span = new
        BackgroundColorSpan(Color.YELLOW);
        private Editable editable;
        private Pattern pattern;
        private Matcher matcher;
        private int index;
        private int height;

        // onQueryTextChange
        @Override
        @SuppressWarnings("deprecation")
        public boolean onQueryTextChange(String newText)
        {
            // Use web view functionality
            if (shown)
                markdownView.findAll(newText);

            // Use regex search and spannable for highlighting
            else
            {
                height = scrollView.getHeight();
                editable = textView.getEditableText();

                // Reset the index and clear highlighting
                if (newText.length() == 0)
                {
                    index = 0;
                    editable.removeSpan(span);
                }

                // Get pattern
                pattern = Pattern.compile(newText,
                                          Pattern.CASE_INSENSITIVE |
                                          Pattern.LITERAL |
                                          Pattern.UNICODE_CASE);
                // Find text
                matcher = pattern.matcher(editable);
                if (matcher.find(index))
                {
                    // Get index
                    index = matcher.start();

                    // Get text position
                    int line = textView.getLayout().getLineForOffset(index);
                    int position = textView.getLayout().getLineBaseline(line);

                    // Scroll to it
                    scrollView.smoothScrollTo(0, position - height / 2);

                    // Highlight it
                    editable.setSpan(span, matcher.start(), matcher.end(),
                                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            return true;
        }

        // onQueryTextSubmit
        @Override
        public boolean onQueryTextSubmit(String query)
        {
            // Use web view functionality
            if (shown)
                markdownView.findNext(true);

            // Use regex search and spannable for highlighting
            else
            {
                // Find next text
                if (matcher.find())
                {
                    // Get index
                    index = matcher.start();

                    // Get text position
                    int line = textView.getLayout().getLineForOffset(index);
                    int position = textView.getLayout().getLineBaseline(line);

                    // Scroll to it
                    scrollView.smoothScrollTo(0, position - height / 2);

                    // Highlight it
                    editable.setSpan(span, matcher.start(), matcher.end(),
                                     Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                // Reset matcher
                if (matcher.hitEnd())
                    matcher.reset();
            }

            return true;
        }
    }

    // ReadTask
    private static class ReadTask
        extends AsyncTask<Uri, Void, CharSequence>
    {
        private WeakReference<Notes> notesWeakReference;

        public ReadTask(Notes notes)
        {
            notesWeakReference = new WeakReference<>(notes);
        }

        // doInBackground
        @Override
        protected CharSequence doInBackground(Uri... uris)
        {
            StringBuilder stringBuilder = new StringBuilder();
            final Notes notes = notesWeakReference.get();
            if (notes == null)
                return stringBuilder;

            try (InputStream inputStream = notes.getContentResolver()
                 .openInputStream(uris[0]);
                 BufferedReader reader = new BufferedReader
                 (new InputStreamReader(inputStream)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                {
                    stringBuilder.append(line);
                    stringBuilder.append(System.getProperty("line.separator"));
                }
            }

            catch (Exception e)
            {
                notes.textView.post(() ->
                                     notes.alertDialog(R.string.appName,
                                                        e.getMessage(),
                                                        R.string.ok));
                e.printStackTrace();
            }

            return stringBuilder;
        }

        // onPostExecute
        @Override
        protected void onPostExecute(CharSequence result)
        {
            final Notes notes = notesWeakReference.get();
            if (notes == null)
                return;

            notes.loadText(result);
        }
    }
}
