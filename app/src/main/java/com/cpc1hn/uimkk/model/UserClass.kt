package com.cpc1hn.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "User")
class UserClass(
    @PrimaryKey
    @ColumnInfo(name = "id") var id:Int=1,
    @ColumnInfo(name="FullName") var FullName:String="",
    @ColumnInfo(name= "Sex") var Sex: String="",
    @ColumnInfo(name= "Position")var Position: String="",
    @ColumnInfo(name="Email") var Email:String="",
    @ColumnInfo(name= "PhoneNumber")var PhoneNumber: String="",
    @ColumnInfo(name = "Password")  var Password:String=""
    ):Serializable