package com.cpc1hn.uimkk.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cpc1hn.uimkk.VERSION
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.model.SetClass
import com.cpc1hn.uimkk.model.UserClass


@Database(entities = [SetClass::class, History::class, UserClass::class, Program::class], version = VERSION)
abstract class AppDatabase : RoomDatabase() {
    abstract fun setDao(): setDao
    abstract fun historyDao(): historyDao
    abstract fun userDao():userDao
    abstract fun programDao(): programDao
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