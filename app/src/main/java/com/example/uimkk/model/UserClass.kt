package com.example.uimkk.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

class UserClass:Serializable{
    var id:Int=1
    var name:String=""
    var sex: String=""
    var organization: String=""
    var email:String=""
    var phone: String=""
    constructor()

    constructor(name:String,sex:String, organization:String, email:String, phone:String){
        this.sex=sex
        this.name=name
        this.organization= organization
        this.email= email
        this.phone=phone
    }
}