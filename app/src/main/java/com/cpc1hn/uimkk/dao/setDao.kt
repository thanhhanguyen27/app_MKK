package com.cpc1hn.uimkk.dao

import androidx.room.*
import com.cpc1hn.uimkk.model.SetClass
import kotlinx.coroutines.flow.Flow

@Dao
interface setDao {
    @Query("SELECT * FROM Setting WHERE id == 1")
    fun getId(): SetClass

    @Query("SELECT speedDomestor from Setting WHERE id==1")
    fun getSpeed():String

    @Query("SELECT speedDomestor from Setting WHERE id==1")
    fun getSpeedObserve():Flow<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(check: SetClass)

    @Update
    fun update(check: SetClass)
}
