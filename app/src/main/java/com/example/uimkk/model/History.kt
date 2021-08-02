package com.example.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "History")
class History:Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: Int = 0
    @ColumnInfo(name = "numberOfRun")
    var numberOfRun: String = ""
    @ColumnInfo(name = "nongdo")
    var nongdo: String = ""
    @ColumnInfo(name = "thetich")
    var thetich: String = ""
    @ColumnInfo(name = "timeCreate")
    var timeCreate: String = ""
    @ColumnInfo(name = "hourStart")
    var hourStart:String=""
    @ColumnInfo(name = "timeEnd")
    var timeEnd: Long=0
    @ColumnInfo(name = "hourEnd")
    var hourEnd:String=""
    @ColumnInfo(name = "creater")
    var creater: String = ""
    @ColumnInfo(name = "location")
    var location: String = ""
    @ColumnInfo(name = "room")
    var room:String =""
    @ColumnInfo(name = "timeRun")
    var timeRun: String = ""
    @ColumnInfo(name = "error")
    var error: String = ""
    @ColumnInfo(name = "speedSpray")
    var speedSpray: String = ""
    @ColumnInfo(name = "status")
    var status: String=""

    fun isNoCode():Boolean{
        return status.isNotEmpty()
    }

    constructor()
    constructor(id:Int, number: String, nongdo:String, thetich:String, timeCreate: String, hourStart: String, timeEnd:Long, hourEnd:String, creater:String, location:String, room:String, timeRun:String, error:String, speedSpray: String, status:String ){
        this.id= id
        this.numberOfRun= number
        this.nongdo= nongdo
        this.thetich=thetich
        this.timeCreate=timeCreate
        this.timeEnd=timeEnd
        this.hourStart=hourStart
        this.creater= creater
        this.hourEnd=hourEnd
        this.location=location
        this.room=room
        this.timeRun=timeRun
        this.error=error
        this.speedSpray=speedSpray
        this.status= status

    }
}