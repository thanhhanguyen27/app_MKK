package com.example.uimkk.ui.fragment.login

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
import com.example.uimkk.LoginActivity
import com.example.uimkk.R
import com.example.uimkk.databinding.RegisterFragmentBinding
import com.example.uimkk.model.UserClass
import com.example.uimkk.ui.viewmodel.login.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

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
    ): View? {
        (activity as LoginActivity).supportActionBar?.show()
       // (activity as LoginActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding= DataBindingUtil.inflate(inflater, R.layout.register_fragment, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.getReference("account")

        register()
        binding.btCancel.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
    fun register(){
        binding.apply {
            btRegister.setOnClickListener {
                if (TextUtils.isEmpty(edtEmail.text.toString())) {
                    edtEmail.setError("Bạn cần nhập email! ")
                    return@setOnClickListener
                } else if (TextUtils.isEmpty(edtPass.text.toString())) {
                    edtPass.setError("Bạn cần điền mật khẩu! ")
                    return@setOnClickListener
                }
                auth.createUserWithEmailAndPassword(edtEmail.text.toString(), edtPass.text.toString())
                    .addOnCompleteListener(requireActivity()) {task->
                        if(task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val currentUSerDb = databaseReference?.child((currentUser?.uid!!))
                           val account: UserClass= UserClass(edtName.text.toString(),"",edtRoom.text.toString(),edtEmail.text.toString(), "")
                            currentUSerDb!!.setValue(account)
                            Toast.makeText(requireContext(), " Đăng ký thành công! ", Toast.LENGTH_LONG).show()
                            requireActivity().onBackPressed()
                        } else {
                            Toast.makeText(requireContext(), " Đăng ký thất bại!", Toast.LENGTH_LONG).show()
                        }
                    }


            }
        }
    }


    fun showAlert(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            requireActivity().onBackPressed()
        }
        with(builder) {
            setMessage("Đăng ký tài khoản thành công.")
            setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
        }
        //builder.setTitle(Html.fromHtml("<font color='#b71c1c'>Cảnh báo</font>"))
        builder.show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        // TODO: Use the ViewModel
    }

}