package br.com.mauker.materialsearchview.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 *
 * Created by mauker on 15/04/16.
 *
 * The content provider for the search history.
 *
 */
public class HistoryProvider extends ContentProvider {
    private static final int SEARCH_HISTORY = 100;
    private static final int SEARCH_HISTORY_DATE = 101;
    private static final int SEARCH_HISTORY_ID = 102;
    private static final int SEARCH_HISTORY_IS_HISTORY = 103;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HistoryDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new HistoryDbHelper(getContext());
        return true;
    }

    public static UriMatcher buildUriMatcher() {
        String content = HistoryContract.CONTENT_AUTHORITY;

        // All paths to the UriMatcher have a corresponding code to return
        // when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, HistoryContract.PATH_HISTORY, SEARCH_HISTORY);
        matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_DATE);
        matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_ID);
        matcher.addURI(content, HistoryContract.PATH_HISTORY + "/#", SEARCH_HISTORY_IS_HISTORY);

        return matcher;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor rCursor;

        switch (sUriMatcher.match(uri)) {
            case SEARCH_HISTORY:
                rCursor = db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case SEARCH_HISTORY_DATE:
                long date = ContentUris.parseId(uri);
                rCursor = db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " = ?",
                        new String[]{String.valueOf(date)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case SEARCH_HISTORY_ID:
                long id = ContentUris.parseId(uri);
                rCursor = db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry._ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        sortOrder
                );
                break;

            case SEARCH_HISTORY_IS_HISTORY:
                long flag = ContentUris.parseId(uri);
                rCursor = db.query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " = ?",
                        new String[]{String.valueOf(flag)},
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        Context context = getContext();

        if (context != null) {
            rCursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return rCursor;
    }

    /**
     * Determine the MIME type of the provided URI.
     * @param uri The URI.
     * @return The MIME type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SEARCH_HISTORY:
                return HistoryContract.HistoryEntry.CONTENT_TYPE;
            case SEARCH_HISTORY_DATE:
            case SEARCH_HISTORY_ID:
            case SEARCH_HISTORY_IS_HISTORY:
                return HistoryContract.HistoryEntry.CONTENT_ITEM;
            default:
                throw new UnsupportedOperationException("Uknown Uri: " + uri);

        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri retUri;

        switch (sUriMatcher.match(uri)) {
            case SEARCH_HISTORY:
                _id = db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    retUri = HistoryContract.HistoryEntry.buildHistoryUri(_id);
                }
                else {
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return retUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {
            case SEARCH_HISTORY:
                rows = db.update(HistoryContract.HistoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rows != 0) {
            Context context = getContext();

            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rows;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        // Number of rows effected.
        int rows;

        switch (sUriMatcher.match(uri)) {
            case SEARCH_HISTORY:
                rows = db.delete(HistoryContract.HistoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (selection == null || rows != 0) {
            Context context = getContext();

            if (context != null) {
                context.getContentResolver().notifyChange(uri, null);
            }
        }

        return rows;
    }
}
