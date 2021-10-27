package com.cpc1hn.uimkk.ui.fragment.history

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.HistoryRetailFragmentBinding
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.ui.viewmodel.history.HistoryRetailViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.*

class HistoryRetailFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryRetailFragment()
    }

    private lateinit var viewModel: HistoryRetailViewModel
    private lateinit var binding: HistoryRetailFragmentBinding
    private lateinit var history: History

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding= DataBindingUtil.inflate(inflater,R.layout.history_retail_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(HistoryRetailViewModel::class.java)
        // sendUDP(binding.tvCreater.text.toString())
        history= HistoryRetailFragmentArgs.fromBundle(requireArguments()).history
        val time= longToDate(history.timeEnd, "dd/MM/yyyy")
        Log.d("_HIS", "${history.creater} ${history.timeRun}")
        binding.history= history
        binding.tvTimeEnd.text= "${time} ${history.hourEnd}"
        binding.tvTimeCreate.text= "${history.timeCreate}"

        return binding.root
    }

    fun longToDate(data: Long, format: String?): String? {
        val date = Date(data)
        val df2 = SimpleDateFormat(format, Locale.getDefault())
        return df2.format(date)
    }

    fun sendUDP(messageStr: String) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
//Open a port to send the package
            val socket = DatagramSocket()
            socket.broadcast = true
            val sendData = messageStr.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName("=192.168.0.103"), 80)
            socket.send(sendPacket)
        } catch (e: IOException) {
// Log.e(FragmentActivity.TAG, "IOException: " + e.message)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryRetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_delete, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Delete -> {
                showNotify()
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    fun showNotify(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            val db = FirebaseFirestore.getInstance()
            db.collection("history").document(history.id.toString()).delete()
            viewModel.deleteHistoryInfo()
            requireActivity().onBackPressed()
        }
        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
        }
        with(builder) {
            setMessage("Xóa bản ghi này?")
            setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton(
                "Hủy",
                DialogInterface.OnClickListener(function = negativeButtonClick)
            )
        }
        builder.show()

    }

}