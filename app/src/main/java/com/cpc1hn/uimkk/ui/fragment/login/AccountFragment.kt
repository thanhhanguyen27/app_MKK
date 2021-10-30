package com.cpc1hn.uimkk.ui.fragment.login

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cpc1hn.uimkk.LoginActivity
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.AccountFragmentBinding
import com.cpc1hn.uimkk.model.UserClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var viewModel: AccountViewModel
    private lateinit var binding: AccountFragmentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPrefsHelper: SaveData
    private  var user: UserClass= UserClass()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        binding= DataBindingUtil.inflate(inflater, R.layout.account_fragment, container, false)
        //user= AccountFragmentArgs.fromBundle(requireArguments()).user
        sharedPrefsHelper= SaveData(requireContext())
        auth = FirebaseAuth.getInstance()
        user = viewModel.getUser()
        getAccount()

        val saveData=SaveData(requireContext())
        activity.run {
            when(saveData.getCheckPermissionLocation()){
                1 -> getWifiSSID()
            }
        }
        binding.btEdit.setOnClickListener {
            findNavController().navigate(AccountFragmentDirections.actionAccountFragmentToEditProfileFragment(user))
        }
        binding.btLogout.setOnClickListener {
                showlogOut()
        }
        return binding.root
    }


    private fun getWifiSSID() {
        val mWifiManager: WifiManager =
            ((activity as AppCompatActivity).applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
        val info: WifiInfo = mWifiManager.getConnectionInfo()
        activity?.runOnUiThread{
            binding.tvCode.text= info.ssid
        }
        sharedPrefsHelper.setCodeMachine(info.ssid)
        Log.d("_SSID", " ${info.ssid} ${info.bssid}, ${info.ipAddress}")

    }

    private fun showlogOut(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            logOut()
        }
        val negativeButtonClick= { _: DialogInterface, _: Int ->

        }
        with(builder) {
            setMessage("Bạn muốn thoát khỏi ứng dụng?")
            setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Hủy", DialogInterface.OnClickListener(negativeButtonClick))
        }
        builder.show()
    }

    private fun logOut() {
            auth.signOut()
            sharedPrefsHelper.clearAndPutLogout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
    }

    private fun getAccount(){
        binding.tvName.text = user.FullName
        binding.tvMail.text = user.Email
        binding.tvRoom.text= user.Position
        if (user.PhoneNumber.isNotEmpty()){
            binding.tvPhone.text= user.PhoneNumber
        }
        if (user.Sex.isNotEmpty()){
            binding.tvSex.text= user.Sex
        }
    }

}