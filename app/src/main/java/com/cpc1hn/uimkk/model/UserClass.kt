package com.cpc1hn.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "User")
class UserClass(
    @PrimaryKey
    @ColumnInfo(name = "id") var id:Int=1,
    @ColumnInfo(name="name") var name:String="",
    @ColumnInfo(name= "sex") var sex: String="",
    @ColumnInfo(name= "organization")var organization: String="",
    @ColumnInfo(name="email") var email:String="",
    @ColumnInfo(name= "phone")var phone: String="",
    ):Serializable