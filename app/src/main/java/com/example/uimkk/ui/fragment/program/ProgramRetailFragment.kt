package com.example.uimkk.ui.fragment.program


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
import androidx.navigation.fragment.findNavController
import com.example.uimkk.MainActivity
import com.example.uimkk.R
import com.example.uimkk.SaveData
import com.example.uimkk.databinding.ProgramRetailFragmentBinding
import com.example.uimkk.model.History
import com.example.uimkk.model.Program
import com.example.uimkk.ui.viewmodel.program.ProgramRetailViewModel
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
    private var timeEnd:Long=0
    private var mili:Long=0
    private lateinit var program: Program
    private var timeRun:String=""
    private var timeSum:Int=0
    private var timeSpray: String=""
    private var time1:String=""
    private var username: String=""
    private lateinit var history: History
    private var histories:ArrayList<History> = arrayListOf()
    private var TAG="_HISTORY"
    private var socketReceive= DatagramSocket()
    private lateinit var savedata:SaveData
    private var spraySpeed:String=""
    private var hourEnd:String=""
    private var hourStart:String=""
    private var numberOfRun:Int=0
    private var error:String=""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        numberOfRun= ProgramRetailFragmentArgs.fromBundle(requireArguments()).numberOfRun
        binding.tvTime.setText(timeRun)
        savedata= SaveData(requireContext())
        savedata.setRoomSpraying(program.name)
        if (savedata.loadSpray().isNotEmpty()){
            spraySpeed= savedata.loadSpray()
        }
        //Receive Data
        if (socketReceive.isClosed){
            socketReceive.bind(InetSocketAddress(8081))
            Log.d("_UDP", "receive open")
        }



        ReceiveData1(port)
//        if (socketReceive.isClosed){
//            socketReceive.bind(InetSocketAddress(8081))
//            Log.d("_UDP", "receive open")
//        }

        return  binding.root
    }

    private fun ReceiveData1(portNum: Int){
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

    fun display1(b: ByteArray) {
        getActivity()?.runOnUiThread(java.lang.Runnable {
            //cap nhat khi dang phun
//            if ((b[1]==0x02.toByte()) &&(b[0]==0x03.toByte())){
//                TransitionManager.beginDelayedTransition(binding.mainLayout)
//                val visible = true
//                if (visible) {
//                    binding.tv1.setVisibility(View.GONE)
//                    binding.ln3.setVisibility(View.GONE)
//                    binding.lnWarning.setVisibility(View.VISIBLE)
//                }
//            }


            //cap nhat phan tram hoa chat
            if ((b[0] == 0x03.toByte()) && (b[1] == 0x04.toByte()) && (b[6] == checkSum(
                    b
                ).toByte())
            ) {
                Log.d("_UDP1", "hoa chat data= ${b}")
                binding.progressBarHorizontal1.progress = (b[4].toInt())
                binding.tvTimePercent.setText("${b[4].toInt()}%")
            }

            //Canh bao chuan bi phun hoa chat(20s) đếm ngược

            if ((b[0] == 0x03.toByte()) && (b[1] == 0x01.toByte()) && (b[6] == checkSum(b).toByte())) {
                // luu ten phong dang phun hoa chat
                savedata.setRoomSpraying(program.name)
                binding.textViewPrimary.setText(b[5].toString())
                Log.d("_UDP1", "dem20s data= ${b}")
                if (b[5].toInt() != 1) {
                    binding.btStop.setOnClickListener {
                        showNotify()

                    }
                }

            }
            //bat dau dem nguoc <2^16s
            if ((b[0] == 0x03.toByte()) && (b[1] == 0x02.toByte()) && (b[6] == checkSum(
                    b
                ).toByte())
            ) {
                TransitionManager.beginDelayedTransition(binding.mainLayout)
                    val visible = true
                    if (visible) {
                        binding.tv1.setVisibility(View.GONE)
                        binding.ln3.setVisibility(View.GONE)
                        binding.lnWarning.setVisibility(View.VISIBLE)
                    }
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
                binding.tvTime.setText(convertSectoDay(timeRun.toInt()))
                if (timeRun == 0.toString()) {
                    checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
                    Log.d("_UDP", "send 030300000000")
                    timeSpray = convertSectoDay(timeSum)
                   // binding.progressBarTime.setProgress(0)
                    error="Không có lỗi"
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
                        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
                            timeSpray = convertSectoDay(timeSum - timeRun.toInt())
                            socketReceive.close()
                          //  binding.progressBarTime.setProgress(0)
                            //binding.tvTime.setText(convertSectoDay(0))
                            checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
                            error="Dừng đột ngột"
                            saveHistory()
                            notifyEnd()
                        }
                        val negativeButtonClick = { dialog: DialogInterface, which: Int ->
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

            //bao hieu ket thuc phun hoa chat(10s) dem nguoc

            //Ket thuc phun hoa chat
//            if ((b[0] == 0x03.toByte()) && (b[1] == 0x03.toByte()) && (b[5] == 0x00.toByte()) && (b[6] == checkSum(
//                    b
//                ).toByte())
//            ) {
//                Log.d("_UDP", "END")
//                saveHistory()
//                notifyEnd()
//            }
        })

    }
    fun checkTemp(){
        //kiem tra qua nhiet, ket thuc phun hoa chat
        if ((savedata.loadTempSetting().isNotEmpty()) && (savedata.loadTemp().isNotEmpty())) {
            if (savedata.loadTemp().toInt() > savedata.loadTempSetting().toInt()) {
                checkOn(0x03, 0x04, 0x00, 0x00, 0x00, 0x00)
               // socketReceive.close()
                //timeSpray = convertSectoDay(timeSum - timeRun.toInt())
                error="Dừng do quá nhiệt"
                // saveHistory()
                //notifyEnd()

            }
        }
    }
    fun saveHistory(){
        savedata.setRoomSpraying("")
        val id= (0..1000).random()
        val thetich= ProgramRetailFragmentArgs.fromBundle(requireArguments()).theTich
        val nongdo= ProgramRetailFragmentArgs.fromBundle(requireArguments()).nongdo
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        timeEnd =convertDateToLong(sdf.format(Date()))
        val sdf1= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        hourEnd=sdf1.format(Date())

        history= History(
            id,
            numberOfRun.toString(),
            nongdo,
            thetich,
            timeCreate,
            hourStart,
            timeEnd,
            hourEnd,
            username,
            "",
            program.name,
            timeSpray,
            error,
            spraySpeed,
            "Chưa đồng bộ"
        )
        viewModel.insert(history)
        sendInfo()
        val db = FirebaseFirestore.getInstance()
        db.collection("histories").document(history.id.toString()).set(history)
            .addOnSuccessListener {
                histories.add(history)
                Log.d(TAG, "tải lên firebase thành công")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "có lỗi xảy ra", e)
            }
    }

    fun convertSectoDay(time: Int):String {
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

    fun showNotify(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        }
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
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
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
            mili = d!!.getTime()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return mili
    }


    private fun checkSum(b: ByteArray):Int{
        val sum = b[0] +b[1]+ b[2]+ b[3] +b[4]+ b[5]
        return sum
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
            val sendData = messageStr
            val sendPacket = DatagramPacket(
                sendData,
                sendData.size,
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


    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ProgramRetailViewModel::class.java)
        // TODO: Use the ViewModel
    }



}
