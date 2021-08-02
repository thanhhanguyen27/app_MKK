package com.example.uimkk.dao

import androidx.room.*
import com.example.uimkk.model.User1
import com.example.uimkk.model.UserClass

@Dao
interface userDao {
    @Query("SELECT * FROM User WHERE id == 1")
    fun getId(): User1

    @Update
    fun update(user:User1)

    @Query("SELECT name from User WHERE id==1")
    fun getUsername():String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User1)
}