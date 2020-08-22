package br.com.mauker.materialsearchview

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

/**
 * Created by mauker on 15/04/16.
 *
 * Tests the HistoryProvider.
 *
 */
@RunWith(AndroidJUnit4::class)
class HistoryProviderTest {
//
//    companion object {
//        private const val TEST_QUERY_01 = "Batata"
//        private const val TEST_QUERY_02 = "Banana"
//        private var time_01: Long = 0
//        private var time_02: Long = 0
//        private const val isHistory_01 = 0
//        private const val isHistory_02 = 1
//    }
//
//    private val mContext = InstrumentationRegistry.getInstrumentation().targetContext
//
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//        testDeleteAllRecords()
//        time_01 = System.currentTimeMillis()
//        time_02 = time_01 + 1000
//    }
//
//    @After
//    @Throws(Exception::class)
//    fun tearDown() {
//        testDeleteAllRecords()
//    }
//
//    @Test
//    fun testDeleteAllRecords() {
//        // Kill them all.
//        mContext.contentResolver.delete(
//                HistoryContract.HistoryEntry.CONTENT_URI,
//                null,
//                null
//        )
//
//        // And make sure they are dead.
//        val cursor = mContext.contentResolver.query(
//                HistoryContract.HistoryEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        )
//        Assert.assertNotNull(cursor)
//        Assert.assertEquals(0, cursor!!.count)
//        cursor.close()
//    }
//
//    @Test
//    fun testGetType() {
//        // content-authority = br.com.mauker.searchhistorydatabase
//
//        // Search History
//        var type = mContext.contentResolver.getType(HistoryContract.HistoryEntry.CONTENT_URI)
//        // vnd.android.cursor.dir/br.com.mauker.searchhistorydatabase/history
//        Assert.assertEquals(HistoryContract.HistoryEntry.CONTENT_TYPE, type)
//
//        // Search History Date
//        // Content-authority + history/date
//        type = mContext.contentResolver.getType(buildHistoryUri(1))
//        // vnd.android.cursor.item/br.com.mauker.searchhistorydatabase/history/0
//        Assert.assertEquals(HistoryContract.HistoryEntry.CONTENT_ITEM, type)
//    }
//
//    @Test
//    fun testInsertAndQuery() {
//        val values = historyContentValues
//        val historyInsertUri = mContext.contentResolver
//                .insert(HistoryContract.HistoryEntry.CONTENT_URI, values)
//        val id = ContentUris.parseId(historyInsertUri!!)
//
//        // Check if it was inserted.
//        Assert.assertTrue(id > 0)
//
//        // Query for all rows and validate cursor
//        val historyCursor = mContext.contentResolver.query(
//                HistoryContract.HistoryEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        )
//        Assert.assertNotNull(historyCursor)
//        validateCursor(historyCursor, values)
//        historyCursor!!.close()
//    }
//
//    @Test
//    fun testUpdateHistory() {
//        // Insert the data first. And there's no need to test this.
//        // The insert test asserts that this works.
//        val values = historyContentValues
//        val historyInsertUri = mContext.contentResolver
//                .insert(HistoryContract.HistoryEntry.CONTENT_URI, values)
//        val id = ContentUris.parseId(historyInsertUri!!)
//        Assert.assertTrue(id > 0)
//
//        // Now, let's update it with new values.
//        val newValues = historyContentValues02
//        newValues.put(HistoryContract.HistoryEntry._ID, id)
//        mContext.contentResolver.update(
//                HistoryContract.HistoryEntry.CONTENT_URI,
//                newValues,
//                HistoryContract.HistoryEntry._ID + " = ?", arrayOf(id.toString()))
//        val cursor = mContext.contentResolver.query(
//                buildHistoryUri(id),
//                null,
//                null,
//                null,
//                null
//        )
//        Assert.assertNotNull(cursor)
//        validateCursor(cursor, newValues)
//        cursor!!.close()
//    }
//
//    private val historyContentValues: ContentValues
//        get() {
//            val values = ContentValues()
//            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, TEST_QUERY_01)
//            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, time_01)
//            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, isHistory_01)
//            return values
//        }
//    private val historyContentValues02: ContentValues
//        get() {
//            val values = ContentValues()
//            values.put(HistoryContract.HistoryEntry.COLUMN_QUERY, TEST_QUERY_02)
//            values.put(HistoryContract.HistoryEntry.COLUMN_INSERT_DATE, time_02)
//            values.put(HistoryContract.HistoryEntry.COLUMN_IS_HISTORY, isHistory_02)
//            return values
//        }
//
//    private fun validateCursor(valueCursor: Cursor?, expectedValues: ContentValues) {
//        Assert.assertTrue(valueCursor!!.moveToFirst())
//        val valueSet = expectedValues.valueSet()
//        for ((columnName, value) in valueSet) {
//            val idx = valueCursor.getColumnIndex(columnName)
//            Assert.assertFalse(idx == -1)
//            when (valueCursor.getType(idx)) {
//                Cursor.FIELD_TYPE_FLOAT -> Assert.assertEquals(value, valueCursor.getDouble(idx))
//                Cursor.FIELD_TYPE_INTEGER -> Assert.assertEquals(value.toString().toLong(), valueCursor.getLong(idx))
//                Cursor.FIELD_TYPE_STRING -> Assert.assertEquals(value, valueCursor.getString(idx))
//                else -> Assert.assertEquals(value.toString(), valueCursor.getString(idx))
//            }
//        }
//        valueCursor.close()
//    }
}
