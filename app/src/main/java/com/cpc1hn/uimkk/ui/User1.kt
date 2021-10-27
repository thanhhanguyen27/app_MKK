package com.cpc1hn.uimkk.ui

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

class User1 (
    @PrimaryKey
    @ColumnInfo(name = "id") var id:Int=1,
    @ColumnInfo(name="name") var name:String="",
    @ColumnInfo(name="email") var email:String=""
)