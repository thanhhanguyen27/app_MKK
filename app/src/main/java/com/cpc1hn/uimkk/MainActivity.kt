package com.cpc1hn.uimkk


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cpc1hn.uimkk.databinding.ActivityMainBinding
import com.cpc1hn.uimkk.helper.hideKeyboard
import com.cpc1hn.uimkk.helper.showToast
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.ui.fragment.history.HistoryFragmentDirections
import com.cpc1hn.uimkk.ui.fragment.program.ProgramFragmentDirections
import com.cpc1hn.uimkk.ui.fragment.setting.SettingFragmentDirections
import com.cpc1hn.uimkk.ui.fragment.test.MotorACFragment
import com.cpc1hn.uimkk.ui.fragment.test.TestFragmentDirections
import com.cpc1hn.uimkk.ui.viewmodel.ProgramViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class MainActivity : AppCompatActivity(), DrawerLocker {
    private lateinit var navController: NavController
    private lateinit var saveData: SaveData
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ProgramViewModel
    private var listPrograms: ArrayList<Program> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(ProgramViewModel::class.java)
        viewModel.getAllProgram()
        viewModel.getAllProgramObserves().observe(this,{
            if (it.isNotEmpty()){
                listPrograms= ArrayList(it)
            }
        })

        navController = Navigation.findNavController(this, R.id.navHostHome)
        setupBottomNavMenu(navController)
        setupWithNavController(binding.navigation, navController)

        binding.fab.setOnClickListener {
            startQRcodeActitivity()
        }

        saveData= SaveData(this)
        val a= saveData.loadLanguageState()

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.navigation.let {
            setupWithNavController(it, navController)
            navController.addOnDestinationChangedListener { _, destination, _ ->
                supportActionBar?.title = destination.label
                when (destination.id) {
                    R.id.nav_home, R.id.nav_test, R.id.nav_history, R.id.nav_setting -> {
                        supportActionBar?.hide()
                        showBottomBar()
                        // showStatusBar()
                    }

                    else -> {
                        supportActionBar?.show()
                        hideBottomBar()
                    }
                }
            }
        }
        binding.navigation.setOnNavigationItemSelectedListener {menuItem ->
            when(menuItem.itemId){
                R.id.scanQR -> {
                    startQRcodeActitivity()
                }
            }
            false
        }
    }
    private fun hideBottomBar(){
        binding.coordinatorLayout.visibility = View.GONE
        binding.navigation.visibility = View.GONE
        binding.bottom.visibility = View.GONE
        binding.fab.visibility=View.GONE
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showBottomBar(){
        binding.navigation.visibility = View.VISIBLE
        binding.bottom.visibility = View.VISIBLE
        binding.fab.visibility= View.VISIBLE
        binding.coordinatorLayout.visibility = View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        hideKeyboard()
        return true
    }
    private fun startQRcodeActitivity() {
        IntentIntegrator(this).apply {
            setBeepEnabled(true)
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setPrompt("Quét mã QR chương trình")
            setCameraId(0)
            setOrientationLocked(true)
            initiateScan()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData.setActiveScale(false)
    }

    override fun setDrawerLocked(shouldLock: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            Log.d("_QRCODE", "ko null")
            if (result.contents != null){
                try {
                    val idProgram = result.contents.toString()
                    Log.d("_QRCODE", " result: $idProgram ")
                    super.onResume()
                    val program = listPrograms.filter { it.Id== idProgram }
                    if (program.isEmpty()){
                        Toast.makeText(this, "Chương trình không tồn tại!", Toast.LENGTH_SHORT).show()
                    }else{
                        when(navController.currentDestination?.id) {
                            R.id.nav_home -> {
                                navController.navigate(ProgramFragmentDirections.actionNavHomeToProgramDependFragment(program.first(), viewModel.getUsername()))
                            }

                            R.id.nav_test -> {
                                navController.navigate(TestFragmentDirections.actionNavTestToProgramDependFragment(program.first(), viewModel.getUsername()))

                            }

                            R.id.nav_history -> {
                                navController.navigate(HistoryFragmentDirections.actionNavHistoryToProgramDependFragment(program.first(), viewModel.getUsername()))

                            }

                            R.id.nav_setting -> {
                                navController.navigate(SettingFragmentDirections.actionNavSettingToProgramDependFragment(program.first(), viewModel.getUsername()))
                            }
                        }
                    }
                }catch (e: Exception){
                    Toast.makeText(this, "Có lỗi xảy ra: $e", Toast.LENGTH_SHORT).show()
                    Log.d("_QRCODE","Có lỗi xảy ra:  $e")
                }
            }
        } else {
            Log.d("_QRCODE", "out")
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}
