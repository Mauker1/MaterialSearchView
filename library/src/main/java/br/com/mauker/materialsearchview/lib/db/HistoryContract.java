package br.com.mauker.materialsearchview.lib.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mauker on 15/04/16.
 * Database contract. Contains the definition of my tables.
 */
public class HistoryContract {

    public static final String CONTENT_AUTHORITY = "br.com.mauker.materialsearchview.searchhistorydatabase";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_HISTORY = "history";

    // ----- Table definitions ----- //

    public static final class HistoryEntry implements BaseColumns {
        // Content provider stuff.
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_HISTORY;

        public static final String CONTENT_ITEM =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_HISTORY;

        // Table definition stuff.
        public static final String TABLE_NAME = "SEARCH_HISTORY";

        public static final String COLUMN_QUERY = "query";
        public static final String COLUMN_INSERT_DATE = "insert_date";
        public static final String COLUMN_IS_HISTORY = "is_history";

        public static Uri buildHistoryUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    // ----- !Table definitions ----- //

}
