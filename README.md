# MaterialSearchView
Android SearchView based on Material Design guidelines. The MaterialSearchView will overlay a Toolbar or ActionBar as well as display a ListView for the user to show suggested or recent searches.

[![Download](https://img.shields.io/badge/download-1.2.3-blue.svg)](https://bintray.com/mauker/maven/MaterialSearchView/_latestVersion)
[![APK size](https://img.shields.io/badge/Size-56%20KB-e91e63.svg)](http://www.methodscount.com/?lib=br.com.mauker.materialsearchview%3Amaterialsearchview%3A1.2.1)
[![Build Status](https://travis-ci.org/Mauker1/MaterialSearchView.svg?branch=master)](https://travis-ci.org/Mauker1/MaterialSearchView)



[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialSearchView-green.svg?style=true)](https://android-arsenal.com/details/1/3469)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/Mauker1/MaterialSearchView/blob/master/LICENSE)

<a href='https://ko-fi.com/A623L7G' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=f' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a> 

## Download
To add the MaterialSearchView library to your Android Studio project, simply add the following gradle dependency:
```java
implementation 'br.com.mauker.materialsearchview:materialsearchview:1.2.3'
```

This library is supported with a min SDK of 14.

**Important note:** If you're still using version 1.0.3, it's recommended to upgrade to the latest version as soon as possible. For more information, please see [this issue](https://github.com/Mauker1/MaterialSearchView/issues/7).

## Setup

Before you can use this lib, you have to implement a class named `MsvAuthority` inside the `br.com.mauker` package on your app module, and it should have a public static String variable called `CONTENT_AUTHORITY`. Give it the value you want and **don't forget** to add the same name on your manifest file. The lib will use this file to set the Content Provider authority.

**Example:**

**MsvAuthority.java**

```java
package br.com.mauker;

public class MsvAuthority {
    public static final String CONTENT_AUTHORITY = "br.com.mauker.materialsearchview.searchhistorydatabase";
}
```

Or if you're using Kotlin:

**MsvAuthority.kt**
```Kotlin
package br.com.mauker

object MsvAuthority {
    const val CONTENT_AUTHORITY: String = "br.com.mauker.materialsearchview.searchhistorydatabase"
}
```

**AndroidManifest.xml**

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ...>

    <application ... >
        <provider
        android:name="br.com.mauker.materialsearchview.db.HistoryProvider"
        android:authorities="br.com.mauker.materialsearchview.searchhistorydatabase"
        android:exported="false"
        android:protectionLevel="signature"
        android:syncable="true"/>
    </application>

</manifest>
```

**Proguard note:** Some of you might experience some problems with Proguard deleting the authority class, to solve those problems, add the following lines on your proguard file:

```
-keep class br.com.mauker.MsvAuthority
-keepclassmembers class br.com.mauker.** { *; }
```

## Usage

To open the search view on your app, add the following code **to the end of your layout**:

```xml
<br.com.mauker.materialsearchview.MaterialSearchView
    android:id="@+id/search_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```    

Then, inside your `Activity` get the reference:

```java
// Activity:
MaterialSearchView searchView = (MaterialSearchView) findViewById(R.id.search_view);
```

- To open the search view, simply call the `searchView.openSearch()` method.

- To close the search view, call the `searchView.closeSearch()` method.

- You can check if the view is open by using the `searchView.isOpen()` method.

- As from Version 1.2.1 it's also possible to get the query anytime by using the `searchView.getCurrentQuery()` method.

- To close the search view using the back button, put the following code on your `Activity`:

```java
@Override
public void onBackPressed() {
    if (searchView.isOpen()) {
        // Close the search on the back button press.
        searchView.closeSearch();
    } else {
        super.onBackPressed();
    }
}
```

For more examples on how to use this lib, [check the sample app code here](https://github.com/Mauker1/MaterialSearchView/blob/master/app/src/main/java/br/com/mauker/materialsearchview/app/MainActivity.java).

## Search history and suggestions

You can provide search suggestions by using the following methods:

- `addSuggestions(String[] suggestions)`
- `addSuggestions(ArrayList<String> suggestions)`

It's also possible to add a single suggestion using the following method:

- `addSuggestion(String suggestion)`

To remove all the search suggestions use:

- `clearSuggestions()`

And to remove a single suggestion, use the following method:

- `removeSuggestion(String suggestion)`

The search history is automatically handled by the view, and it can be cleared by using:

- `clearHistory()`

You can also remove both by using the method below:

- `clearAll()`

## Modifying the suggestion list behavior

The suggestion list is based on a `ListView`, and as such you can define the behavior of the item click by using the `MaterialSearchView#setOnItemClickListener()` method.

If you want to submit the query from the selected suggestion, you can use the snippet below:

```java
searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Do something when the suggestion list is clicked.
        String suggestion = searchView.getSuggestionAtPosition(position);

        searchView.setQuery(suggestion, true);
    }
});
```

If you just want to set the text on the search view text field when the user selects the suggestion, change the second argument from the  `searchView#setQuery()` from `true` to `false`.

## Styling the View

You can change how your MaterialSearchView looks like. To achieve that effect, try to add the following lines to your styles.xml:

```xml
<style name="MaterialSearchViewStyle">
        <style name="MaterialSearchViewStyle">
            <item name="searchBackground">@color/white_ish</item>
            <item name="searchVoiceIcon">@drawable/ic_action_voice_search</item>
            <item name="searchCloseIcon">@drawable/ic_action_navigation_close</item>
            <item name="searchBackIcon">@drawable/ic_action_navigation_arrow_back</item>
            <item name="searchSuggestionBackground">@color/search_layover_bg</item>
            <item name="historyIcon">@drawable/ic_history_white</item>
            <item name="suggestionIcon">@drawable/ic_action_search_white</item>
            <item name="listTextColor">@color/white_ish</item>
            <item name="searchBarHeight">?attr/actionBarSize</item>
            <item name="voiceHintPrompt">@string/hint_prompt</item>
            <item name="android:textColor">@color/black</item>
            <item name="android:textColorHint">@color/gray_50</item>
            <item name="android:hint">@string/search_hint</item>
            <item name="android:inputType">textCapWords</item>
        </style>
</style>
```

Alternatively, you can also style the Search View programmatically by calling the methods:

- `setBackgroundColor(int color);`
- `setTintAlpha(int alpha);`
- `setSearchBarColor(int color);`
- `setSearchBarHeight(int height);`
- `setTextColor(int color);`
- `setHintTextColor(int color);`
- `setHint(String hint);`
- `setVoiceHintPrompt(String voiceHint);`
- `setVoiceIcon(DrawableRes int resourceId);`
- `setClearIcon(DrawableRes int resourceId);`
- `setBackIcon(DrawableRes int resourceId);`
- `setSuggestionBackground(DrawableRes int resourceId);`
- `setHistoryIcon(@DrawableRes int resourceId);`
- `setSuggestionIcon(@DrawableRes int resourceId);`
- `setListTextColor(int color);`

And add this line on your `br.com.mauker.materialsearchview.MaterialSearchView` tag:

```xml
style="@style/MaterialSearchViewStyle"
```

So it'll look like:

```xml
<br.com.mauker.materialsearchview.MaterialSearchView
    android:id="@+id/search_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    style="@style/MaterialSearchViewStyle"/>
```

## Interfaces
Currently there are two interfaces that you can use to instantiate listeners for:

- `OnQueryTextListener`: Use this interface to handle QueryTextChange or QueryTextSubmit events inside the MaterialSearchView.
- `SearchViewListener`: You can use this interface to listen and handle the open or close events of the MaterialSearchView.


## Languages

The MaterialSearchView supports the following languages:

- English (en_US);
- Brazillian Portuguese (pt_BR);
- Italian (Thanks to [Francesco Donzello](https://github.com/wideawake));
- French (Thanks to [Robin](https://github.com/RobinPetit));
- Bosnian, Croatian and Serbian (Thanks to [Luke](https://github.com/luq-0));
- Spanish (Thanks to [Gloix](https://github.com/Gloix)).

## Sample GIF
<img src='http://i.stack.imgur.com/C5LA4.gif' width='450' height='800' />

## More Info

For more use cases, and some examples, you can [check the sample app](https://github.com/Mauker1/MaterialSearchView/tree/master/app/src/main/java/br/com/mauker/materialsearchview/app).

## Credits
This library was created by Maurício Pessoa with contributions from:
- [Adam McNeilly](http://adammcneilly.com)
- [Pier Betos](https://github.com/peterbetos)

JCenter version was made possible with help from:

- [Eric Cugota](https://github.com/tryadelion)

This project was inspired by the [MaterialSearchView](https://github.com/krishnakapil/MaterialSeachView) library by krishnakapil.

## License
The MaterialSearchView library is available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
