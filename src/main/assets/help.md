# Notes
---

Android notebook

* Notes saved in text files
* Use markdown formatting
* Optional note template
* Display media
* Display [OpenStreetMap](https://www.openstreetmap.org) maps
* Share notes
* Add date and time
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

The toolbar items which won’t necessarily all appear at once:

* **New** &ndash; Start a new note
* **Save** &ndash; Save note
* **Open** &ndash; Open an existing note
* **Search** &ndash; incremental search of note

And on the menu:

* **Find all** &ndash; Find all notes containing search text
* **Open recent** &ndash; Pop up a list of recent files
  * **Clear list** &ndash; Clear list of recent files
* **Share** &ndash; Share note
* **Save as** &ndash; Save note in another file
* **Add date** &ndash; Add the current date and time
* **Add media** &ndash; Add media from media providers
* **Edit styles** &ndash; show an editor to edit the custom styles
* **Edit script** &ndash; show an editor to edit custom javascript
* **Backup** &ndash; backup notes to a zip file
* **Settings** &ndash; Show the settings

## Help
You may put a link `[Help](file:///android_asset/help.md)` to the help
file in the app assets in a note. The file will be loaded and may be
saved using the **Save as** menu entry.

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

## Scrolling
Scrolling the page up will temporarily hide the floating
button. Scrolling down restores it.

## Search
You may search notes, the search will update as text is entered into
the search field. Use the search widget or keyboard action button to
find the next match.

## Find all
You may find all notes that contain the current search
text. This menu item will only appear while the search widget is
active. A dialog will pop up with a list of matching notes. Touch an
entry to open that note. You may repeat this or refine the search
text to find the desired note.

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
added as a link. Media added from removable SD cards not part of the
device storage may work but may not be persistent and is not supported.

## LaTeX
Notes doesn't support [LaTeX](https://en.wikipedia.org/wiki/LaTeX),
but it is possible to use an external site to add LaTeX formatted
images.

    ![Math](http://www.sciweavers.org/tex2img.php?eq=\sum_{i=1}^{n}x_{i}^{2}&bc=cornsilk&fc=black&im=jpg&fs=24&ff=arev)

    \sum_{i=1}^{n}x_{i}^{2}

![Math](http://www.sciweavers.org/tex2img.php?eq=\sum_{i=1}^{n}x_{i}^{2}&bc=cornsilk&fc=black&im=jpg&fs=24&ff=arev)

If you don't intend to change the image, you can copy it and use that
instead, which will then work offline.

## Links
You may put external links in notes, so
`[DuckDuckGo](https://duckduckgo.com)` will show a link to
[DuckDuckGo](https://duckduckgo.com). Links will be followed if
touched. Use the back button in the toolbar or the navigation bar to
return to the note. Links to other notes may added as a relative
reference `[AnotherNote](AnotherNote.md)`.

## Maps
You may put [OpenStreetMap](https://www.openstreetmap.org) maps in
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

**Caution** &ndash; There is no such thing as a markdown syntax error, but
syntax errors in a styles file may cause unpredictable results and
affect all notes. See [CSS Tutorial](https://www.w3schools.com/Css).

You may include the built in styles file with an `@import` statement
`@import "file:///android_asset/styles.css";` or
`@import url("file:///android_asset/styles.css");`, which should be on
the first line.

## Javascript
You may add custom javascript to be loaded into all notes by placing a
`script.js` file in the `Notes/js` folder. Use the built in editor to
create a script file. You must use the editor `accept` tick button to
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

## Sync
Android cloud storage apps when last tested appeared not to be capable
of syncing a real storage folder on the device. However
[Syncthing](https://syncthing.net) does just that and can sync your
notes folder with other devices and desktop computers.

## Removable SD cards
Android allows removable SD cards to be used like a USB stick or as
part of the device storage. Storing notes on a removable SD card not
part of the device storage may work on some devices, but is not
supported. Adding media may also work, but may not be persistent.

## Settings
* **Folder** &ndash; Change notes storage folder. Caution &ndash;
    notes, styles and scripts will not be moved
* **Template** &ndash; Set a note template. Use the dialog to choose a
    file.
* **Browser** &ndash; Use browser for external links
* **Dark theme** &ndash; Use dark theme for editing
* **About** &ndash; Show app version, licence and credits
