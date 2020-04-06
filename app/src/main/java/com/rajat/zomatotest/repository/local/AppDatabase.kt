package com.rajat.zomatotest.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.utils.GithubTypeConvertor

@Database(entities = arrayOf(Repository::class), version = 1)
@TypeConverters(GithubTypeConvertor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun repositoryDao():RepositoryDAO
}