package br.com.mauker.materialsearchview.db

import androidx.room.TypeConverter
import br.com.mauker.materialsearchview.db.model.QueryType
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            val cal = Calendar.getInstance()
            cal.timeInMillis = it
            cal
        }
    }

    @TypeConverter
    fun calendarToTimestamp(cal: Calendar?): Long? {
        return cal?.timeInMillis
    }

    @TypeConverter
    fun queryTypeToInt(type: QueryType): Int {
        return type.value
    }

    @TypeConverter
    fun intToQueryType(value: Int): QueryType {
        return when(value) {
            0 -> QueryType.PINNED
            1 -> QueryType.HISTORY
            else -> QueryType.SUGGESTION
        }
    }
}