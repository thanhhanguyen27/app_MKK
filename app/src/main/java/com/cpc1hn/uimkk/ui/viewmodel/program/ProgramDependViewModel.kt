package com.cpc1hn.uimkk.ui.viewmodel.program

import android.app.Application
import androidx.lifecycle.*
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.SetClass

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