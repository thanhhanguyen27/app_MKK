package com.example.uimkk.dao

import androidx.room.*
import com.example.uimkk.model.History

@Dao
interface historyDao {
    @Query("SELECT * FROM  History ORDER BY timeCreate DESC")
    fun getAll(): List<History>

    @Query("SELECT * FROM History WHERE timeEnd BETWEEN :daystart AND :dayend")
    fun getHisFilter(daystart: Long, dayend: Long): kotlinx.coroutines.flow.Flow<List<History>>

    @Query("SELECT * FROM History WHERE room LIKE :query")
    fun searchQuery(query: String): kotlinx.coroutines.flow.Flow<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(history: History)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(histories: ArrayList<History>)

    @Update
    fun update(history: History)

    @Query("DELETE FROM History")
    fun delete()

    @Delete
    fun delete(history: ArrayList<History>)
}