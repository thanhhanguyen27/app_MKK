package com.example.uimkk.ui.viewmodel.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uimkk.dao.AppDatabase
import com.example.uimkk.model.History

class HistoryRetailViewModel (app: Application): AndroidViewModel(app)  {
    lateinit var allHistory : MutableLiveData<List<History>>
    init {
        allHistory= MutableLiveData()
    }

    fun getAllHistory(){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        var history = historyDao?.getAll()
        allHistory.postValue(history)
    }
    fun getAllHistoryObserves(): MutableLiveData<List<History>> {
        return allHistory
    }

    fun deleteHistoryInfo(){
        val historyDao = AppDatabase.getAppdatabase((getApplication()))?.historyDao()
        historyDao!!.delete()
    }

}