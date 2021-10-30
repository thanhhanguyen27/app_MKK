package com.cpc1hn.uimkk.ui.fragment.login

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.EditProfileFragmentBinding
import com.cpc1hn.uimkk.model.UserClass
import com.cpc1hn.uimkk.ui.viewmodel.login.EditProfileViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class EditProfileFragment : Fragment() {

    companion object {
        fun newInstance() = EditProfileFragment()
    }

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: EditProfileFragmentBinding
    private lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding= DataBindingUtil.inflate(inflater, R.layout.edit_profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(EditProfileViewModel::class.java)
        binding.btCancel1.setOnClickListener {
            requireActivity().onBackPressed()
        }
        val db = FirebaseFirestore.getInstance()
        val user= EditProfileFragmentArgs.fromBundle(requireArguments()).user
        binding.apply {
            edtName.setText(user.FullName)
            edtMail.setText(user.Email)
            edtRoom.setText(user.Position)
            edtPhone.setText(user.PhoneNumber)
            edtSex.setText(user.Sex)

            btSave.setOnClickListener {
                val accountRoom= UserClass(1, edtName.text.toString(), edtSex.text.toString(), edtRoom.text.toString(), edtMail.text.toString(), edtPhone.text.toString() )
               val account= hashMapOf("FullName" to edtName.text.toString(),
                   "Position" to edtRoom.text.toString(),
                   "Email" to edtMail.text.toString(),
                   "PhoneNumber" to edtPhone.text.toString(),
                   "Sex" to edtSex.text.toString()
                  )
                if (edtMail.text.toString().isNotEmpty()){
                    db.collection("accounts").whereEqualTo("Email", binding.edtMail.text.toString()).get().addOnSuccessListener {
                        for (document in it){
                            db.collection("accounts").document(document.id).set(account).addOnSuccessListener {
                                Toast.makeText(context,"Cập nhật thành công", Toast.LENGTH_LONG).show()
                                viewModel.updatetUser(accountRoom)
                                requireActivity().onBackPressed()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Cập nhật không thành công", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "Có lỗi xảy ra", Toast.LENGTH_LONG).show()
                    }

                }else{
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Không được để trống Email")
                        .setPositiveButton("Ok"){_, _ ->
                        }.show()
                }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

}