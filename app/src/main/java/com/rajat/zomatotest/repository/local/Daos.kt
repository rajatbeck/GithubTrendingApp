package com.rajat.zomatotest.repository.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rajat.zomatotest.models.Repository

abstract class BaseDao<T> {

    @Insert
    abstract fun insert(obj: T): Long

    @Insert
    abstract fun insert(vararg obj: T): Array<Long>

    @Insert
    abstract fun insertAll(objs: List<@JvmSuppressWildcards T>): List<Long>

    @Update
    abstract fun update(obj: T): Int

    @Transaction
    open fun upsert(obj: T) {
        val id = insert(obj)
        if (id == -1L) update(obj)
    }

    @Delete
    abstract fun delete(vararg obj: T): Int

    @Delete
    abstract fun deleteAll(objs: List<@JvmSuppressWildcards T>): Int

}

@Dao
interface  RepositoryDAO {

    @Query("SELECT * from repository")
    fun getSavedRepo(): LiveData<List<Repository>>
}