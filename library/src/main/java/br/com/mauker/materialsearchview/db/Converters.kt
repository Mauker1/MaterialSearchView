package br.com.mauker.materialsearchview.db

import androidx.room.TypeConverter
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
}