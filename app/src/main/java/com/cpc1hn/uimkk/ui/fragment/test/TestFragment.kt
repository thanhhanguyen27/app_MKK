package com.cpc1hn.uimkk.ui.fragment.test

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.TestFragmentBinding
import com.cpc1hn.uimkk.ui.viewmodel.TestViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class TestFragment : Fragment() {

    companion object {
        fun newInstance() = TestFragment()
    }

    private lateinit var viewModel: TestViewModel
    private lateinit var binding: TestFragmentBinding
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
    //private lateinit var saveData: SaveData
    private  var TAG = "_TEST"
    private var socketReceive= DatagramSocket(null)
    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        binding= DataBindingUtil.inflate(inflater, R.layout.test_fragment, container, false)
        ipAddress= "192.168.4.1"
        port= 8080
        //connect
        checkOn(0x01, 0x0B, 0x00, 0x00, 0x00, 0x01)
        if ((ipAddress.isNotEmpty()) && (port != 0) ){
            setBomNhuDong()
            setMotorBlow()
            setFan()
            setBuzzer()
            setMotoAC()
//            setLedRed()
//            setLedGreen()
//            setLedBlue()
        }


        saveData= SaveData(requireContext())
//        if (saveData.loadTemp().isNotEmpty()){
//            binding.arcProgress.progress= saveData.loadTemp().toInt()
//        }
        if (saveData.loadPer() != 0){
          //  binding.progressBarHorizontal1.progress = saveData.loadPer()
            binding.tvTimePercent.setText("${saveData.loadPer()}%")
        }
        Log.d("_UDP", "Send Data ")

        ReceiveData(port)

        return binding.root
    }

    fun ReceiveData(portNum: Int) {
        var buffer = ByteArray(1000)
        Thread(
            Runnable {
                try {
                    while (true) {
                        socketReceive = DatagramSocket(null)
                        Log.d("_UDP", "ReceiveData ")
                        socketReceive.reuseAddress=true
                        socketReceive.broadcast = true
                        socketReceive.bind(InetSocketAddress(8081))
                        //socketReceive.setSoTimeout(10000)
                        val data = DatagramPacket(buffer, buffer.size)
                        socketReceive.receive(data)
                        Log.d("_UDP", "ReceiveData data= ${buffer}")
                        display(buffer)
                        //reset lại mảng
                        buffer = byteArrayOfInts(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00)
                    }
                } catch (e: java.lang.Exception) {
                    Log.d("_UDPRC", " ${e.printStackTrace()}")
                    e.printStackTrace()
                }

            }
        ).start()
    }

    fun display(buffer: ByteArray){
        getActivity()?.runOnUiThread(java.lang.Runnable {
            if ((buffer[0] == 0x03.toByte()) && (buffer[1] == 0x04.toByte()) && (buffer[6] == checkSum(
                    buffer
                ).toByte())
            ) {
                binding.tvNotify.setText("Đang kết nối")
                saveData.setConnect(binding.tvNotify.text.toString())
                binding.arcProgress.progress = buffer[5].toInt()
                Log.d(TAG, "${buffer[5].toInt()}  ${buffer[4].toInt()}")
                binding.progressBarHorizontal1.progress = (buffer[4].toInt())
                binding.tvTimePercent.setText("${buffer[4].toInt()}%")
                saveData.setTemp(binding.arcProgress.progress.toString())
                saveData.setPer(binding.progressBarHorizontal1.progress)

            }

        })
    }

//    fun setLedRed(){
//        binding.btLedRed.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked){
//                checkOn(B1, 0x05, B3, B4, B5, B6)
//                checkOff(B1, 0x06, B3, B4, B5, 0x00)
//                checkOff(B1, 0x07, B3, B4, B5, 0x00)
//                binding.lnLedRed.setBackgroundResource(R.drawable.checked_true)
//            }else{
//                checkOff(B1, 0x05, B3, B4, B5, 0x00)
//                binding.lnLedRed.setBackgroundResource(R.drawable.checked_false)
//            }
//        }
//    }
//
//    fun setLedBlue(){
//        binding.btLedBlue.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked){
//                checkOn(B1, 0x07, B3, B4, B5, B6)
//                checkOff(B1, 0x05, B3, B4, B5, 0x00)
//                checkOff(B1, 0x06, B3, B4, B5, 0x00)
//                binding.lnLedBlue.setBackgroundResource(R.drawable.checked_true)
//
//            }else{
//                checkOff(B1, 0x07, B3, B4, B5, 0x00)
//                binding.lnLedBlue.setBackgroundResource(R.drawable.checked_false)
//            }
//        }
//    }
//
//    fun setLedGreen(){
//        binding.btLedGreen.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked){
//                checkOn(B1, 0x06, B3, B4, B5, B6)
//                checkOff(B1, 0x05, B3, B4, B5, 0x00)
//                checkOff(B1, 0x07, B3, B4, B5, 0x00)
//                binding.lnLedGreen.setBackgroundResource(R.drawable.checked_true)
//            }else{
//                checkOff(B1, 0x06, B3, B4, B5, 0x00)
//                binding.lnLedGreen.setBackgroundResource(R.drawable.checked_false)
//            }
//        }
//    }

    fun setMotoAC() {
        binding.quaynguoc.setOnClickListener {
            checkOn(B1, 0x03, B3, B4, B5, 0x00)
        }
        binding.quayxuoi.setOnClickListener {
            checkOn(B1, 0x03, B3, B4, B5, 0x01)
        }
    }

    fun setBomNhuDong(){
        binding.btPump.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkOn(B1, 0x02, B3, B4, B5, B6)
                   binding.lnPump.setBackgroundResource(R.drawable.checked_true)
            }else {
                checkOff(B1, 0x02, B3, B4, B5, 0x00)
                binding.lnPump.setBackgroundResource(R.drawable.checked_false)
            }
        }

    }

    fun setMotorBlow(){
        binding.btMotorBlow.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkOn(B1, B2, B3, B4, B5, B6)
                binding.lnThoi.setBackgroundResource(R.drawable.checked_true)
//                binding.tvThoi.setTextColor(R.attr.Text_light)
            }else {
                checkOff(B1, B2, B3, B4, B5, 0x00)
               binding.lnThoi.setBackgroundResource(R.drawable.checked_false)
//                binding.tvThoi.setTextColor(R.attr.Text_head)
            }
        }

    }



    fun setFan(){

        binding.btFanOnOff.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                checkOn(B1, 0x04, B3, B4, B5, B6)
                binding.lnFan.setBackgroundResource(R.drawable.checked_true)
//                binding.tvFan.setTextColor(R.attr.Text_light)
            }else {
                checkOff(B1, 0x04, B3, B4, B5, 0x00)
                binding.lnFan.setBackgroundResource(R.drawable.checked_false)
//                binding.tvFan.setTextColor(R.attr.Text_head)
            }
        }
    }


    fun setBuzzer(){
        binding.btNotifyOnOff.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                checkOn(B1, 0x08, B3, B4, B5, B6)
               binding.lnNotification.setBackgroundResource(R.drawable.checked_true)
//                binding.tvNotify.setTextColor(R.attr.Text_light)
            }else{
                checkOff(B1, 0x08, B3, B4, B5, 0x00)
               binding.lnNotification.setBackgroundResource(R.drawable.checked_false)
//                binding.tvNotification.setTextColor(R.attr.Text_head)
            }
        }

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
        viewModel = ViewModelProviders.of(this).get(TestViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStop() {
        super.onStop()
        checkOn(0x01, 0x0C, 0x00, 0x00, 0x00, 0x01)
    }

}

