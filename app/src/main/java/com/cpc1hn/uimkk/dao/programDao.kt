package com.cpc1hn.uimkk.dao

import androidx.room.*
import com.cpc1hn.uimkk.model.Program

@Dao
interface programDao {
    @Query("SELECT * FROM  Program ORDER BY TimeCreate DESC")
    fun getAll(): List<Program>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(program: Program)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(programs: ArrayList<Program>)

    @Update
    fun update(program: Program)

    @Update
    fun updateAllProgram (programs: ArrayList<Program>)

    @Query("DELETE FROM Program")
    fun delete()

    @Delete
    fun delete(program: Program)
}