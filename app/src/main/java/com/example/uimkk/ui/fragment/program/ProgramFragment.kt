package com.example.uimkk.ui.fragment.program

import SwipeHelper
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uimkk.Adapter.IconProgramAdapter
import com.example.uimkk.MainActivity
import com.example.uimkk.R
import com.example.uimkk.databinding.ProgramFragmentBinding
import com.example.uimkk.model.Program
import com.example.uimkk.model.UserClass
import com.example.uimkk.ui.viewmodel.ProgramViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.create_program.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProgramFragment : Fragment(), IconProgramAdapter.OnItemButtonClick {

    companion object {
        fun newInstance() = ProgramFragment()
    }

    private lateinit var viewModel: ProgramViewModel
    private lateinit var binding: ProgramFragmentBinding
    private lateinit var programAdapter: IconProgramAdapter
    private var programs: ArrayList<Program> = arrayListOf()
    private var programFilter: ArrayList<Program> = arrayListOf()
    private var theTich:String=""
    private var nongDo:String=""
    private lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database: FirebaseDatabase? = null
    private var user:UserClass= UserClass()
    private var username:String=""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(this).get(ProgramViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.program_fragment, container, false)
        programAdapter = IconProgramAdapter(programs, this)
        getProgram()
        binding.programRecyclerView.apply {
            adapter = programAdapter
            layoutManager = LinearLayoutManager(context)
        }
        username= viewModel.getUsername()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.getReference("account")

        binding.floatingAddProgram.setOnClickListener {
           addProgram()
        }
     // delete program
//        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.programRecyclerView) {
//            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
//                var buttons = listOf<UnderlayButton>()
//                val deleteButton = deleteButton(position)
//                when (position) {
//                    position -> buttons = listOf(deleteButton)
//                    else -> Unit
//                }
//                return buttons
//            }
//        })
//
//        itemTouchHelper.attachToRecyclerView(binding.programRecyclerView)

            return binding.root
        }

//    private fun deleteButton(position: Int) : SwipeHelper.UnderlayButton {
//        return SwipeHelper.UnderlayButton(
//            requireContext(),
//            "Xóa",
//            14.0f,
//            android.R.color.holo_red_light,
//            object : SwipeHelper.UnderlayButtonClickListener {
//                override fun onClick() {
//                    programs.removeAt(position)
//                    programAdapter.notifyDataSetChanged()
//                    val db = FirebaseFirestore.getInstance()
//                    db.collection("programs").document().delete()
//                        .addOnSuccessListener {Toast.makeText(context, "Đã xóa chương trình", Toast.LENGTH_LONG).show() }
//                        .addOnFailureListener { e -> Toast.makeText(context, "$e", Toast.LENGTH_LONG).show() }
//                }
//            })
//    }

    fun getProgram(){
        val db = FirebaseFirestore.getInstance()
            db.collection("programs").get()
                .addOnSuccessListener { result ->

                    programs = ArrayList(result.map {
                        it.toObject<Program>()
                    })
                    Log.d("_FragmentProgram", programs.toString())
                    programAdapter.programs = programs
                    programAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Log.d("GETProgram", "Error getting documents: ", exception)
                }
    }

    fun addProgram(){
        val mDialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.create_program, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
            .setTitle("Tạo phòng")
        //show dialog
        val mAlertDialog = mBuilder.show()
        val btOk:Button= mDialogView.findViewById(R.id.btOk)
        val edtRoom:EditText= mDialogView.findViewById(R.id.edtRoom)
        val btCancel:Button = mDialogView.findViewById(R.id.btCancel1)
        val edtNongdo:EditText = mDialogView.findViewById(R.id.edtNongdo)
        val edtThetich:EditText = mDialogView.findViewById(R.id.edtThetich)
        val sdf = SimpleDateFormat("dd/MM/yyyy-HH:mm", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
            btOk.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val room = Program(
                    edtRoom.text.toString(),
                    edtNongdo.text.toString(),
                    edtThetich.text.toString(), currentDateandTime, user.name, UUID.randomUUID().toString()
                )
                db.collection("programs").document(room.id).set(room)
                    .addOnSuccessListener { documentReference ->
                        Log.d("ADD", "$nongDo  $theTich")
                        programAdapter.notifyDataSetChanged()
                        programs.add(room)
                        Toast.makeText(context, "Đã lưu chương trình", Toast.LENGTH_SHORT).show()
                        programAdapter.notifyDataSetChanged()
                        mAlertDialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.w("ADD", "có lỗi xảy ra", e)

                    }

            }
            btCancel.setOnClickListener {
                mAlertDialog.dismiss()
            }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_program, menu)
        val searchItem = menu.findItem(R.id.Search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setQueryHint("Tìm kiêm")
        searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null && newText.isNotEmpty()){
                    programFilter.clear()
                    programs.forEach {program ->
                        if(program.name.toLowerCase().contains(newText.toLowerCase())){
                            programFilter.add(program)
                        }
                    }
                    programAdapter.programs = programFilter
                    programAdapter.notifyDataSetChanged()
                }else {
                    programAdapter.programs = programs
                    programAdapter.notifyDataSetChanged()
                }

                return true
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    override fun onItemClick(program: Program) {
     requireView().findNavController().navigate(
         ProgramFragmentDirections.actionProgramFragmentToProgramDependFragment(
             program, username = username
         )
     )
    }

}