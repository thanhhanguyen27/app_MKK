package com.example.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
class User1 (
    @PrimaryKey
    @ColumnInfo(name = "id") var id:Int=1,
    @ColumnInfo(name="name") var name:String="",
    @ColumnInfo(name="email") var email:String=""
)