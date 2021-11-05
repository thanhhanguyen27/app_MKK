package com.cpc1hn.uimkk.ui.fragment.setting

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.ActivateFragmentBinding
import com.cpc1hn.uimkk.ui.viewmodel.setting.ActivateViewModel
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class ActivateFragment : Fragment() {

    companion object {
        fun newInstance() = ActivateFragment()
    }

    private lateinit var viewModel: ActivateViewModel
    private lateinit var binding:ActivateFragmentBinding
    private lateinit var ipAddress:String
    private var port: Int=0
    private lateinit var a:ByteArray
    private  var B1 = 0x02
    private var B2= 0x01
    private var B3= 0x00
    private var B4 = 0x00
    private var B5 = 0x00
    private var B6= 0x01
    private var socketReceive= DatagramSocket(null)
    private lateinit var saveData: SaveData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= DataBindingUtil.inflate(inflater, R.layout.activate_fragment, container, false)
        val toolbar= binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ipAddress= "192.168.4.1"
        port=8080
        saveData= SaveData(requireContext())
        //get data
        checkOn(0x02, 0x09, B3, B4, B5, 0x01)
        receiveData1()
        binding.apply {
            binding.btSwitchLED.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkOn(B1, B2, B3, B4, B5, B6)
                } else {
                    checkOff(B1, B2, B3, B4, B5, 0x00)
                }
            }
            btSwitchFan.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    checkOn(B1, 0x04, B3, B4, B5, B6)
                }else {
                    checkOff(B1, 0x04, B3, B4, B5, 0x00)
                }
            }
            btSwitchBuzzer.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    checkOn(B1, 0x03, B3, B4, B5, B6)
                    saveData.setActiveScale(true)
                }else {
                    checkOff(B1, 0x03, B3, B4, B5, 0x00)
                }
            }
            btSwitchScale.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked){
                    checkOn(B1, 0x02, B3, B4, B5, B6)
                    saveData.setActiveScale(true)
                }else {
                    checkOff(B1, 0x02, B3, B4, B5, 0x00)
                    saveData.setActiveScale(false)
                }
            }
        }
        return binding.root
    }

    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    private fun checkSum(b: ByteArray): Int {
        return b[0] + b[1] + b[2] + b[3] + b[4] + b[5]
    }

   private fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
       Log.d("_ACTIVE", "send udp")
        sendUDP(a)
    }

    private fun checkOff(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
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
                        Log.d("_UDP", "receive data = ${buffer}")
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
            //Receive LED, Scale, Fan, Buzzer
            if ((b[0] == 0x02.toByte()) && (b[1] == 0x09.toByte()) && (b[6] == checkSum(b).toByte())) {
                Log.d("_UDP", "LED, Scale, Fan, Buzzer")
                if (b[2] == 0x01.toByte()) {
                    binding.btSwitchLED.isChecked = true
                } else if (b[2] == (0x00).toByte()) {
                    binding.btSwitchLED.isChecked = false
                }
                if (b[3] == 0x01.toByte()) {
                    binding.btSwitchScale.isChecked = true
                } else if (b[3] == 0x00.toByte()) {
                    binding.btSwitchScale.isChecked = false
                }
                if (b[4] == 0x01.toByte()) {
                    binding.btSwitchBuzzer.isChecked = true
                } else if (b[4] == 0x00.toByte()) {
                    binding.btSwitchBuzzer.isChecked = false
                }
                if (b[5] == 0x01.toByte()) {
                    binding.btSwitchFan.isChecked = true
                } else if (b[5] == 0x00.toByte()) {
                    binding.btSwitchFan.isChecked = false
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ActivateViewModel::class.java)
        // TODO: Use the ViewModel
    }

}