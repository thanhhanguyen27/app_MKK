package com.cpc1hn.uimkk.ui.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.ui.User1
import com.cpc1hn.uimkk.model.UserClass

class EditProfileViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allUser: MutableLiveData<User1>
    init {
        allUser= MutableLiveData()
    }

    fun updatetUser(user: UserClass){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        userDao.insert(user)
    }

}