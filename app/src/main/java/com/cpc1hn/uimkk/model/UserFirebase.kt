package com.cpc1hn.uimkk.model

import androidx.room.ColumnInfo
import java.io.Serializable

class UserFirebase (
    var Email:String="",
    var FullName:String="",
    var Password:String="",
    var PhoneNumber: String="",
    var Position: String="",
    var Sex: String=""

):Serializable