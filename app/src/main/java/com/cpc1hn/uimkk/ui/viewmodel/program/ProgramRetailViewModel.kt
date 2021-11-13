package com.cpc1hn.uimkk.ui.viewmodel.program

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.History

class ProgramRetailViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allHistory : MutableLiveData<List<History>>
    init {
        allHistory= MutableLiveData()
    }

    fun getUsername():String {
        val userDao = AppDatabase.getAppdatabase((getApplication()))?.userDao()
        var username = userDao!!.getUsername()
        return username
    }

    fun getAllHistory(){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        var history = historyDao?.getAll()
        allHistory.postValue(history)
    }
    fun insert(history: History){
        val historyDao= AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.insert(history)
        getAllHistory()
    }
    fun getSpeed():String{
        val setDao = AppDatabase.getAppdatabase((getApplication()))?.setDao()
        var setting = setDao?.getSpeed()!!
        return setting
    }

}