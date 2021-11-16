package com.cpc1hn.uimkk.ui.fragment.test

import android.animation.ObjectAnimator
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
    private  var B1 = 0x01
    private var B2= 0x01
    private var B3= 0x00
    private var B4 = 0x00
    private var B5 = 0x00
    private var B6= 0x01
    private lateinit var ipAddress:String
    private var port: Int=0
    private lateinit var saveData: SaveData
    private var socketReceive= DatagramSocket(null)
    private lateinit var animator: ObjectAnimator

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

        binding.imOverall.setOnClickListener {
            animator = ObjectAnimator.ofFloat( binding.imOverall , View.ROTATION, -360f, 0f )
            animator.duration = 2000
            animator.repeatCount= 4
            animator.start()
        }

        saveData= SaveData(requireContext())
        if (saveData.loadTemp().isNotEmpty()){
           binding.temperature.text= saveData.loadTemp()
           // binding.temperature.text= "0"
        }
        if (saveData.loadPer() != 0){
            binding.progressBarHorizontal1.progress = 0
            binding.tvTimePercent.text = "${saveData.loadPer()}%"
            //binding.tvTimePercent.text="0%"
        }

        receiveData()

        return binding.root
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

    private fun display(buffer: ByteArray){
        activity?.runOnUiThread {
            if ((buffer[0] == 0x03.toByte()) && (buffer[1] == 0x04.toByte()) && (buffer[6] == checkSum(
                    buffer
                ).toByte())
            ) {
                binding.tvNotify.text = "Đang kết nối"
                binding.imCheck.visibility= View.VISIBLE
                binding.imOverall.visibility= View.GONE
                saveData.setSpray(buffer[2].toUInt().toInt())
                saveData.setConnect(binding.tvNotify.text.toString())
                Log.d("_SCALE", "${buffer[4].toUByte().toInt()}, temp:${buffer[5].toUInt()}")
                binding.temperature.text = buffer[5].toUInt().toString()
                binding.progressBarHorizontal1.progress = (buffer[4].toInt())
                binding.tvTimePercent.text = "${buffer[4].toUInt()}%"
                saveData.setTemp(binding.temperature.text.toString())
                saveData.setPer(binding.progressBarHorizontal1.progress)

            }

        }
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

    private fun setMotoAC() {
        binding.quaynguoc.setOnClickListener {
            checkOn(B1, 0x03, B3, B4, B5, 0x00)
        }
        binding.quayxuoi.setOnClickListener {
            checkOn(B1, 0x03, B3, B4, B5, 0x01)
        }
    }

    private fun setBomNhuDong(){
        binding.btPump.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkOn(B1, 0x02, B3, B4, B5, B6)
                   binding.lnPump.setBackgroundResource(R.drawable.checked_true)
            }else {
                checkOff(B1, 0x02, B3, B4, B5, 0x00)
                binding.lnPump.setBackgroundResource(R.drawable.checked_false)
            }
        }

    }

    private fun setMotorBlow(){
        binding.btMotorBlow.setOnCheckedChangeListener { _, isChecked ->
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



    private fun setFan(){

        binding.btFanOnOff.setOnCheckedChangeListener { _, isChecked ->
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


    private fun setBuzzer(){
        binding.btNotifyOnOff.setOnCheckedChangeListener { _, isChecked ->
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

    override fun onPause() {
        super.onPause()
        checkOn(0x01, 0x0C, 0x00, 0x00, 0x00, 0x01)
        Log.d("_BACK", "OK")
    }

    private fun checkSum(b: ByteArray):Int{
        val sum = b[0] +b[1]+ b[2]+ b[3] +b[4]+ b[5]
        return sum
    }
    private fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        Log.d("_UDP", "Data: $B1-$B2-$B3-$B4-$B5-$B6-$B7")
        sendUDP(a)
    }
    private fun checkOff(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        Log.d("_UDP", "Data: $B1-$B2-$B3-$B4-$B5-$B6-$B7")
        sendUDP(a)
    }




    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    private fun sendUDP(messageStr: ByteArray) {
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

