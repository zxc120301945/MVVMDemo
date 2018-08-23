package com.mvvm.project.demo.model.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import com.mvvm.project.demo.DemoApplication
import com.mvvm.project.demo.model.bean.MainResult

/**
 * Room数据库
 */
@Database(entities = arrayOf(MainResult::class), version = 1, exportSchema = false)
abstract class MainResultBase : RoomDatabase() {
    abstract fun mainResultDao(): MainResultDao

    companion object {
        @Volatile private var INSTANCE: MainResultBase? = null
        fun getInstance(): MainResultBase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase().also { INSTANCE = it }
                }

        private fun buildDatabase() =
                Room.databaseBuilder(DemoApplication.getApp().applicationContext,
                        MainResultBase::class.java, "DemoData.db")
                        .fallbackToDestructiveMigration()
                        .build()
    }
}