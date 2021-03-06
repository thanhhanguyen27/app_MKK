package com.cpc1hn.uimkk.ui.fragment.login

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.cpc1hn.uimkk.MainActivity
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.LoginFragmentBinding
import com.cpc1hn.uimkk.helper.showToast
import com.cpc1hn.uimkk.ui.viewmodel.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import java.net.InetAddress

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding
    lateinit var auth: FirebaseAuth
    private lateinit var saveData:SaveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show()
       binding= DataBindingUtil.inflate(inflater,R.layout.login_fragment, container, false)
        auth = FirebaseAuth.getInstance()

        val currentuser = auth.currentUser
        if(currentuser != null) {
            startActivity(Intent( requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
        login()
        saveData= SaveData(requireContext())
        if (saveData.getMail().isNotEmpty()){
            binding.edtEmail.setText(saveData.getMail())
        }
        if (saveData.getPass().isNotEmpty()){
            binding.edtPass.setText(saveData.getPass())
        }

        binding.btRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        return binding.root
    }
    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }

    fun login(){
        binding.apply {
            btLogin.setOnClickListener {
                    if (TextUtils.isEmpty(edtEmail.text.toString())) {
                        edtEmail.error = "B???n c???n nh???p email! "
                        return@setOnClickListener
                    } else if (TextUtils.isEmpty(edtPass.text.toString())) {
                        edtPass.error = "B???n c???n ??i???n m???t kh???u! "
                        return@setOnClickListener
                    }

                    auth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPass.text.toString())
                        .addOnCompleteListener {task->
                            if (task.isSuccessful) {
                                startActivity(Intent(requireContext(), MainActivity::class.java))
                                Toast.makeText(
                                    context,
                                    "????ng nh???p th??nh c??ng ",
                                    Toast.LENGTH_LONG
                                ).show()
                                saveData.setMail(edtEmail.text.toString())

                            } else {
                                Toast.makeText(
                                    context,
                                    "????ng nh???p ch??a th??nh c??ng. Vui l??ng xem l???i th??ng tin ????ng nh???p ",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        }
                }


        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        // TODO: Use the ViewModel
    }

}