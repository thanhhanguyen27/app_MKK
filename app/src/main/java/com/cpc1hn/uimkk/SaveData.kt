package com.cpc1hn.uimkk

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import java.util.*

class SaveData(context:Context) {
    private var sharedPreferences:SharedPreferences= context.getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
    private var sharedPreferencesLanguage:SharedPreferences = context.getSharedPreferences(LANGUAGE, Context.MODE_PRIVATE)
    private var resources: Resources = context.resources

    fun clearAndPutLogout(){
        sharedPreferences.edit()
            .clear()
            .apply()
    }

    fun setCheckPermissionLocation(check:Int){
        val editor= sharedPreferences.edit()
        editor.putInt("Permission Location",check)
        editor.apply()
    }

    fun getCheckPermissionLocation(): Int {
        return sharedPreferences.getInt("Permission Location", 0)
    }

    fun setConnect(connect:String){
        val editor= sharedPreferences.edit()
        editor.putString(CONNECT, connect)
        editor.apply()
    }
    fun loadConnect():String{
        val connect = sharedPreferences.getString(CONNECT, "")
        return connect!!
    }

    fun setActiveScale(selected:Boolean){
        val editor= sharedPreferences.edit()
        editor.putBoolean(ACTIVE, selected)
        editor.apply()
    }
    fun loadActiveScale():Boolean{
        val selected = sharedPreferences.getBoolean(ACTIVE, true)
        return selected
    }

    fun setCodeMachine(code:String){
        val editor = sharedPreferences.edit()
        editor.putString(CODEMACHINE, code)
        editor.apply()
    }
    fun getCodeMachine():String{
        val code= sharedPreferences.getString(CODEMACHINE, " ")
        return code!!
    }

    fun setRoom(room:String){
        val editor= sharedPreferences.edit()
        editor.putString(ROOM, room)
        editor.apply()
    }
    fun loadRoom():String{
        val room = sharedPreferences.getString(ROOM, "")
        return room!!
    }

    fun setRoomSpraying(room:String){
        val editor= sharedPreferences.edit()
        editor.putString(ROOMSPRAYING, room)
        editor.apply()
    }
    fun loadRoomSpraying():String{
        val room = sharedPreferences.getString(ROOMSPRAYING, "")
        return room!!
    }

    fun setSpray(spray:Int){
        val editor= sharedPreferences.edit()
        editor.putInt(SPEED,spray)
        editor.apply()
    }

    fun loadSpray(): Int {
        return sharedPreferences.getInt(SPEED, 0)
    }
    fun setTemp(temp:String){
        val editor= sharedPreferences.edit()
        editor.putString(TEMP, temp)
        editor.apply()
    }
    fun loadTemp():String{
        val temp= sharedPreferences.getString(TEMP, "")
        return temp!!
    }

    fun setTempSetting(temp:String){
        val editor= sharedPreferences.edit()
        editor.putString(TEMP_SETTING, temp)
        editor.apply()
    }
    fun loadTempSetting():String{
        val temp= sharedPreferences.getString(TEMP_SETTING, "")
        return temp!!
    }


    fun setPer(per: Int){
        val editor= sharedPreferences.edit()
        editor.putInt(PER, per)
        editor.apply()
    }

    fun loadPer():Int{
        val temp= sharedPreferences.getInt(PER, 0)
        return temp
    }

    fun setDarkModeState(state:Boolean?){
        val editor= sharedPreferences.edit()
        editor.putBoolean("Dark", state!!)
        editor.apply()
    }

    fun loadDarkModeState():Boolean?{
        val state= sharedPreferences.getBoolean("Dark", false)
        return state
    }

    fun loadLanguageState(): String{
        var Lang = sharedPreferencesLanguage.getString(LANGUAGE, "")
        if (Lang!!.isEmpty()){
            Lang="vi"
        }
       setLocale(Lang)
        return Lang
    }

    fun setMail(mail:String){
        val editor= sharedPreferences.edit()
        editor.putString("MAIL", mail)
        editor.apply()
    }

    fun getMail():String{
        return sharedPreferences.getString("MAIL", "")!!
    }

    fun setLocale(Lang:String){
        val locale = Locale(Lang)
        Locale.setDefault(locale)
       // val resources = requireContext().resources

        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)

        val editor= sharedPreferencesLanguage.edit()
        editor.putString(LANGUAGE, Lang)
        editor.apply()
        }


}