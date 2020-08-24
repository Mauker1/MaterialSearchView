package br.com.mauker.materialsearchview.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.mauker.materialsearchview.db.dao.HistoryDAO
import br.com.mauker.materialsearchview.db.model.History

@Database(
        entities = [History::class],
        version = 6
)
@TypeConverters(Converters::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun historyDAO(): HistoryDAO

    companion object {
        private var INSTANCE: HistoryDatabase? = null

        fun getInMemoryDatabase(context: Context): HistoryDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context,
                HistoryDatabase::class.java, "SearchHistory.db"
        )
                .fallbackToDestructiveMigration()
                .build()
    }
}