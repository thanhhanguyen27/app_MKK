package com.cpc1hn.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "History")
class History(
    @PrimaryKey
    @ColumnInfo(name = "TimeCreate")
    var TimeCreate: String = "",
    @ColumnInfo(name = "CodeMachine")
    var CodeMachine:String="",
    @ColumnInfo(name = "Concentration")
    var Concentration: Int= 0,
    @ColumnInfo(name = "Volume")
    var Volume: Int = 0,
    @ColumnInfo(name = "hourStart")
    var hourStart:String="",
    @ColumnInfo(name = "TimeEnd")
    var TimeEnd: String="",
    @ColumnInfo(name = "timeEndLong")
    var timeEndLong: Long=0,
    @ColumnInfo(name = "hourEnd")
    var hourEnd:String="",
    @ColumnInfo(name = "Creator")
    var Creator: String = "",
    @ColumnInfo(name = "Room")
    var Room:String ="",
    @ColumnInfo(name = "TimeRun")
    var TimeRun: String = "",
    @ColumnInfo(name = "Error")
    var Error: Int = 0,
    @ColumnInfo(name = "SpeedSpray")
    var SpeedSpray: Int = 0,
    @ColumnInfo(name = "Status")
    var Status: Int=0,
    @ColumnInfo(name="TimeProgram")
    var TimeProgram: String=""
): Serializable {
    fun isNoCode(): Boolean {
        return Status==0
    }
    constructor(timeCreate:String,CodeMachine:String,Concentration:Int,Volume: Int,TimeEnd:String, Creator: String,
                Room:String, TimeRun: String,Error: Int, SpeedSpray: Int,Status: Int ) : this() {
        this.TimeCreate=timeCreate
        this.CodeMachine= CodeMachine
        this.Concentration=Concentration
        this.Volume=Volume
        this.TimeEnd=TimeEnd
        this.Creator=Creator
        this.Room=Room
        this.TimeRun= TimeRun
        this.Error=Error
        this.SpeedSpray=SpeedSpray
        this.Status=Status
    }
    fun checkError():String{
        return when(Error){
            0-> "Không có lỗi"
            1 -> "Dừng đột ngột"
            else -> "Dừng do quá nhiệt"
        }
    }
}


