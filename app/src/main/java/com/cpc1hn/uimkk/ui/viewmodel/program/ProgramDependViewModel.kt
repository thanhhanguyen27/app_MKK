package com.cpc1hn.uimkk.ui.viewmodel.program

import android.app.Application
import androidx.lifecycle.*
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.model.SetClass

class ProgramDependViewModel (app: Application): AndroidViewModel(app)  {
    lateinit var allHistory : MutableLiveData<List<History>>
    init {
        allHistory= MutableLiveData()
    }

    fun getAllHistory(){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        var history = historyDao?.getAll()
        allHistory.postValue(history)
    }
    fun getAllHistoryObserves(): MutableLiveData<List<History>>{
        return allHistory
    }
    fun updateProgram(program: Program){
        val programDao = AppDatabase.getAppdatabase((getApplication()))?.programDao()
        programDao?.update(program)
    }
    fun update(history: History){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.update(history)
        getAllHistory()
    }
    fun insert(history: History){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.insert(history)
    }

}