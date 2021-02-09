package br.com.mauker.materialsearchview

import br.com.mauker.materialsearchview.db.DaoProvider
import br.com.mauker.materialsearchview.db.dao.HistoryDAO
import br.com.mauker.materialsearchview.db.model.History
import br.com.mauker.materialsearchview.db.model.QueryType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.withContext
import java.util.*

class MsvDataAccessor(daoProvider: DaoProvider) {

    private val historyDAO: HistoryDAO = daoProvider.getHistoryDAO()

    suspend fun getDefaultList(maxHistory: Int, maxPinned: Int) =
            historyDAO.getDefaultHistoryWithPin(maxHistory, maxPinned).toMutableList()

    suspend fun getFilteredList(query: String, maxHistory: Int, maxPinned: Int): MutableList<History> {
        val filtered = if (query.isBlank()) {
            historyDAO.getDefaultHistoryWithPin(maxHistory, maxPinned)
        } else {
            historyDAO.getFilteredHistory(query)
        }

        return filtered.toMutableList()
    }

    suspend fun saveQuery(query: String) {
        val history = History(
                query = query,
                insertDate = Calendar.getInstance(),
                queryType = QueryType.HISTORY
        )
        println("Inserting query...")
        historyDAO.insert(history)
        println("Done inserting query.")
    }

    suspend fun addPin(pin: String) {
        val history = History(
                query = pin,
                insertDate = Calendar.getInstance(),
                queryType = QueryType.PINNED
        )
        println("Inserting pin...")
        historyDAO.insert(history)
        println("Done inserting pin.")
    }

    suspend fun addSuggestion(suggestion: String) {
        val history = History(
                query = suggestion,
                insertDate = Calendar.getInstance(),
                queryType = QueryType.SUGGESTION
        )
        historyDAO.insert(history)
    }

    private suspend fun addItems(items: List<String>, queryType: QueryType) {
        val insertDate = Calendar.getInstance()
        val filteredList = items.filterNot { it.isBlank() }
        val suggestionList = filteredList.map { History(query = it, insertDate = insertDate, queryType = queryType) }
        println("Inserting items...")
        historyDAO.insert(suggestionList)
        println("Done inserting items.")
    }

    suspend fun addSuggestions(suggestions: List<String>) {
        addItems(suggestions, QueryType.SUGGESTION)
    }

    suspend fun addPinnedItems(pinnedItems: List<String>) {
        addItems(pinnedItems, QueryType.PINNED)
    }

    suspend fun removeHistoryItem(item: String) {
        historyDAO.deleteItemByText(item)
    }

    suspend fun clearSuggestions() {
        historyDAO.clearSuggestions()
    }

    suspend fun clearSearchHistory() {
        historyDAO.clearHistory()
    }

    suspend fun clearPinned() {
        historyDAO.clearPinned()
    }

    suspend fun clearAll() {
        historyDAO.deleteAll()
    }
}