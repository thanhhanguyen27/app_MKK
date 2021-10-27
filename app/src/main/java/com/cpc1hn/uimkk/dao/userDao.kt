package com.cpc1hn.uimkk.dao
import androidx.room.*
import com.cpc1hn.uimkk.model.UserClass

@Dao
interface userDao {
    @Query("SELECT * FROM User WHERE id == 1")
    fun getId(): UserClass

    @Update
    fun update(user:UserClass)

    @Query("SELECT name from User WHERE id==1")
    fun getUsername():String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserClass)
}