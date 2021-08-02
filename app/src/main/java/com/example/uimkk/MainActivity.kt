package com.example.uimkk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.uimkk.ui.fragment.program.ProgramRetailFragment

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var saveData: SaveData



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.navHostHome)
        NavigationUI.setupActionBarWithNavController(this, navController)

        saveData= SaveData(this)
        val a= saveData.loadLanguageState()
//        switch= findViewById<View>(R.id.btSwitch) as Switch?
//        if (saveData.loadDarkModeState() == true) {
//            setTheme(R.style.DarkTheme)
//        }else
//            setTheme(R.style.AppTheme)
//
//        if (saveData.loadDarkModeState() == true){
//            switch?.isChecked= true
//        }
//        switch?.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked ){
//                saveData.setDarkModeState(true)
//                onRestartApp()
//            }else{
//                saveData.setDarkModeState(false)
//                onRestartApp()
//            }
//        }
//    }
//
//    fun onRestartApp(){
//        startActivity(Intent(applicationContext, MainActivity::class.java))
//        finish()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }



    override fun onSupportNavigateUp(): Boolean {
            navController.navigateUp()
            return super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData.setActiveScale(true)
        saveData.setRoom("")
        saveData.setTempSetting("")
    }


}
