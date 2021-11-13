package com.cpc1hn.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Program")
class Program(
    @PrimaryKey @ColumnInfo(name = "Id")
    var id:String="",

    @ColumnInfo(name = "name")
    var NameProgram: String= "",

    @ColumnInfo(name = "concentration")
    var Concentration: Int=0,

    @ColumnInfo(name = "volumne")
    var Volume: Int=0,

    @ColumnInfo(name = "timeCreate")
    var TimeCreate: String= "",

    @ColumnInfo (name = "creator")
    var Creator: String="",

    @ColumnInfo(name = "email")
    var Email: String =""
):Serializable