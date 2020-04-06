package com.rajat.zomatotest.di

import android.app.Application
import androidx.room.Room
import com.rajat.zomatotest.repository.local.AppDatabase
import com.rajat.zomatotest.repository.local.RepositoryDAO
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @JvmStatic
    @Singleton
    @Provides
    fun providesRoomDb(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java, "database-name"
        ).build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun providesRepositoryDao(appDatabase: AppDatabase): RepositoryDAO {
        return appDatabase.repositoryDao()
    }

}