package com.example.uimkk.ui.fragment.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.uimkk.R
import com.example.uimkk.databinding.EditProfileFragmentBinding
import com.example.uimkk.model.User1
import com.example.uimkk.model.UserClass
import com.example.uimkk.ui.viewmodel.login.EditProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class EditProfileFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: EditProfileFragmentBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database: FirebaseDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding= DataBindingUtil.inflate(inflater, R.layout.edit_profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
        binding.btCancel1.setOnClickListener {
            requireActivity().onBackPressed()
        }
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.getReference("account")
        //loadProfile()
        val currentUser = auth.currentUser
        val currentUSerDb = databaseReference?.child((currentUser?.uid!!))
        val user= EditProfileFragmentArgs.fromBundle(requireArguments()).user
        binding.apply {
            edtName.setText(user.name)
            edtMail.setText(user.email)
            edtRoom.setText(user.organization)
            edtPhone.setText(user.phone)
            edtSex.setText(user.sex)

            btSave.setOnClickListener {
                val account= mapOf<String, String>("name" to binding.edtName.text.toString(), "sex" to binding.edtSex.text.toString(), "organization" to binding.edtRoom.text.toString(), "email" to  binding.edtMail.text.toString(), "phone" to binding.edtPhone.text.toString())
                currentUSerDb!!.updateChildren(account).addOnSuccessListener {
                  Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
              }
                  .addOnFailureListener {
                      Toast.makeText(context, "Cập nhật không thành công", Toast.LENGTH_SHORT).show()
                  }

                val userRoom =User1(1,binding.edtName.text.toString(),  binding.edtMail.text.toString())
                viewModel.updatetUser(userRoom)
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}