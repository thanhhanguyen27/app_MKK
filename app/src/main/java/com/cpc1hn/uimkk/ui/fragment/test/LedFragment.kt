package com.cpc1hn.uimkk.ui.fragment.test

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.LedFragmentBinding
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class LedFragment : Fragment() {

    companion object {
        fun newInstance() = LedFragment()
    }

    private lateinit var viewModel: LedViewModel
    private lateinit var a:ByteArray
    private var temp = 50
    private  var B1 = 0x01
    private var B2= 0x01
    private var B3= 0x00
    private var B4 = 0x00
    private var B5 = 0x00
    private var B6= 0x01
    private var B7= 0x00
    private lateinit var ipAddress:String
    private var port: Int=0
    private lateinit var saveData: SaveData
    private  var TAG = "_TEST"
    private var socketReceive= DatagramSocket(null)
    private lateinit var binding: LedFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= DataBindingUtil.inflate(inflater, R.layout.led_fragment, container, false)
        ipAddress= "192.168.4.1"
        port= 8080
        binding.apply {
            binding.btledRed.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    checkOn(B1, 0x05, B3, B4, B5, B6)
                    checkOff(B1, 0x06, B3, B4, B5, 0x00)
                    checkOff(B1, 0x07, B3, B4, B5, 0x00)
                }else{
                    checkOff(B1, 0x05, B3, B4, B5, 0x00)
                }
            }
            binding.btledGreen.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    checkOn(B1, 0x06, B3, B4, B5, B6)
                    checkOff(B1, 0x05, B3, B4, B5, 0x00)
                    checkOff(B1, 0x07, B3, B4, B5, 0x00)
                 }else{
                     checkOff(B1, 0x06, B3, B4, B5, 0x00)

                 }
            }
            binding.btledBlue.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    checkOn(B1, 0x07, B3, B4, B5, B6)
                    checkOff(B1, 0x05, B3, B4, B5, 0x00)
                    checkOff(B1, 0x06, B3, B4, B5, 0x00)

                }else{
                    checkOff(B1, 0x07, B3, B4, B5, 0x00)
                }
             }
        }
        return binding.root
    }

    fun checkSum(b: ByteArray):Int{
        val sum = b[0] +b[1]+ b[2]+ b[3] +b[4]+ b[5]
        return sum
    }
    fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        Log.d("_UDP", "Data: $B1-$B2-$B3-$B4-$B5-$B6-$B7")
        sendUDP(a)
    }
    fun checkOff(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
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
            val sendData = messageStr
            val sendPacket = DatagramPacket(
                sendData,
                sendData.size,
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LedViewModel::class.java)
        // TODO: Use the ViewModel
    }

}