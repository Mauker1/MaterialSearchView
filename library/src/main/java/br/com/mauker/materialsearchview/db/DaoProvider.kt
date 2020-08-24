package br.com.mauker.materialsearchview.db

import br.com.mauker.materialsearchview.db.dao.HistoryDAO

interface DaoProvider {
    fun getHistoryDAO(): HistoryDAO
}