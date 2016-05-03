# MaterialSearchView
Android SearchView based on Material Design guidelines. The MaterialSearchView will overlay a Toolbar or ActionBar as well as display a ListView for the user to show suggested or recent searches.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-MaterialSearchView-green.svg?style=true)](https://android-arsenal.com/details/1/3469)

## Download
To add the MaterialSearchView library to your Android Studio project, simply add the following gradle dependency:
```java
compile 'br.com.mauker.materialsearchview:materialsearchview:1.1.0'
```

This library is supported with a min SDK of 14.

## Usage

To open the search view on your app, add the following code to your layout:

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

To open the search view, simply call the `searchView.openSearch()` method.

To close the search view, call the `searchView.closeSearch()` method.

You can check if the view is open by using the `searchView.isOpen()` method.

**Protip:** To close the search view using the back button, put the following code on your `Activity`:

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

**Note:** To get the library to work, now you have to implement a class 
named `MsvAuthority` inside the `br.com.mauker` package on your app module,
and it should have a public static String variable called `CONTENT_AUTHORITY`.
Give it the value you want and don't forget to add the same name on your
manifest file.

## Search history and suggestions

You can provide search suggestions by using the following methods:

- `addSuggestions(String[] suggestions)`
- `addSuggestions(ArrayList<String> suggestions)`

To remove the search suggestions use:

- `clearSuggestions()`

The search history is automatically handled by the view, and it can be cleared by using:

- `clearHistory()`

You can also remove both by using the method below:

- `clearAll()`

## Styling the View

You can change how your MaterialSearchView looks like. To achieve that effect, try to add the following lines to your styles.xml:

```xml
<style name="MaterialSearchViewStyle">
    <item name="searchBackground">@color/white_ish</item>
    <item name="searchVoiceIcon">@drawable/ic_action_voice_search</item>
    <item name="searchCloseIcon">@drawable/ic_action_navigation_close</item>
    <item name="searchBackIcon">@drawable/ic_action_navigation_arrow_back</item>
    <item name="searchSuggestionBackground">@color/search_layover_bg</item>
    <item name="android:textColor">@color/black</item>
    <item name="android:textColorHint">@color/gray_50</item>
    <item name="android:hint">@string/search_hint</item>
</style>
```

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

- `OnQueryTextListener`: This interface handles either QueryTextChange or QueryTextSubmit events inside the MaterialSearchView.
- `SearchViewListener`: This interfaces handles the open or close events of the MaterialSearchView.


## Languages

The MaterialSearchView supports the following languages:

- English (en_US);
- Brazillian Portuguese (pt_BR).

## Sample
<img src='http://i.stack.imgur.com/C5LA4.gif' width='450' height='800' />

## Credits
This library was created by Maurício Pessoa with contributions from:
- [Adam McNeilly](http://adammcneilly.com)

JCenter version was made possible with help from:

- [Eric Cugota](https://github.com/tryadelion)

This project was inspired by the [MaterialSearchView](https://github.com/krishnakapil/MaterialSeachView) library by krishnakapil.

## License
The MaterialSearchView library is available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
