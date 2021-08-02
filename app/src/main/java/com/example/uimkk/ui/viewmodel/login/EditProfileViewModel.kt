package com.example.uimkk.ui.viewmodel.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.uimkk.dao.AppDatabase
import com.example.uimkk.model.User1
import com.example.uimkk.model.UserClass

class EditProfileViewModel (app: Application): AndroidViewModel(app) {
    lateinit var allUser: MutableLiveData<User1>
    init {
        allUser= MutableLiveData()
    }

    fun updatetUser(user: User1){
        val userDao= AppDatabase.getAppdatabase(getApplication())!!.userDao()
        userDao.insert(user)
    }

}