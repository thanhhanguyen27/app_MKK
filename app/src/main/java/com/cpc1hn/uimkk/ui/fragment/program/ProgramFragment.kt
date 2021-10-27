package com.cpc1hn.uimkk.ui.fragment.program

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpc1hn.uimkk.Adapter.IconProgramAdapter
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.ProgramFragmentBinding
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.model.UserClass
import com.cpc1hn.uimkk.ui.viewmodel.ProgramViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProgramFragment : Fragment(), IconProgramAdapter.OnItemButtonClick  {

    companion object {
        fun newInstance() = ProgramFragment()
    }
    private lateinit var viewModel: ProgramViewModel
    private lateinit var binding: ProgramFragmentBinding
    private lateinit var programAdapter: IconProgramAdapter
    private var programs: ArrayList<Program> = arrayListOf()
    private var programFilter: ArrayList<Program> = arrayListOf()
    private lateinit var auth: FirebaseAuth
    var databaseReference : DatabaseReference? = null
    var database: FirebaseDatabase? = null
    private var user:UserClass= UserClass()
    private lateinit var ipAddress:String
    private var port: Int=0
    private var socketReceive= DatagramSocket(null)
    private lateinit var a:ByteArray



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.hide()
        viewModel = ViewModelProviders.of(this).get(ProgramViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.program_fragment, container, false)
        programAdapter = IconProgramAdapter(programs, this)
        getProgram()
        getAccount()
        ipAddress= "192.168.4.1"
        port= 8080
        checkOn(0x01, 0x0B, 0x00, 0x00, 0x00, 0x01)

        binding.programRecyclerView.apply {
            adapter = programAdapter
            layoutManager = LinearLayoutManager(context)
        }
        //username= viewModel.getUser().name
        binding.btAdd.setOnClickListener {
           addProgram()
        }
        binding.imAccount.setOnClickListener {
            findNavController().navigate(ProgramFragmentDirections.actionNavHomeToAccountFragment(user))
        }
        search()
        receiveData()
// setup navigation

            return binding.root
        }
    fun search(){
        binding.imSearch.setOnClickListener (object :View.OnClickListener {
            override fun onClick(v: View?) {
                TransitionManager.beginDelayedTransition(binding.lnMain)
                if (binding.searchView.visibility== View.GONE) {
                    binding.searchView.visibility = View.VISIBLE
                    binding.edtSearch.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            if (binding.edtSearch.text.toString().isNotEmpty()) {
                                binding.btClear.visibility = View.VISIBLE
                            } else {
                                binding.btClear.visibility = View.GONE
                            }
                            binding.btClear.setOnClickListener { s!!.removeRange(0, 0) }
                        }

                        override fun afterTextChanged(s: Editable?) {
                            binding.btClear.setOnClickListener { s!!.clear() }

                            if (s != null && s.isNotEmpty()) {
                                programFilter.clear()
                                programs.forEach { program ->
                                    if (program.NameProgram.toLowerCase()
                                            .contains(s.toString().toLowerCase())
                                    ) {
                                        programFilter.add(program)
                                    }
                                }
                                programAdapter.programs = programFilter
                                programAdapter.notifyDataSetChanged()
                            } else {
                                programAdapter.programs = programs
                                programAdapter.notifyDataSetChanged()
                            }

                        }

                    })
                }else{
                    binding.searchView.visibility = View.GONE
                }
            }
        })

    }

    private fun getAccount(){
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database?.getReference("account")
        //loadProfile()
        val currentUser = auth.currentUser
        val currentUSerDb = databaseReference?.child((currentUser?.uid!!))
        currentUSerDb!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(UserClass::class.java)!!
                val user1 =
                    UserClass(user.id, user.name, user.phone,user.organization,user.email, user.sex)
                viewModel.insertUser(user1)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun receiveData() {
        var buffer = ByteArray(1000)
        Thread {
            try {
                while (true) {
                    socketReceive = DatagramSocket(null)
                    Log.d("_UDP", "ReceiveData ")
                    socketReceive.reuseAddress = true
                    socketReceive.broadcast = true
                    socketReceive.bind(InetSocketAddress(8081))
                    //socketReceive.setSoTimeout(10000)
                    val data = DatagramPacket(buffer, buffer.size)
                    socketReceive.receive(data)
                    display(buffer)
                    //reset lại mảng
                    buffer = byteArrayOfInts(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
                }
            } catch (e: java.lang.Exception) {
                Log.d("_UDPRC", " ${e.printStackTrace()}")
                e.printStackTrace()
            }

        }.start()
    }

    fun display(buffer: ByteArray){
        activity?.runOnUiThread {
            if ((buffer[0] == 0x03.toByte()) && (buffer[1] == 0x04.toByte()) && (buffer[6] == checkSum(
                    buffer
                ).toByte())
            ) {
                binding.tvConnect.text = "Đang kết nối"
            }
        }
    }

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
                    edtNongdo.text.toString().toLong(),
                    edtThetich.text.toString().toLong(), currentDateandTime, user.name, UUID.randomUUID().toString()
                )
                db.collection("programs").document(room.id).set(room)
                    .addOnSuccessListener {
                        getProgram()
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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_program, menu)
        val searchItem = menu.findItem(R.id.Search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = "Tìm kiếm"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null && newText.isNotEmpty()){
                    programFilter.clear()
                    programs.forEach {program ->
                        if(program.NameProgram.toLowerCase().contains(newText.toLowerCase())){
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
         ProgramFragmentDirections.actionNavHomeToProgramDependFragment(
             program, username = viewModel.getUsername()
         )
     )
    }

    override fun delete(program: Program) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
           deleteProgram(program)
        }
        val negativeButtonClick= { _: DialogInterface, _: Int ->

        }
        with(builder) {
            setMessage("Xóa chương trình?")
            setPositiveButton(
                "Xóa",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Hủy", DialogInterface.OnClickListener(negativeButtonClick))
        }
        builder.show()

    }
    fun deleteProgram(program: Program){
        programs.remove(program)
        val db = FirebaseFirestore.getInstance()
        db.collection("programs").document(program.id).delete()
            .addOnSuccessListener {Toast.makeText(context, "Đã xóa chương trình", Toast.LENGTH_LONG).show() }
            .addOnFailureListener { e -> Toast.makeText(context, "$e", Toast.LENGTH_LONG).show() }
        programAdapter.notifyDataSetChanged()
    }

    override fun seeMore(program: Program) {
        requireView().findNavController().navigate(
            ProgramFragmentDirections.actionNavHomeToProgramDependFragment(
                program, username = viewModel.getUsername()
            )
        )
    }

    fun checkSum(b: ByteArray): Int {
        return b[0] + b[1] + b[2] + b[3] + b[4] + b[5]
    }
    fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        Log.d("_UDP", "Data: $B1-$B2-$B3-$B4-$B5-$B6-$B7")
        sendUDP(a)
    }
    fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    fun sendUDP(messageStr: ByteArray) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
//Open a port to send the package
            val socket = DatagramSocket(8082)
            socket.broadcast = true
            socket.reuseAddress=true
            val sendPacket = DatagramPacket(
                messageStr,
                messageStr.size,
                InetAddress.getByName(ipAddress),
                port
            )
            socket.send(sendPacket)
            Log.d("_UDP", "$sendPacket")
            if (!socket.isClosed){
                socket.close()
                Log.d("_UDP", "closed")
            }
        } catch (e: IOException) {
            Log.e("_UDP", "IOException: " + e.message)
        }

    }

}