package com.rajat.zomatotest.repository.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rajat.zomatotest.models.Repository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
public abstract class RepositoryDAO {

    @Transaction
    @Query("SELECT * from repository")
    abstract fun getObservableRepositoryList(): LiveData<List<Repository>>

    @Query("SELECT * from repository")
    abstract fun getRepositoryListOnce(): Single<List<Repository>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepositoryList(repositoryList: List<Repository>): Single<List<Long>>

    @Update
    abstract fun updateRepo(repository: Repository): Completable

    @Query("DELETE FROM repository")
    abstract fun deleteRepositoryTable(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertWithoutResponse(repositoryList: List<Repository>)

    @Query("DELETE FROM repository")
    abstract fun deleteTableWithoutResponse()

    @Transaction
    open fun deleteBeforeInsert(repositoryList: List<Repository>){
        deleteTableWithoutResponse()
        insertWithoutResponse(repositoryList)
    }


}