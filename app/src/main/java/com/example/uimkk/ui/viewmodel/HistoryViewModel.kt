package com.example.uimkk.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.room.RoomMasterTable
import com.example.uimkk.Adapter.IconHistoryAdapter
import com.example.uimkk.dao.AppDatabase
import com.example.uimkk.dao.historyDao
import com.example.uimkk.model.History
import com.example.uimkk.model.SetClass

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
        getAllHistory()
    }

    fun insertAll(histories:ArrayList<History>){
        val historyDao= AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.insertAll(histories)
        getAllHistory()
    }

}