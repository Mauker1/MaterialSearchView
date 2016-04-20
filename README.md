# DISCLAIMER: This library is in the final stages of production. The information that follows is not complete, and may change at any moment.

# MaterialSearchView
Android SearchView based on Material Design guidelines. The MaterialSearchView will overlay a Toolbar or ActionBar as well as display a ListView for the user to show filtered, suggested, or recent searches.

## Usage
To add the MaterialSearchView library to your Android Studio project, simply add the following gradle dependency:
```java
  //TODO: This library is not yet released in JCenter.
```

## Interfaces
Currently there are two interfaces that you can use to instantiate listeners for:

- OnQueryTextListener: This interface handles either QueryTextChange or QueryTextSubmit events inside the MaterialSearchView.
- SearchViewListener: This interfaces handles the open or close events of the MaterialSearchView.

## Custom Attributes
A number of custom attributes can be applied to the MaterialSearchView inside of your XML layout:
- `searchBackground`: The background drawable for the view.
- `searchVoiceIcon`: The drawable resource for the voice search icon.
- `searchCloseIcon`: The drawable resource for the close search icon.
- `searchBackIcon`: The drawable resource for the back icon.
- `searchSuggestionBackground`: The background drawable for the suggestions ListView.
- `android:hint`: The hint that appears in the EditText of the search view.
- `android:textColor`: The text color of the EditText of the search view.
- `android:textColorHint`: The text color of the hint in the EditText of the search view.

## Sample
//TODO: Add a sample here.

## Credits
This library was created by Maurício Pessoa with contributions from:
- [Adam McNeilly](http://adammcneilly.com)

This project was inspired by the [MaterialSearchView](https://github.com/krishnakapil/MaterialSeachView) library by krishnakapil.

## License
The MaterialSearchView library is available under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).
