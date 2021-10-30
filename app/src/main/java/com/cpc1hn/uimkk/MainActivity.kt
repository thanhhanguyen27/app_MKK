package com.cpc1hn.uimkk


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cpc1hn.uimkk.databinding.ActivityMainBinding
import com.cpc1hn.uimkk.helper.hideKeyboard


class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var saveData: SaveData
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.navHostHome)
        NavigationUI.setupActionBarWithNavController(this, navController)
        navController = Navigation.findNavController(this, R.id.navHostHome)
        val navView = binding.bottomNavigation
        val appBarConfiguration= AppBarConfiguration(
            topLevelDestinationIds = setOf(R.id.nav_home, R.id.nav_test, R.id.nav_history, R.id.nav_setting)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener{_, destination, _ ->
            when(destination.id){
                R.id.nav_test, R.id.nav_history, R.id.nav_setting-> {
                    showBottomBar()
                }
                R.id.nav_home->{
                    showFloatting()
                }else -> {
                    hideBottomBar()
                }
            }
        }
        navView.setupWithNavController(navController)


        saveData= SaveData(this)
        val a= saveData.loadLanguageState()

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    private fun hideBottomBar(){
        binding.bottomNavigation.visibility = View.GONE
    }

    private fun showBottomBar(){
        binding.bottomNavigation.visibility = View.VISIBLE
    }
    private fun showFloatting(){
        binding.bottomNavigation.visibility = View.VISIBLE
    }
    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        hideKeyboard()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData.setActiveScale(false)
    }


}
