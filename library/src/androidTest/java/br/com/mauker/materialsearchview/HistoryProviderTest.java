package br.com.mauker.materialsearchview;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import br.com.mauker.materialsearchview.db.HistoryContract;

/**
 * Created by mauker on 15/04/16.
 *
 * Tests the HistoryProvider.
 *
 */
public class HistoryProviderTest extends AndroidTestCase {
    private static final String TEST_QUERY_01 = "Batata";
    private static final String TEST_QUERY_02 = "Banana";
    private static long time_01;
    private static long time_02;
    private static int isHistory_01 = 0;
    private static int isHistory_02 = 1;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testDeleteAllRecords();
        time_01 = System.currentTimeMillis();
        time_02 = time_01 + 1000;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDeleteAllRecords();
    }

    public void testDeleteAllRecords() {
        // Kill them all.
        mContext.getContentResolver().delete(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null
        );

        // And make sure they are dead.
        Cursor cursor = mContext.getContentResolver().query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertNotNull(cursor);
        assertEquals(0,cursor.getCount());
        cursor.close();
    }

    public void testGetType() {
        // content-authority = br.com.mauker.searchhistorydatabase

        // Search History
        String type = mContext.getContentResolver().getType(HistoryContract.HistoryEntry.CONTENT_URI);
        // vnd.android.cursor.dir/br.com.mauker.searchhistorydatabase/history
        assertEquals(HistoryContract.HistoryEntry.CONTENT_TYPE, type);

        // Search History Date
        // Content-authority + history/date
        type = mContext.getContentResolver().getType(HistoryContract.HistoryEntry.buildHistoryUri(1));
        // vnd.android.cursor.item/br.com.mauker.searchhistorydatabase/history/0
        assertEquals(HistoryContract.HistoryEntry.CONTENT_ITEM,type);
    }

    public void testInsertAndQuery() {
        ContentValues values = getHistoryContentValues();
        Uri historyInsertUri = mContext.getContentResolver()
                .insert(HistoryContract.HistoryEntry.CONTENT_URI, values);

        long id = ContentUris.parseId(historyInsertUri);

        // Check if it was inserted.
        assertTrue(id > 0);

        // Query for all rows and validate cursor
        Cursor historyCursor = mContext.getContentResolver().query(
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertNotNull(historyCursor);

        validateCursor(historyCursor,values);

        historyCursor.close();
    }

    public void testUpdateHistory() {
        // Insert the data first. And there's no need to test this.
        // The insert test asserts that this works.
        ContentValues values = getHistoryContentValues();
        Uri historyInsertUri = mContext.getContentResolver()
                .insert(HistoryContract.HistoryEntry.CONTENT_URI, values);

        long id = ContentUris.parseId(historyInsertUri);

        assertTrue(id > 0);

        // Now, let's update it with new values.
        ContentValues newValues = getHistoryContentValues02();
        newValues.put(HistoryContract.HistoryEntry._ID, id);

        mContext.getContentResolver().update(
                HistoryContract.HistoryEntry.CONTENT_URI,
                newValues,
                HistoryContract.HistoryEntry._ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        Cursor cursor = mContext.getContentResolver().query(
                HistoryContract.HistoryEntry.buildHistoryUri(id),
                null,
                null,
                null,
                null
        );

        assertNotNull(cursor);

        validateCursor(cursor,newValues);
        cursor.close();
    }

    private ContentValues getHistoryContentValues(){
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, TEST_QUERY_01);
        values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, time_01);
        values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY,isHistory_01);
        return values;
    }

    private ContentValues getHistoryContentValues02() {
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, TEST_QUERY_02);
        values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, time_02);
        values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY,isHistory_02);

        return values;
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues){
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            switch(valueCursor.getType(idx)){
                case Cursor.FIELD_TYPE_FLOAT:
                    assertEquals(entry.getValue(), valueCursor.getDouble(idx));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    assertEquals(Long.parseLong(entry.getValue().toString()), valueCursor.getLong(idx));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
                default:
                    assertEquals(entry.getValue().toString(), valueCursor.getString(idx));
                    break;
            }
        }
        valueCursor.close();
    }
}
