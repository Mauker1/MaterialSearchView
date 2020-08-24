package br.com.mauker.materialsearchview.db.dao

import androidx.room.Query
import br.com.mauker.materialsearchview.db.model.History

interface HistoryDAO: BaseDAO<History> {

    @Query("SELECT * FROM SEARCH_HISTORY")
    suspend fun getAll(): List<History>

    @Query("SELECT * FROM SEARCH_HISTORY WHERE is_history=1 ORDER BY insert_date DESC LIMIT :maxHistory")
    suspend fun getDefaultHistory(maxHistory: Int): List<History>

    @Query("SELECT * FROM SEARCH_HISTORY WHERE `query` LIKE '%' || :filter || '%' ORDER BY is_history DESC, `query`")
    suspend fun getFilteredHistory(filter: String): List<History>

    @Query("DELETE FROM SEARCH_HISTORY WHERE is_history=0")
    suspend fun clearSuggestions(): Int

    @Query("DELETE FROM SEARCH_HISTORY WHERE is_history=1")
    suspend fun clearHistory(): Int

    @Query("DELETE FROM SEARCH_HISTORY WHERE is_pinned=1")
    suspend fun clearPinned(): Int

    @Query("DELETE FROM SEARCH_HISTORY")
    suspend fun deleteAll()
}