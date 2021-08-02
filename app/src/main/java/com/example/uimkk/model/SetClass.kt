package com.example.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Setting")
class SetClass (
    @PrimaryKey @ColumnInfo(name = "id") var id:Int=1,
    @ColumnInfo(name = "led") var led: Boolean = false,
    @ColumnInfo(name = "scale") var scale:Boolean = false,
    @ColumnInfo(name = "buzzer") var buzzer:Boolean = false,
    @ColumnInfo(name = "fan") var fan:Boolean= false,
    @ColumnInfo(name = "zeroscale") var zeroscale:Boolean= false,
    @ColumnInfo(name = "maxscale") var maxscale:Boolean= false,
    @ColumnInfo(name = "temp") var  temp:String="",
    @ColumnInfo(name = "speedDomestor") var speedDomestor:String="",
    )
