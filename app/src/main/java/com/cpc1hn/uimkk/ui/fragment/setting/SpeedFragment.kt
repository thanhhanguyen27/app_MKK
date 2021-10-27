package com.cpc1hn.uimkk.ui.fragment.setting

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.SpeedFragmentBinding
import com.cpc1hn.uimkk.ui.viewmodel.setting.SpeedViewModel
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class SpeedFragment : Fragment() {

    companion object {
        fun newInstance() = SpeedFragment()
    }

    private lateinit var viewModel: SpeedViewModel
    private lateinit var binding:SpeedFragmentBinding
    private lateinit var ipAddress:String
    private var port: Int=0
    private lateinit var a:ByteArray
    private var B3= 0x00
    private var B4 = 0x00
    private var B5 = 0x00
    private var socketReceive= DatagramSocket(null)
    private lateinit var editSpeed:EditText
    private lateinit var saveData: SaveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding= DataBindingUtil.inflate(inflater,R.layout.speed_fragment, container, false)
        ipAddress="192.168.4.1"
        port=8080
        saveData= SaveData(requireContext())
        if (saveData.loadSpray().isNotEmpty()){
            binding.tvSpeedSpray.text= saveData.loadSpray()
        }
        //get data Temp, Speed
        checkOn(0x02, 0x09, B3, B4, B5, 0x02)
        receiveData1()
        binding.btEditSpeedSpray.setOnClickListener {
            DialogEditSpeed()
        }
        val items = listOf("Chậm", "Bình thường", "Nhanh")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.tocdoquay.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.btSet.setOnClickListener {
            if (binding.textSpeed.text.isEmpty()){
                val builder1 = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                val positiveButtonClick = { dialog: DialogInterface, which: Int ->

                }
                with(builder1) {
                    setMessage("Bạn chưa chọn tốc độ quay. Vui lòng thiết lập lại.")
                    setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener(function = positiveButtonClick)
                    )
                }
                val dialog1 = builder1.create()
                dialog1.show()
            }else{
                if (binding.textSpeed.text.toString() =="Chậm"){
                    checkOn(0x02, 0x07, 0x00, 0x00, 0x00, 0x00)
                }
                if (binding.textSpeed.text.toString() =="Bình thường"){
                    checkOn(0x02, 0x07, 0x00, 0x00, 0x00, 0x01)
                }
                if (binding.textSpeed.text.toString() =="Nhanh"){
                    checkOn(0x02, 0x07, 0x00, 0x00, 0x00, 0x02)
                }
                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                val positiveButtonClick = { dialog: DialogInterface, _: Int ->

                }
                with(builder) {
                    setMessage("Thiết lập tốc độ quay thành công.")
                    setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener(function = positiveButtonClick)
                    )
                }
                val dialog = builder.create()
                dialog.show()

            }
        }


        return  binding.root
    }

    private fun DialogEditSpeed(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val inflater = layoutInflater
        builder.setTitle("Tốc độ phun (30-37 ml/phút)")
        val dialogLayout = inflater.inflate(R.layout.alert_password, null)
        editSpeed  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Lưu") { _, _ ->
            checkOn(0x02, 0x06, 0x00, 0x00, 0x00, editSpeed.text.toString().toInt())
            if (( editSpeed.text.toString().toInt() >=30) && ( editSpeed.text.toString().toInt()<=37)) {
                binding.tvSpeedSpray.text = editSpeed.text
            }
            if ( editSpeed.text.toString().toInt()<30){
                binding.tvSpeedSpray.text = "30"
            }
            if ( editSpeed.text.toString().toInt()>37){
                binding.tvSpeedSpray.text = "37"
            }
            saveData.setSpray(binding.tvSpeedSpray.text.toString())
        }
        builder.setNegativeButton("Hủy"){ _, _ ->  }
        val dialog = builder.create()
        dialog.show()

       // builder.setPositiveButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.or))
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SpeedViewModel::class.java)
        // TODO: Use the ViewModel
    }


   private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    private fun checkSum(b: ByteArray): Int {
        return b[0] + b[1] + b[2] + b[3] + b[4] + b[5]
    }

   private fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        sendUDP(a)
    }

    private fun sendUDP(messageStr: ByteArray) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
//Open a port to send the package
            val socket = DatagramSocket(8082)
            socket.reuseAddress=true
            socket.broadcast=true
            //socketReceive= DatagramSocket(port)
            val sendPacket = DatagramPacket(
                messageStr,
                messageStr.size,
                InetAddress.getByName(ipAddress),
                port
            )
            socket.send(sendPacket)
            Log.d("_UDP2", "$sendPacket")
            if (!socket.isClosed){
                socket.close()
                Log.d("_UDP", "closed")
            }
        } catch (e: IOException) {
            Log.e("_UDP23", "IOException: " + e.message)
        }
    }

   private fun receiveData1(){
        var buffer = ByteArray(6566)
        object : Thread() {
            override fun run() {
                try {
                    //val socketReceive = DatagramSocket(portNum)
                    socketReceive = DatagramSocket(null)
                    socketReceive.reuseAddress=true
                    socketReceive.broadcast=true
                    socketReceive.bind(InetSocketAddress(8081))
                    while (true) {
                        val data = DatagramPacket(buffer, buffer.size)
                        socketReceive.receive(data)
                        display1(buffer)
                        buffer = byteArrayOfInts(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

   private fun display1(b: ByteArray) {
        activity?.runOnUiThread {
            //receive Temp, Speed
            if ((b[0] == 0x02.toByte()) && (b[1] == 0x09.toByte()) && (b[5] == 0x02.toByte()) && (b[6] == checkSum(
                    b
                ).toByte())
            ) {
                Log.d("_UDP", "Speed")
                binding.tvSpeed.setText(b[4].toInt().toString())
                "$${b[4].toInt()} ml/phút".also { binding.tvSpeedSpray.text = it }

            }
        }
   }

}