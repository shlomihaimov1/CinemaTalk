package com.app.cinematalk.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.cinematalk.base.MyApplication
import com.app.cinematalk.model.Review

@Database(entities = [Review::class], version = 3)
abstract class AppLocalDbRepository : RoomDatabase() {
    /**
     * Provides access to the ReviewDao.
     */
    abstract fun reviewDao(): ReviewDao
}

object AppLocalDatabase {

    /**
     * Lazy initialization of the database instance.
     */
    val db: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context,
            AppLocalDbRepository::class.java,
            "dbFileName.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}