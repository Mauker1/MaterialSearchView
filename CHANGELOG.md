Change Log
==========

## Version 1.2.1

_2017-03-20_

* Fixed a bug where MSV was crashing on devices with Kitkat or older due to the use of Vector Drawables. For more info on this issue, check [issue #92](https://github.com/Mauker1/MaterialSearchView/issues/92).

## Version 1.2.1

_2017-03-16_

* You can now get the default adapter by calling `MaterialSearchView#getAdapter()` and have more control over it;
* Added the `getCurrentQuery()` method, making it possible to get the query anywhere in the application;
* Added the `setCloseOnTintClick(boolean)` method. If you set it to `true`, a touch outside the result list will close the `MaterialSearchView`, it'll remain open otherwise;
* Added the `setSearchBarColor(int color)` method, where you can change specifically the search bar color;
* The `saveQueryToDb()` method is now public, giving the programmer more control over when to save the queries;
* The `setTintBackground(int color)` method is now private. It's been replaced by the public method `setBackgroundColor(int color)`;
* Added French, Dutch, Bosnian, Croatian and Serbian translations.

## Version 1.2.0

_2016-10-27_

* Suggestions can now be inserted and removed one by one;
* It's now possible to change the voice hint prompt and the searchBarHeight;
* MSV now have RTL support;
* MSV now is correctly displayed with transparent status bar;
* `CoordinatorLayout` was dropped. Now `FrameLayout` is the new root for MSV;
* Added italian translation;
* Bunch of bug fixes (See [this link](https://github.com/Mauker1/MaterialSearchView/milestone/2) for more information).

## Version 1.1.3

_2016-08-14_

* Fixed a bug where `onQueryTextChanged` interface method was called multiple times;
* Added `OnItemLongClickListener` on history/suggestion list;
* Implemented an `OnClickListener` to the voice search icon, so it's possible to change the click behavior.

## Version 1.1.2

_2016-07-19_

* Now it's possible to change the Search View input type.
* Added the `getItemAtPosition()` method to simplify how you get the list item String.

**e.g.:** `search_view.setInputType(InputType.TYPE_CLASS_TEXT);`

## Version 1.1.1

_2016-06-23_

* Just some bug fixes.

## Version 1.1.0

_2016-05-08_

* Solved the issue with the Content Provider authority, now it's possible to install multiple instances of this lib (Fix #7).
* Added support for a transparent suggestion list.
* Added support for styles, so now it's possible to change some of the look and feel of the Search View.
* Now it's possible to change how many search history results the view will show.

**Note:** To get the library to work, now you have to implement a class 
named `MsvAuthority` inside the `br.com.mauker` package, and it should 
have a public static String variable called `CONTENT_AUTHORITY`. 
Give it the value you want and don't forget to add the same name on your
manifest file.

More information, on [this link](http://stackoverflow.com/a/14592121/4070469).

## Version 1.0.3

_2016-04-21_

Initial release.
