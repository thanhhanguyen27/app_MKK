package com.example.uimkk.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.uimkk.dao.AppDatabase
import com.example.uimkk.model.User1

class ProgramViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allUser: MutableLiveData<User1>
    init {

        allUser= MutableLiveData()
    }

    fun getUsername():String {
        val userDao = AppDatabase.getAppdatabase((getApplication()))?.userDao()
        var username = userDao!!.getUsername()
        return username
    }
}