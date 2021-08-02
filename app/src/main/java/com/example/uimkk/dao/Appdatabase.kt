package com.example.uimkk.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.uimkk.VERSION
import com.example.uimkk.model.History
import com.example.uimkk.model.SetClass
import com.example.uimkk.model.User1


@Database(entities = [SetClass::class, History::class, User1::class], version = VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun setDao(): setDao
    abstract fun historyDao(): historyDao
    abstract fun userDao():userDao
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getAppdatabase(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder<AppDatabase>(context, AppDatabase::class.java, "APPDB")
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE
        }
    }
}