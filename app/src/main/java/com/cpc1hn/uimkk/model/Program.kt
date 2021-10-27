package com.cpc1hn.uimkk.model

import java.io.Serializable

class Program:Serializable {
    var NameProgram: String= ""
    var Concentration: Long=0
    var Volume: Long=0
    var TimeCreate: String= ""
    var Creater: String=""
    var id: String =""
    constructor()
    constructor(name: String,nongdo:Long, thetich:Long, time:String, creater:String, id:String){
        this.NameProgram= name
        this.Concentration=nongdo
        this.Volume=thetich
        this.TimeCreate= time
        this.Creater=creater
        this.id= id
    }
}