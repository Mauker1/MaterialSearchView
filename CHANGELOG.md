Change Log
==========

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