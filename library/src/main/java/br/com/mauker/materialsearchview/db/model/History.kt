package br.com.mauker.materialsearchview.db.model;

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(
        tableName = "SEARCH_HISTORY",
        indices = [Index(value = ["query"], unique = true)]
)
data class History(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "history_id")
        val historyId: Long = 0L,
        @ColumnInfo(name = "query") val query: String,
        @ColumnInfo(name = "insert_date") val insertDate: Calendar,
        @ColumnInfo(name = "query_type") val queryType: QueryType,
)
