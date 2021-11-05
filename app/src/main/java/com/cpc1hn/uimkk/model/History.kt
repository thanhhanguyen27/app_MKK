package com.cpc1hn.uimkk.model

import android.os.health.TimerStat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "History")
class History(
    @PrimaryKey
    @ColumnInfo(name = "TimeStart")
    var TimeStart: String = "",
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
    var TimeRun: Int= 0,
    @ColumnInfo(name = "Error")
    var Error: Int = 0,
    @ColumnInfo(name = "SpeedSpray")
    var SpeedSpray: Int = 0,
    @ColumnInfo(name = "Status")
    var Status: Int=0,
    @ColumnInfo(name="TimeProgram")
    var TimeCreateProgram: String=""
): Serializable {
    fun isNoCode(): Boolean {
        return Status==0
    }
    constructor(timerStart:String, CodeMachine:String,Concentration:Int,Volume: Int,TimeEnd:String, Creator: String,
                Room:String, TimeRun: Int ,Error: Int, SpeedSpray: Int,timeCreateProgram: String, Status: Int ) : this() {
        this.TimeStart =timerStart
        this.CodeMachine = CodeMachine
        this.Concentration = Concentration
        this.Volume = Volume
        this.TimeEnd = TimeEnd
        this.Creator=Creator
        this.Room=Room
        this.TimeRun= TimeRun
        this.Error=Error
        this.SpeedSpray=SpeedSpray
        this.TimeCreateProgram= timeCreateProgram
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


