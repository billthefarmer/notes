# ![Logo](src/main/res/drawable-hdpi/ic_launcher.png) Notes [![Build Status](https://travis-ci.org/billthefarmer/notes.svg?branch=master)](https://travis-ci.org/billthefarmer/notes)

Android notepad

![Notes](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Notes.png)
![Notes](https://github.com/billthefarmer/billthefarmer.github.io/raw/master/images/Notes-settings.png)

* Notes saved in text files
* Use markdown formatting
* Optional note template
* Display media
* Display [OpenStreetMap](http://www.openstreetmap.org/) maps
* Share notes
* Add media from media providers
* Receive media from other apps
* Receive geo uris from other apps
* Incremental search of notes
* Dark or light theme for editing
* Optional CSS styles
* Optional JavaScript scripts
* Back up notes to a zip file
* Optional edit cursor position control

## Toolbar

The toolbar icons are, from left to right:

* **New** - Start a new note
* **Save** - Save note
* **Open** - Open an existing note
* **Search** - incremental search of note
* **Share** - Share note
* **Save as** - Save note in another file
* **Add time** - Add the current time
* **Add media** - Add media from media providers
* **Edit styles** - show an editor to edit the custom styles
* **Edit script** - show an editor to edit custom javascript
* **Backup** - backup notes to a zip file
* **Settings** - Show the settings

Depending on the device and orientation, some items will be on the
menu.

## Double tap
In the formatted view a double tap on the screen will switch to the
edit view in approximately the same position in the markdown text. The
accuracy is dependent on the text formatting and media in the note.

## Editing
In the formatted view the **Edit** button floating above the page
allows editing notes. The **Accept** button restores the formatted
view. A long touch on the button hides it until the device is rotated
or a long touch on the page.

See [Markdown](https://daringfireball.net/projects/markdown) for
markdown syntax.

## Search
You may search notes, the search will update as text is entered into
the search field. Use the search widget or keyboard action button for
find next.

## Template
You may use a note template. If a template is set it will be copied to
a new note.

## Text
You may receive text clips from another app. The text will be added at
the current cursor position, or in a new note if no note is open.

## Media
You may store media in the note storage folder and reference them in
notes, so markdown text `![cat](cat.jpg)` will display `cat.jpg`
stored in the note folder. You may either add media from media
providers like file managers or image managers or receive media sent
by other apps. Media added will be added at the current cursor
position. Media sent by other apps will be added at the current cursor
position, or in a new note if no note is open. Content URIs
(`content://`) sent by some media providers and apps will be resolved
to file URIs (`file:///`) if possible. Media will be added using
markdown image syntax (`![<name>](<url>)`), which will be parsed into
an HTML5 media player, text clips will be added as above, URLs will be
added as a link.

## LaTeX
Notes doesn't support [LaTeX](https://en.wikipedia.org/wiki/LaTeX),
but it is possible to use an external site to add LaTeX formatted
images.

    ![Math](http://www.sciweavers.org/tex2img.php?eq=\sum_{i=1}^{n}x_{i}^{2}&bc=cornsilk&fc=black&im=jpg&fs=24&ff=arev)

    \sum_{i=1}^{n}x_{i}^{2}

![Math](http://www.sciweavers.org/tex2img.php?eq=\sum_{i=1}^{n}x_{i}^{2}&bc=cornsilk&fc=black&im=jpg&fs=24&ff=arev)

## Links
You may put external links in notes, so
`[DuckDuckGo](https://duckduckgo.com)` will show a link to
[DuckDuckGo](https://duckduckgo.com). Links will be followed if
touched. Use the back button in the toolbar or the navigation bar to
return to the note. Links to other notes may added as a relative
reference `[AnotherNote](AnotherNote.md)`.

## Maps
You may put [OpenStreetMap](http://www.openstreetmap.org) maps in
notes with a geo URI `![osm](geo:<lat>,<lng>)`. Geo uris received from
other apps will be added at the current cursor position, or in a new
note if no note is open.

## Cursor position
You may put a line in a note to control or remember the edit cursor
position. This will not appear in the markdown view. Put `[<]: #` on a
line for the start of an note, `[>]: #` for the end of a note. Put
`[#]: #` for the cursor position to be remembered. There should be no
other text on the line. The current cursor position will be added when
the note is saved `[#]: # (nnn)`. Because notes are only saved if they
are changed, moving the cursor with no other change will not move the
saved position.

## Styles
You may add custom styles to the markdown formatting by placing a
`styles.css` file in the `Notes/css` folder, which will replace the
built in styles file which simply limits the width of images to the
page width. Use the built in editor to create a styles file. You must
use the editor `accept` tick button to save the edits.

**Caution** - There is no such thing as a markdown syntax error, but
syntax errors in a styles file may cause unpredictable results and
affect all notes. See [CSS Tutorial](https://www.w3schools.com/Css).

You may include the built in styles file with an `@import` statement
`@import "file:///android_asset/styles.css";` or
`@import url("file:///android_asset/styles.css");`, which should be on
the first line.

## Javascript
You may add custom javascript to be loaded into all notes by placing a
`script.js` file in the `Notes/js` folder. Use the built in editor to
create a script file.  You must use the editor `accept` tick button to
save the edits. Errors in the script will be logged by the
[WebView](https://developer.android.com/reference/android/webkit/WebView)
which displays the page. See [Javascript
tutorial](https://www.w3schools.com/js).

If you want to use javascript libraries or write a large script it
might be a good idea to use the Google
[Closure Compiler](https://developers.google.com/closure/compiler) to
check and minimise your code. It will handle multiple input files.

## Backup
You may create a backup of all your notes in a zip file. The file
will have the same name as the notes folder, default `Notes.zip`.

## Settings
* **Folder** - Change notes storage folder. Caution - notes, styles
    and scripts will not be moved
* **Template** - Set a note template. Use the dialog to choose a file.
* **Browser** - Use browser for external links
* **Dark theme** - Use dark theme for editing
* **About** - Show app version, licence and credits
