package com.example.uimkk.ui.viewmodel.program

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.uimkk.dao.AppDatabase
import com.example.uimkk.model.SetClass

class ProgramDependViewModel (app: Application): AndroidViewModel(app)  {
    lateinit var allSetting: MutableLiveData<SetClass>
    init {
        allSetting = MutableLiveData()
    }
    fun getAllSet(){
        val setDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
        var setting = setDao?.getId()
        allSetting.postValue(setting)
    }

    fun getSpeed():String{
        val setDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
        var setting = setDao?.getSpeed()!!
        return setting
    }

    fun getAllChecksObserves(): MutableLiveData<SetClass> {
        return allSetting
    }

}