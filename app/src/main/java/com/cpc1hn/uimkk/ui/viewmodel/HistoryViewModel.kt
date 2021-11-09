package com.cpc1hn.uimkk.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.History

class HistoryViewModel(app: Application): AndroidViewModel(app)  {
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
    fun update(history: History){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.update(history)
        getAllHistory()
    }

    fun getHisstoryFilter(start:Long, end:Long):LiveData<List<History>>{
        val historyDao= AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        val history= historyDao?.getHisFilter(start, end)!!.asLiveData()
        return history
    }

    fun getHisByName(query: String):LiveData<List<History>>{
        val historyDao= AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        val history= historyDao?.searchQuery(query)!!.asLiveData()
        return history
    }

    fun deleteHistoryInfo(){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.delete()
    }

    fun insertAll(histories:ArrayList<History>){
        val historyDao= AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.insertAll(histories)
        getAllHistory()
    }

}