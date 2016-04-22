package br.com.mauker.materialsearchview.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mauker on 15/04/16.
 * Helper class used to create and update the database.
 */
public class HistoryDbHelper extends SQLiteOpenHelper {
    // Defines the database version. Increment it if the schema has been changed.
    private static final int DB_VERSION = 4;
    // The name of the database.
    private static final String DB_NAME = "SearchHistory.db";

    /**
     * The default constructor for this helper.
     * @param context The Android application context which is using this DB.
     */
    public HistoryDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Called when this database is first created.
     * @param db The database being created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        addHistoryTable(db);
    }

    /**
     * Called whenever DATABASE_VERSION is incremented. This is used whenever schema changes need
     * to be made or new tables are added.
     *
     * @param db The database being updated.
     * @param oldVersion The previous version of the database. Used to determine whether or not
     *                   certain updates should be run.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO - Create update if needed.
        dropAllTables(db);
        onCreate(db);
    }

    /**
     * Called whenever DATABASE_VERSION is decremented.
     *
     * @param db The database being updated.
     * @param oldVersion The previous version of the database. Used to determine whether or not
     *                   certain updates should be run.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO - Create downgrade if needed.
        dropAllTables(db);
        onCreate(db);
    }

    /**
     * Inserts the Search History table into the database.
     * @param db The SQLiteDatabase the table is inserted into.
     */
    private void addHistoryTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + HistoryContract.HistoryEntry.TABLE_NAME + " (" +
                        HistoryContract.HistoryEntry._ID + " INTEGER PRIMARY KEY," +
                        HistoryContract.HistoryEntry.COLUMN_QUERY + " TEXT NOT NULL," +
                        HistoryContract.HistoryEntry.COLUMN_INSERT_DATE + " INTEGER DEFAULT 0," +
                        HistoryContract.HistoryEntry.COLUMN_IS_HISTORY + " INTEGER NOT NULL DEFAULT 0," +
                        "UNIQUE (" + HistoryContract.HistoryEntry.COLUMN_QUERY + ") ON CONFLICT REPLACE);"
        );
    }

    /**
     * Convenience method to drop all tables. Take extreme care with this.
     *
     * @param db The SQLiteDatabase from where you're dropping the tables.
     */
    private void dropAllTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + HistoryContract.HistoryEntry.TABLE_NAME);
    }
}
