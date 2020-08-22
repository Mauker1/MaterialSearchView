package br.com.mauker.materialsearchview.db

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import br.com.mauker.materialsearchview.db.HistoryContract.HistoryEntry.buildHistoryUri

/**
 *
 * Created by mauker on 15/04/16.
 *
 * The content provider for the search history.
 *
 */
class HistoryProvider : ContentProvider() {

    companion object {
        private const val SEARCH_HISTORY = 100
        private const val SEARCH_HISTORY_DATE = 101
        private const val SEARCH_HISTORY_ID = 102
        private const val SEARCH_HISTORY_IS_HISTORY = 103
        private val sUriMatcher = buildUriMatcher()

        private fun buildUriMatcher(): UriMatcher {
            val content = HistoryContract.CONTENT_AUTHORITY

            // All paths to the UriMatcher have a corresponding code to return
            // when a match is found (the ints above).
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            matcher.addURI(content, HistoryContract.PATH_HISTORY, SEARCH_HISTORY)
            matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_DATE)
            matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_ID)
            matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_IS_HISTORY)
            return matcher
        }
    }

    private lateinit var mOpenHelper: HistoryDbHelper

    override fun onCreate(): Boolean {
        mOpenHelper = HistoryDbHelper(context)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val db = mOpenHelper.readableDatabase
        val rCursor: Cursor
        rCursor = when (sUriMatcher.match(uri)) {
            SEARCH_HISTORY -> db.query(
                    HistoryContract.HistoryEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            )
            SEARCH_HISTORY_DATE -> {
                val date = ContentUris.parseId(uri)
                db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " = ?", arrayOf(date.toString()),
                        null,
                        null,
                        sortOrder
                )
            }
            SEARCH_HISTORY_ID -> {
                val id = ContentUris.parseId(uri)
                db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry._ID + " = ?", arrayOf(id.toString()),
                        null,
                        null,
                        sortOrder
                )
            }
            SEARCH_HISTORY_IS_HISTORY -> {
                val flag = ContentUris.parseId(uri)
                db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?", arrayOf(flag.toString()),
                        null,
                        null,
                        sortOrder
                )
            }
            else -> throw UnsupportedOperationException("Unknown Uri: $uri")
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        val context = context
        if (context != null) {
            rCursor.setNotificationUri(context.contentResolver, uri)
        }
        return rCursor
    }

    /**
     * Determine the MIME type of the provided URI.
     * @param uri The URI.
     * @return The MIME type.
     */
    override fun getType(uri: Uri): String? {
        return when (sUriMatcher.match(uri)) {
            SEARCH_HISTORY -> HistoryContract.HistoryEntry.CONTENT_TYPE
            SEARCH_HISTORY_DATE, SEARCH_HISTORY_ID, SEARCH_HISTORY_IS_HISTORY -> HistoryContract.HistoryEntry.CONTENT_ITEM
            else -> throw UnsupportedOperationException("Uknown Uri: $uri")
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val db = mOpenHelper.writableDatabase
        val _id: Long
        val retUri: Uri
        when (sUriMatcher.match(uri)) {
            SEARCH_HISTORY -> {
                _id = db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values)
                retUri = if (_id > 0) {
                    buildHistoryUri(_id)
                } else {
                    throw UnsupportedOperationException("Unable to insert rows into: $uri")
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        return retUri
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper.writableDatabase
        val rows: Int
        rows = when (sUriMatcher.match(uri)) {
            SEARCH_HISTORY -> db.update(HistoryContract.HistoryEntry.TABLE_NAME, values, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (rows != 0) {
            val context = context
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rows
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val db = mOpenHelper.writableDatabase
        // Number of rows effected.
        val rows: Int
        rows = when (sUriMatcher.match(uri)) {
            SEARCH_HISTORY -> db.delete(HistoryContract.HistoryEntry.TABLE_NAME, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (selection == null || rows != 0) {
            val context = context
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rows
    }
}