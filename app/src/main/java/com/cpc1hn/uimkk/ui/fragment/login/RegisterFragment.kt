package com.cpc1hn.uimkk.ui.fragment.login

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.LoginActivity
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.RegisterFragmentBinding
import com.cpc1hn.uimkk.model.UserClass
import com.cpc1hn.uimkk.ui.viewmodel.login.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.edit_profile_fragment.*

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var viewModel: RegisterViewModel
    private lateinit var binding: RegisterFragmentBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database: FirebaseDatabase? = null
    private var accounts: ArrayList<UserClass> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false)
        val toolbar= binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.getReference("account")

        register()
        binding.btCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
    private fun register(){
        binding.apply {
            btRegister.setOnClickListener {
                if (TextUtils.isEmpty(edtEmail.text.toString())) {
                    edtEmail.error = "Bạn cần nhập email! "
                    return@setOnClickListener
                } else if (TextUtils.isEmpty(edtPass.text.toString())) {
                    edtPass.error = "Bạn cần điền mật khẩu! "
                    return@setOnClickListener
                }
                auth.createUserWithEmailAndPassword(edtEmail.text.toString(), edtPass.text.toString())
                    .addOnCompleteListener(requireActivity()) {task->
                        if(task.isSuccessful) {
                         //   val account= UserClass(1,edtName.text.toString(),"",edtRoom.text.toString(),edtEmail.text.toString(), "")
                           val account= hashMapOf("FullName" to edtName.text.toString(),
                           "Position" to edtRoom.text.toString(),
                           "Email" to edtEmail.text.toString(),
                           "Password" to edtPass.text.toString())
                            val db = FirebaseFirestore.getInstance()
                            db.collection("accounts")
                                .add(account)
                                .addOnSuccessListener { _ ->

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Có lỗi xảy ", Toast.LENGTH_LONG).show()
                                }
                            Toast.makeText(requireContext(), " Đăng ký thành công! ", Toast.LENGTH_LONG).show()
                            val saveData= SaveData(requireContext())
                            saveData.setMail(edtEmail.text.toString())
                            requireActivity().onBackPressed()
                        } else {
                            Toast.makeText(requireContext(), " Đăng ký thất bại! ${task.exception}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
    }

}