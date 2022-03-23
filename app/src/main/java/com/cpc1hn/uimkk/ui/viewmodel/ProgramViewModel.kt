package com.cpc1hn.uimkk.ui.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.cpc1hn.uimkk.dao.AppDatabase
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.model.UserClass

class ProgramViewModel (app: Application): AndroidViewModel(app) {
    private var allUser: MutableLiveData<UserClass>
    lateinit var allProgram: MutableLiveData<List<Program>>

    init {
        allUser= MutableLiveData()
        allProgram= MutableLiveData()
    }

    fun insertProgram(program: Program){
        val programDao = AppDatabase.getAppdatabase((getApplication()))?.programDao()
        programDao?.insert(program)
        getAllProgram()
    }
    fun insertListProgram(programs: ArrayList<Program>){
        val programDao = AppDatabase.getAppdatabase((getApplication()))?.programDao()
        programDao?.insertAll(programs)
        getAllProgram()
    }

    fun getAllProgramObserves(): MutableLiveData<List<Program>>{
        return allProgram
    }

    fun getAllProgram(){
        val programDao = AppDatabase.getAppdatabase((getApplication()))?.programDao()
        var program= programDao?.getAll()
        allProgram.postValue(program)
    }

    fun delete(program: Program){
        val programDao = AppDatabase.getAppdatabase((getApplication()))?.programDao()
        programDao?.delete(program)
        getAllProgram()
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