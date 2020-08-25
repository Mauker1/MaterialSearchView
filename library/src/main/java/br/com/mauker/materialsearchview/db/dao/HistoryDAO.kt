package br.com.mauker.materialsearchview.db.dao

import androidx.room.Dao
import androidx.room.Query
import br.com.mauker.materialsearchview.db.model.History
import br.com.mauker.materialsearchview.db.model.QueryType

@Dao
interface HistoryDAO: BaseDAO<History> {

    @Query("SELECT * FROM SEARCH_HISTORY")
    suspend fun getAll(): List<History>

    // TODO - Pinned items (OR query_type=1 ORDER BY query_type ASC,) -
    //  Solve LIMIT problem with transaction. Query the number of pinned items, or change strategies
    //  e.g.: Instead of calling it maxHistory, call it maxResults.
    @Query("SELECT * FROM SEARCH_HISTORY WHERE query_type=1  ORDER BY insert_date DESC LIMIT :maxHistory")
    suspend fun getDefaultHistory(maxHistory: Int): List<History>

    @Query("SELECT * FROM SEARCH_HISTORY WHERE `query` LIKE '%' || :filter || '%' ORDER BY query_type ASC, `query`")
    suspend fun getFilteredHistory(filter: String): List<History>

    @Query("DELETE FROM SEARCH_HISTORY WHERE query_type=0")
    suspend fun clearPinned(): Int

    @Query("DELETE FROM SEARCH_HISTORY WHERE query_type=1")
    suspend fun clearHistory(): Int

    @Query("DELETE FROM SEARCH_HISTORY WHERE query_type=2")
    suspend fun clearSuggestions(): Int

    @Query("DELETE FROM SEARCH_HISTORY WHERE `query`=:query")
    suspend fun deleteItemByText(query: String): Int

    @Query("DELETE FROM SEARCH_HISTORY")
    suspend fun deleteAll()
}