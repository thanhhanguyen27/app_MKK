package com.cpc1hn.uimkk.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.SetClass
import com.cpc1hn.uimkk.model.UserClass

class SettingViewModel(app: Application): AndroidViewModel(app)  {
    private val TAG = "_SETTING"
    lateinit var allSetting: MutableLiveData<SetClass>
    lateinit var allUser:MutableLiveData<UserClass>
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


    fun getAllChecksObservesUser(): MutableLiveData<UserClass> {
        return allUser
    }
    fun insertUser(user: UserClass){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        userDao.insert(user)
    }




}


