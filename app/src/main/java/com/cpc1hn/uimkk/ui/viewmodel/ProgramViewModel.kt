package com.cpc1hn.uimkk.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.UserClass

class ProgramViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allUser: MutableLiveData<UserClass>
    init {

        allUser= MutableLiveData()
    }

    fun getUsername():String {
        val userDao = AppDatabase.getAppdatabase((getApplication()))?.userDao()
        var username = userDao!!.getUsername()
        return username
    }
    fun getUser():UserClass{
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        val user= userDao.getId()
        return user
    }
    fun insertUser(user: UserClass){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        userDao.insert(user)
    }
}