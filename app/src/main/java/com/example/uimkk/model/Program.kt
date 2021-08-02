package com.example.uimkk.model

import java.io.Serializable

class Program:Serializable {
    var name: String= ""
    var nongdo: String=""
    var thetich: String=""
    var timeCreate: String= ""
    var creater: String=""
    var id: String =""
    constructor()
    constructor(name: String,nongdo:String, thetich:String, time:String, creater:String, id:String){
        this.name= name
        this.nongdo=nongdo
        this.thetich=thetich
        this.timeCreate= time
        this.creater=creater
        this.id= id
    }
}