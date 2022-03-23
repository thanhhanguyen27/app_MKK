package com.cpc1hn.uimkk.ui.fragment.program


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.SaveData
import com.cpc1hn.uimkk.convertStringToDateHHMMSS
import com.cpc1hn.uimkk.databinding.ProgramRetailFragmentBinding
import com.cpc1hn.uimkk.dateToLong
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
    private lateinit var program: Program
    private var timeRun:String=""
    private var timeSum:Int=0
    private var timeSpray: Int=0
    private var username: String=""
    private  var history: History = History()
    private var histories:ArrayList<History> = arrayListOf()
    private var TAG="_HISTORY"
    private var socketReceive= DatagramSocket()
    private lateinit var savedata:SaveData
    private var spraySpeed:Int=0
    private var hourEnd:String=""
    private var hourStart:String=""
    private var error:Int=0
    val handler = Handler(Looper.getMainLooper())
    private var programContinue : History? = null
    private var timeKeyHistory = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        setupViewmodel()
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
        Log.d("_CHECK", "$timeCreate")
        program= ProgramRetailFragmentArgs.fromBundle(requireArguments()).program
        timeRun= ProgramRetailFragmentArgs.fromBundle(requireArguments()).timeSpeed
        timeSum= ProgramRetailFragmentArgs.fromBundle(requireArguments()).timeSum
        username=  ProgramRetailFragmentArgs.fromBundle(requireArguments()).username
        hourStart= ProgramRetailFragmentArgs.fromBundle(requireArguments()).hourStart
        spraySpeed=  ProgramRetailFragmentArgs.fromBundle(requireArguments()).speedSpray

        checkProgramSpraying()

        //lay du lieu hoa chat
        checkOn1(0x03, 0x04, 0x00, 0x00, 0x00, 0x00)

        binding.tvTime.text = timeRun
        binding.lnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
        check1s()
        savedata= SaveData(requireContext())
        savedata.setRoomSpraying(program.NameProgram)

        //Receive Data
        if (socketReceive.isClosed){
            socketReceive.bind(InetSocketAddress(8081))
        }

        receiveData1()
        return  binding.root
    }

    private fun checkProgramSpraying(){
        viewModel.getAllHistoryObserves().observe(viewLifecycleOwner,{ listProgram ->
            val listProgram = listProgram.filter { it.save }
            if (listProgram.isNotEmpty()){
                Log.d("_CHECKPROGRAM", "program not empty")
                programContinue = listProgram[0]
            }else{
                Log.d("_CHECKPROGRAM", "program empty")
            }
        })
    }

    private fun check1s(){
        val runnableCode = object : Runnable {
            override fun run() {
                checkOn(0x01, 0x0B, 0x00, 0x00, 0x00, 0x01)
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnableCode)
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
                Log.d("_CHECK", "hoa chat data")
                binding.progressBarHorizontal1.progress = (b[4].toInt())
                binding.tvTimePercent.text = "${b[4].toUInt()}%"
                savedata.setTemp(b[5].toUInt().toString())

            }

            //Canh bao chuan bi phun hoa chat(20s) đếm ngược

            if ((b[0] == 0x03.toByte()) && (b[1] == 0x01.toByte()) && (b[6] == checkSum(b).toByte())) {
                // luu ten phong dang phun hoa chat
                savedata.setRoomSpraying(program.NameProgram)
                binding.btBack.visibility=View.GONE
                binding.textViewPrimary.text = b[5].toString()
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
                //truyen thoi gian dem nguoc
                val b5 = b[4] * 256
                val b6 = UnsignedBytes.toInt(b[5])
                val c = b5 + b6
                timeRun = c.toString()

                binding.progressBarTime.max = 100
                binding.tvTime.text = convertSecToTime(timeRun.toInt())


                if (programContinue != null){
                    //checkOn1(0x03, 0x04, 0x00, 0x00, 0x00, 0x00)
                    binding.progressBarTime.progress =
                        ((c.toDouble() / programContinue!!.timeProgramOff.toDouble()) * 100).toInt()
                    Log.d("_CHECKPROGRAM", "program not null")
                }else{
//                    Log.d("_CHECKPROGRAM", "program null")
//                    if (c == timeSum) {
//                        binding.progressBarTime.progress = 100
//                    }
//                    if (c != timeSum) {
//                        binding.progressBarTime.progress =
//                            ((c.toDouble() / timeSum.toDouble()) * 100).toInt()
//                    }
                }

                if (timeRun == 0.toString()) {
                    checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
                    Log.d("_UDP", "send 030300000000")
                    timeSpray= timeSum
                    error = 0
                    saveHistory()
                    notifyEnd()
                }
                if (timeRun != 0.toString()) {
                    //kiem tra qua nhiet, ket thuc phun hoa chat
                    checkTemp()
                    binding.btStop.setOnClickListener {
                        val builder =
                            AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                        val positiveButtonClick = { _: DialogInterface, _: Int ->
                            timeSpray= timeSum- timeRun.toInt()
                            socketReceive.close()
                            checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
                            error = 1
                            saveHistory()
                            navigateToProgramFragment()
                            //notifyEnd()
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
            checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
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
                checkOn(0x03, 0x03, 0x00, 0x00, 0x00, 0x01)
                error=2
                saveHistory()
                navigateToProgramFragment()
            }
        }
    }
    private fun saveHistory(){
        val sdf1= SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        hourEnd=sdf1.format(Date())
        val thetich= ProgramRetailFragmentArgs.fromBundle(requireArguments()).theTich
        val nongdo= ProgramRetailFragmentArgs.fromBundle(requireArguments()).nongdo
//        if (programContinue != null){
//            history.TimeStart = programContinue!!.TimeStart
//            Log.d("_CHECKPROGRAM", "program not null 2")
//        }else{
//            history.TimeStart = hourEnd
//            Log.d("_CHECKPROGRAM", "program null")
//        }
        history= History(
            CodeMachine= savedata.getCodeMachine(),
            TimeStart = programContinue!!.TimeStart,
            Concentration= nongdo,
            Volume= thetich,
            TimeEnd= hourEnd,
            timeEndLong= dateToLong(history.TimeEnd, "yyyy/MM/dd HH:mm:ss"),
            Creator=username,
            Room= program.NameProgram,
            TimeRun= timeSpray,
            Error= error,
            TimeCreateProgram= program.TimeCreate,
            SpeedSpray= spraySpeed,
            Status = 0,
            save = false
        )
        Log.d("_CHECKPROGRAM", "end ${hourEnd} , ${history.TimeStart}, ${programContinue!!.TimeStart}")
        viewModel.update(history)
        sendInfo()
    }

    private fun convertSecToTime(seconds: Int):String {
        val h = seconds / 3600
        val m = seconds % 3600 / 60
        val s = seconds % 3600 % 60
        return if (h <= 0){
            String.format("%02d:%02d", m, s)
        }else{
            String.format("%02d:%02d:%02d", h, m, s)
        }
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
//            requireActivity().onBackPressed()
           navigateToProgramFragment()
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

    private fun navigateToProgramFragment(){
        findNavController().navigate(ProgramRetailFragmentDirections.actionProgramRetailFragmentToNavHome())
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

    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    private fun setupViewmodel(){
        viewModel = ViewModelProviders.of(this).get(ProgramRetailViewModel::class.java)
    }

    private fun checkOn1(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        var a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        sendUDP(a)
        Log.d("_CHECKPROGRAM", "lay hoa chat")
    }

    override fun onResume() {
        super.onResume()
        //connect
        check1s()
//        //lay du lieu hoa chat
//        checkOn1(0x03, 0x04, 0x00, 0x00, 0x00, 0x00)

        receiveData1()
    }

}

