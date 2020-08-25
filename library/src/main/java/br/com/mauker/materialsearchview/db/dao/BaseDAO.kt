package br.com.mauker.materialsearchview.db.dao

import androidx.room.*

interface BaseDAO<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(objList: List<T>): List<Long>

    @Update
    suspend fun update(obj: T): Int

    @Delete
    suspend fun delete(obj: T): Int

}