package com.example.uimkk.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.uimkk.dao.AppDatabase
import com.example.uimkk.model.SetClass
import com.example.uimkk.model.User1
import com.example.uimkk.model.UserClass

class SettingViewModel(app: Application): AndroidViewModel(app)  {
    private val TAG = "_SETTING"
    lateinit var allSetting: MutableLiveData<SetClass>
    lateinit var allUser:MutableLiveData<User1>
    init {
        allSetting = MutableLiveData()
        allUser= MutableLiveData()
    }
    fun getAllSet(){
        val setDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
        var setting = setDao?.getId()
        Log.d(TAG, "$setting")
        allSetting.postValue(setting)
    }

    fun getAllChecksObserves(): MutableLiveData<SetClass> {
        return allSetting
    }

    fun getSpeed():LiveData<String>{
        val setDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
        var setting = setDao?.getSpeedObserve()!!.asLiveData()
        return setting
    }


    fun update(setting: SetClass){
        val setDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
        setDao!!.insert(setting)
    }

    fun getUser(){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        val user= userDao.getId()
        allUser.postValue(user)
    }

    fun getAllChecksObservesUser(): MutableLiveData<User1> {
        return allUser
    }
    fun insertUser(user: User1){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        userDao.insert(user)
    }




}


