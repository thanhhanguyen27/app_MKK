package com.cpc1hn.uimkk.ui.fragment.program


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.databinding.ProgramRetailFragmentBinding
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.ui.viewmodel.program.ProgramRetailViewModel
import com.google.common.primitives.UnsignedBytes
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ProgramRetailFragment: Fragment(){

    companion object {
        fun newInstance() = ProgramRetailFragment()
    }

    private lateinit var viewModel: ProgramRetailViewModel
    private lateinit var binding: ProgramRetailFragmentBinding
    private lateinit var ipAddress:String
    private var port: Int=0
    private var timeCreate: String=""
    private var timeEndLong:Long=0
    private var mili:Long=0
    private lateinit var program: Program
    private var timeRun:String=""
    private var timeSum:Int=0
    private var timeSpray: Int=0
    private var time1:String=""
    private var username: String=""
    private lateinit var history: History
    private var histories:ArrayList<History> = arrayListOf()
    private var TAG="_HISTORY"
    private var socketReceive= DatagramSocket()
    private lateinit var savedata:SaveData
    private var spraySpeed:Int=0
    private var hourEnd:String=""
    private var hourStart:String=""
    private var error:Int=0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding= DataBindingUtil.inflate(
            inflater,
            R.layout.program_retail_fragment,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(ProgramRetailViewModel::class.java)
        port=8080
        ipAddress="192.168.4.1"
        timeCreate= ProgramRetailFragmentArgs.fromBundle(requireArguments()).timecreate
        program= ProgramRetailFragmentArgs.fromBundle(requireArguments()).program
        timeRun= ProgramRetailFragmentArgs.fromBundle(requireArguments()).timeSpeed
        timeSum= ProgramRetailFragmentArgs.fromBundle(requireArguments()).timeSum
        username=  ProgramRetailFragmentArgs.fromBundle(requireArguments()).username
        hourStart=ProgramRetailFragmentArgs.fromBundle(requireArguments()).hourStart
        spraySpeed=  ProgramRetailFragmentArgs.fromBundle(requireArguments()).speedSpray
        binding.tvTime.text = timeRun
        binding.lnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        savedata= SaveData(requireContext())
        savedata.setRoomSpraying(program.NameProgram)
        if (savedata.loadSpray()!=0){
            spraySpeed= savedata.loadSpray()
        }
        //Receive Data
        if (socketReceive.isClosed){
            socketReceive.bind(InetSocketAddress(8081))
            Log.d("_UDP", "receive open")
        }

        receiveData1()
        return  binding.root
    }

    private fun receiveData1(){
        var buffer = ByteArray(6566)
        object : Thread() {
            override fun run() {
                try {
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
                    Log.d("_UDP1", "Loi ${e.printStackTrace()}")
                    e.printStackTrace()
                }
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    fun display1(b: ByteArray) {
        activity?.runOnUiThread {

            //cap nhat phan tram hoa chat
            if ((b[0] == 0x03.toByte()) && (b[1] == 0x04.toByte()) && (b[6] == checkSum(
                    b
                ).toByte())
            ) {
                Log.d("_UDP1", "hoa chat data= ${b}")
                binding.progressBarHorizontal1.progress = (b[4].toInt())
                binding.tvTimePercent.text = "${b[4].toUInt()}%"
            }

            //Canh bao chuan bi phun hoa chat(20s) đếm ngược

            if ((b[0] == 0x03.toByte()) && (b[1] == 0x01.toByte()) && (b[6] == checkSum(b).toByte())) {
                // luu ten phong dang phun hoa chat
                savedata.setRoomSpraying(program.NameProgram)
                binding.btBack.visibility=View.GONE
                binding.textViewPrimary.text = b[5].toString()
                Log.d("_UDP1", "dem20s data= ${b}")
                if (b[5].toInt() != 1) {
                    binding.btStop.setOnClickListener {
                        confirmEnd()

                    }
                }

            }
            //bat dau dem nguoc <2^16s
            if ((b[0] == 0x03.toByte()) && (b[1] == 0x02.toByte()) && (b[6] == checkSum(
                    b
                ).toByte())
            ) {
                binding.btBack.visibility=View.GONE
                TransitionManager.beginDelayedTransition(binding.mainLayout)
                binding.tv1.visibility = View.GONE
                binding.ln3.visibility = View.GONE
                binding.lnWarning.visibility = View.VISIBLE
                Log.d("_UDP1", "bat dau data= ${b}")
                //truyen thoi gian dem nguoc
                val b5 = b[4] * 256
                val b6 = UnsignedBytes.toInt(b[5])
                val c = b5 + b6
                timeRun = c.toString()
                if (c == timeSum) {
                    binding.progressBarTime.progress = 100
                }
                if (c != timeSum) {
                    binding.progressBarTime.progress =
                        ((c.toDouble() / timeSum.toDouble()) * 100).toInt()
                }
                binding.progressBarTime.max = 100
                Log.d(TAG, "${binding.progressBarTime.progress}  $timeSum  $timeRun")
                binding.tvTime.text = convertSectoDay(timeRun.toInt())
                if (timeRun == 0.toString()) {
                    checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x00)
                    Log.d("_UDP", "send 030300000000")
                    //timeSpray = convertSectoDay(timeSum)
                    timeSpray= timeSum
                    // binding.progressBarTime.setProgress(0)
                    error = 0
                    saveHistory()
                    // binding.tvTime.setText(convertSectoDay(0))
                    notifyEnd()
                }
                if (timeRun != 0.toString()) {
                    //kiem tra qua nhiet, ket thuc phun hoa chat
                    checkTemp()
                    binding.btStop.setOnClickListener {
                        val builder =
                            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                        val positiveButtonClick = { _: DialogInterface, _: Int ->
                           // timeSpray = convertSectoDay(timeSum - timeRun.toInt())
                            timeSpray= timeSum- timeRun.toInt()
                            socketReceive.close()
                            //  binding.progressBarTime.setProgress(0)
                            //binding.tvTime.setText(convertSectoDay(0))
                            checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
                            error = 1
                            saveHistory()
                            notifyEnd()
                        }
                        val negativeButtonClick = { _: DialogInterface, _: Int ->
                        }
                        with(builder) {
                            setMessage("Dừng phun?")
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
            }

  
        }

    }
     private fun confirmEnd(){
         val builder =
             AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
         val positiveButtonClick = { _: DialogInterface, _: Int ->
             checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x00)
             requireActivity().onBackPressed()
         }
         val negativeButtonClick = { _: DialogInterface, _: Int ->
         }
         with(builder) {
             setMessage("Dừng phun?")
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
    private fun checkTemp(){
        //kiem tra qua nhiet, ket thuc phun hoa chat
        if ((savedata.loadTempSetting().isNotEmpty()) && (savedata.loadTemp().isNotEmpty())) {
            if (savedata.loadTemp().toInt() > savedata.loadTempSetting().toInt()) {
                checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x00)
               // socketReceive.close()
                //timeSpray = convertSectoDay(timeSum - timeRun.toInt())
                error=2
                // saveHistory()
                //notifyEnd()

            }
        }
    }
    private fun saveHistory(){
        savedata.setRoomSpraying("")
        val thetich= ProgramRetailFragmentArgs.fromBundle(requireArguments()).theTich
        val nongdo= ProgramRetailFragmentArgs.fromBundle(requireArguments()).nongdo
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        timeEndLong =convertDateToLong(sdf.format(Date()))
        val sdf1= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        hourEnd=sdf1.format(Date())

        history= History(
            TimeStart= timeCreate,
            CodeMachine= savedata.getCodeMachine(),
            Concentration= nongdo,
            Volume= thetich,
            TimeEnd= "${convertTimeLongToDate(timeEndLong)} ${hourEnd} ",
            Creator=username,
           Room= program.NameProgram,
            TimeRun= timeSpray,
            Error= error,
            SpeedSpray= spraySpeed,
            Status = 0
        )
        val historyFirebase= hashMapOf( "TimeCreate" to timeCreate,
            "CodeMachine" to savedata.getCodeMachine(),
            "Concentration" to nongdo,
            "Volume" to thetich,
            "TimeEnd" to "${convertTimeLongToDate(timeEndLong)} $hourEnd ",
            "Creator" to username,
            "Room" to program.NameProgram,
            "TimeRun" to timeSpray,
            "Error" to error,
            "SpeedSpray" to spraySpeed,
            "Status" to 0)
        viewModel.insert(history)
        sendInfo()
        val db = FirebaseFirestore.getInstance()
        db.collection("histories").add(historyFirebase)
            .addOnSuccessListener {
                histories.add(history)
                Log.d(TAG, "tải lên firebase thành công")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "có lỗi xảy ra", e)
            }
    }

    private fun convertSectoDay(time: Int):String {
        var n = time
        val hour = n / 3600
        n %= 3600
        val minutes = n / 60
        n %= 60
        val seconds = n
        time1 ="${hour}:${minutes}:${seconds}"
        if ((hour<10) && (hour!=0)&&(minutes<10) &&(seconds<10)){
            time1= "0${hour}:0${minutes}:0${seconds}"
        }else if ((hour<10) && (hour!=0)&&(minutes<10)&&(seconds>=10)){
            time1= "0${hour}:0${minutes}:${seconds}"
        }else if ((hour==0)&&(minutes<10) &&(seconds<10)){
            time1= "0${minutes}:0${seconds}"
        }else if ((hour==0)&&(minutes<10)&&(seconds>=10)){
            time1= "0${minutes}:${seconds}"
        }
        else if ((hour==0)&&(minutes>=10) &&(seconds>=10)){
            time1= "${minutes}:${seconds}"
        }

        return time1
    }

    private fun showNotify(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int -> }
        with(builder) {
            setMessage("Đang trong quá trình phun.")
            setPositiveButton(
                "Hủy",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
        }

        builder.show()

    }


    private fun sendInfo(){
        var a= byteArrayOfInts(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
        val b7= checkSum(a)
        a= byteArrayOfInts(0x03, 0x03, 0x00, 0x00, 0x00, 0x01, b7)
        sendUDP(a)
        Log.d("_UDP", "Ket thuc 030300000001")
    }

    private fun notifyEnd(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            requireActivity().onBackPressed()
        }
        with(builder) {
            setMessage("Hoàn tất")
            setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
        }
        builder.show()

    }

    private fun convertDateToLong(date: String): Long {
        try {
            val f: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val d = f.parse(date)
            mili = d!!.time
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return mili
    }


    private fun checkSum(b: ByteArray): Int {
        return b[0] + b[1] + b[2] + b[3] + b[4] + b[5]
    }

    private fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        var a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val b7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, b7)
        sendUDP(a)
    }


    private fun sendUDP(messageStr: ByteArray) {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
//Open a port to send the package
            val socket = DatagramSocket(8082)
            socket.broadcast = true
            val sendPacket = DatagramPacket(
                messageStr,
                messageStr.size,
                InetAddress.getByName(ipAddress),
                port
            )
            socket.send(sendPacket)
            if (!socket.isClosed){
                socket.close()
            }
        } catch (e: IOException) {
            Log.e("_UDP", "IOException: " + e.message)
        }
    }

    private fun convertTimeLongToDate(time:Long):String{
       return SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date(time))
    }



    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProgramRetailViewModel::class.java)
        // TODO: Use the ViewModel
    }



}
