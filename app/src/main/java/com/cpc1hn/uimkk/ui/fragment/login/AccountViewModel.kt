package com.cpc1hn.uimkk.ui.fragment.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.UserClass

class AccountViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allUser: MutableLiveData<UserClass>

    init {
        allUser= MutableLiveData()
    }

    fun insertUser(user: UserClass){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        userDao.insert(user)
    }
    fun getUser():UserClass{
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        val user= userDao.getId()
        return user
    }
}