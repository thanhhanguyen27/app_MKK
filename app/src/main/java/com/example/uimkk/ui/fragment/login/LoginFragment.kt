package com.example.uimkk.ui.fragment.login

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
import com.example.uimkk.MainActivity
import com.example.uimkk.R
import com.example.uimkk.databinding.LoginFragmentBinding
import com.example.uimkk.ui.viewmodel.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: LoginFragmentBinding
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
       binding= DataBindingUtil.inflate(inflater,R.layout.login_fragment, container, false)
        auth = FirebaseAuth.getInstance()

        val currentuser = auth.currentUser
        if(currentuser != null) {
            startActivity(Intent( requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }
        login()

        binding.btRegister.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }

        return binding.root
    }

    fun login(){
        binding.apply {
            btLogin.setOnClickListener {
                if (TextUtils.isEmpty(edtEmail.text.toString())) {
                    edtEmail.setError("Bạn cần nhập email! ")
                    return@setOnClickListener
                } else if (TextUtils.isEmpty(edtPass.text.toString())) {
                    edtPass.setError("Bạn cần điền mật khẩu! ")
                    return@setOnClickListener
                }

                auth.signInWithEmailAndPassword(edtEmail.text.toString(), edtPass.text.toString())
                    .addOnCompleteListener {task->
                        if (task.isSuccessful) {
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                            Toast.makeText(
                                context,
                                "Đăng nhập thành công ",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Đăng nhập chưa thành công. Vui lòng xem lại thông tin đăng nhập ",
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