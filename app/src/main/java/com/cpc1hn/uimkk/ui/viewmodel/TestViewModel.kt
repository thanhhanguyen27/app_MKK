package com.cpc1hn.uimkk.ui.viewmodel

import androidx.lifecycle.ViewModel

class TestViewModel(): ViewModel() {}
//    lateinit var allcheck: MutableLiveData<SetClass>
//    init {
//        allcheck = MutableLiveData()
//    }
//    fun getTestFragment(){
//        val testDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
//        var listTest = testDao?.getAll()
//        allcheck.postValue(listTest)
//    }
//
//    fun getAllChecksObserves(): MutableLiveData<SetClass>{
//        return allcheck
//    }
//
//    fun insertCheck(entity: SetClass){
//        val checkDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
//        checkDao?.insert(entity)
//    }
//
//    fun update(check: SetClass){
//        val checkDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
//        checkDao?.update(check)
//    }
//}

//package com.example.uimkk.dao
//
//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.uimkk.model.SetClass
//
//@Database(entities = [SetClass::class], version = 1)
//abstract class AppDatabase : RoomDatabase() {
//    abstract fun setDao(): setDao
//    companion object{
//        private var INSTANCE: AppDatabase? = null
//
//        fun getAppdatabase(context: Context): AppDatabase?{
//            if (INSTANCE == null){
//                INSTANCE= Room.databaseBuilder<AppDatabase>(context, AppDatabase::class.java ,"APPDB")
//                    .allowMainThreadQueries()
//                    .build()
//            }
//            return INSTANCE
//        }
//    }
//}


//package com.example.uimkk.dao
//
//import androidx.room.*
//import com.example.uimkk.model.SetClass
//
//@Dao
//interface setDao {
//    @Query("SELECT * FROM `Set`")
//    fun getAll(): SetClass
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insert(vararg check: SetClass)
//
//    @Update
//    fun update(check: SetClass)
//}


//import androidx.room.ColumnInfo
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//
//
//@Entity(tableName = "Set")
//class SetClass (
//    @PrimaryKey @ColumnInfo(name = "led") val led: Boolean,
//    @ColumnInfo(name = "scale") val scale:Boolean = true,
//    @ColumnInfo(name = "buzzer") val buzzer:Boolean =true,
//    @ColumnInfo(name = "fan") val fan:Boolean= true,
//    @ColumnInfo(name = "zeroscale") val zeroscale:Boolean= true,
//    @ColumnInfo(name = "maxscale") val maxscale:Boolean= true,
//   // @ColumnInfo(name = "temp") val  temp:String= "",
//   // @ColumnInfo(name = "speedDomestor") val speedDomestor:String= "",
//  //  @ColumnInfo(name = "speed") val speed: String= "",
//    @ColumnInfo(name = "language") val language:Boolean= true,
//
//    )