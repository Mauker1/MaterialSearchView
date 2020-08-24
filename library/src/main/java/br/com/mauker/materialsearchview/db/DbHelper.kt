package br.com.mauker.materialsearchview.db

import android.content.Context

class DbHelper(context: Context): DaoProvider {
    private val db = HistoryDatabase.getInMemoryDatabase(context)

    override fun getHistoryDAO() = db.historyDAO()
}