package com.example.uimkk.ui.fragment

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.transition.TransitionManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.uimkk.*
import com.example.uimkk.Adapter.IconMainAdapter
import com.example.uimkk.databinding.MainFragmentBinding
import com.example.uimkk.model.IconProgram
import com.example.uimkk.ui.viewmodel.MainViewModel
import com.google.firebase.database.Transaction
import java.net.DatagramPacket
import java.net.DatagramSocket

class MainFragment : Fragment(), AdapterView.OnItemClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainFragmentBinding
    private lateinit var iconMainAdapter: IconMainAdapter
    private var iconMain: ArrayList<IconProgram> = arrayListOf()
    private lateinit var saveData: SaveData
    private lateinit var ipAddress:String
    private var port: Int=0
    private var  socketReceive= DatagramSocket()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()

        binding= DataBindingUtil.inflate(inflater,R.layout.main_fragment, container, false)
        iconMain = ArrayList()
        iconMain= setDataList()
        iconMainAdapter = IconMainAdapter(requireContext() , iconMain)
        binding.gridView.adapter= iconMainAdapter
        binding.gridView.onItemClickListener = this
        val pref= requireContext().getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
        //if ((pref.getString(IPADDRESS, "")!!.isNotEmpty()) && ((pref.getInt(PORT, 0).toString().isNotEmpty())  )){
            ipAddress= pref.getString(IPADDRESS, "")!!
            port= pref.getInt(PORT, 0)
        if ((ipAddress.isNotEmpty()) && (port != 0)){
                binding.edtIP.setText(ipAddress)
               binding.edtPort.setText(port.toString())
            }
        binding.btOk.setOnClickListener {
            with(pref.edit()){
                putString(IPADDRESS, binding.edtIP.text.toString())
                putInt(PORT, binding.edtPort.text.toString().toInt())
                apply()
            }
            ipAddress= binding.edtIP.text.toString()
            port= binding.edtPort.text.toString().toInt()        }

        saveData= SaveData(requireContext())
        saveData.loadLanguageState()

        return binding.root
    }


    fun setDataList():ArrayList<IconProgram>{
        val arraylist: ArrayList<IconProgram> = ArrayList()
        arraylist.add(IconProgram(R.drawable.program,getString(R.string.chuongtrinh)))
        arraylist.add(IconProgram(R.drawable.historyy, getString(R.string.lich_su)))
        arraylist.add(IconProgram(R.drawable.note, getString(R.string.kiem_tra)))
        arraylist.add(IconProgram(R.drawable.settingg, getString(R.string.cai_dat)))
        return arraylist
    }
    fun withEditText(view: View) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val inflater = layoutInflater
        builder.setTitle("Nhập mật khẩu")
        val dialogLayout = inflater.inflate(R.layout.alert_password, null)
        val editSpeed  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Ok") { dialogInterface, i ->
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToTestFragment(ipAddress, port))
        }
        builder.setNegativeButton("Hủy"){ dialogInterface, i ->  }
        val dialog = builder.create()
        dialog.show()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val iconItem: IconProgram = iconMain.get(position)
        when (iconItem.textName) {
            "Chương trình"-> {
               // socketReceive.close()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToProgramFragment())
            }
            "Program" -> {
              //  socketReceive.close()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToProgramFragment())
            }
            "Lịch sử"-> {
              //  socketReceive.close()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToHistoryFragment())
            }
            "History" ->{
               // socketReceive.close()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToHistoryFragment())
            }
            "Kiểm tra"->{
               // socketReceive.close()
                withEditText(requireView())
            }
            "Check" -> {
                //socketReceive.close()
                withEditText(requireView())
            }
            "Cài đặt"-> {
               // socketReceive.close()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingFragment(ipaddress = ipAddress, port = port))
            }
            "Setting" -> {
              //  socketReceive.close()
                findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingFragment(ipaddress = ipAddress, port = port))
            }
        }
    }




}