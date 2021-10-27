package com.cpc1hn.uimkk.ui.fragment.program

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.cpc1hn.uimkk.*
import com.cpc1hn.uimkk.databinding.ProgramDependFragmentBinding
import com.cpc1hn.uimkk.model.Program
import com.cpc1hn.uimkk.ui.viewmodel.program.ProgramDependViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.text.SimpleDateFormat
import java.util.*


class ProgramDependFragment : Fragment() {

    companion object {
        fun newInstance() = ProgramDependFragment()
    }

    private lateinit var viewModel: ProgramDependViewModel
    private lateinit var binding: ProgramDependFragmentBinding
    private lateinit var program: Program
    private var timeSpeed: Int = 0
    private var thetich: String = ""
    private var nongdo: String = ""
    private var timeCreate = ""
    private var time1: String = ""
    private var username: String = ""
    private var speedSpray: String = "30"
    private var liqid: Float = 0.0f
    private lateinit var ipAddress: String
    private var port: Int = 0
    private var liquidlevel: Int = 0
    private var socketReceive = DatagramSocket()
    private lateinit var savedata: SaveData
    private var hourStart: String = ""
    private var numberOfRun: Int = 0
    private lateinit var checkArray: ByteArray
    private var scaleActive: Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.program_depend_fragment,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this).get(ProgramDependViewModel::class.java)
        program = ProgramDependFragmentArgs.fromBundle(requireArguments()).program
        username = ProgramDependFragmentArgs.fromBundle(requireArguments()).username
        ipAddress = "192.168.4.1"
        port = 8080
        savedata = SaveData(requireContext())
        savedata.setRoom(program.NameProgram)
        scaleActive = savedata.loadActiveScale()

        //lay du lieu hoa chat
        checkOn1(0x03, 0x04, 0x00, 0x00, 0x00, 0x00)

        //Nhan du lieu muc hoa chat
        checkArray = receiveData()

        savedata = SaveData(requireContext())
        if (savedata.loadSpray().isNotEmpty()) {
            speedSpray = savedata.loadSpray()
        }

        //  Toast.makeText(context, speedSpray, Toast.LENGTH_SHORT).show()
        binding.tvTheTich.setText(program.Volume.toString())
        binding.tvNongdo.setText(program.Concentration.toString())
        thetich = binding.tvTheTich.text.toString()
        nongdo = binding.tvNongdo.text.toString()
        //cai thoi gian uoc tinh
        estimateTime()
        binding.apply {
            //cai thoi gian uoc tinh
            setNongDo_TheTich()
            //setMucHoaChat()
            tvRun.setOnClickListener {
                showAlert()

            }
        }
        return binding.root
    }

    private fun receiveData(): ByteArray {
        var buffer = ByteArray(6566)
        object : Thread() {
            override fun run() {
                try {
                    socketReceive = DatagramSocket(null)
                    socketReceive.reuseAddress = true
                    socketReceive.broadcast = true
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
        return buffer
    }

    @SuppressLint("SetTextI18n")
    fun display1(buffer: ByteArray) {
        activity?.runOnUiThread {
            //cap nhat phan tram hoa chat
            if ((buffer[0] == 0x03.toByte()) && (buffer[1] == 0x04.toByte()) && (buffer[6] == checkSum(
                    buffer
                ).toByte())
            ) {
                TransitionManager.beginDelayedTransition(binding.linear)
                val visible = true
                if (visible) {
                    binding.tvTimePercent.text = "${buffer[4].toInt()}%"
                    binding.tvTimePercent.visibility = View.VISIBLE

                }


                binding.progressBarHorizontal.progress = (buffer[4].toInt())
                liquidlevel = buffer[4].toInt()
                //tinh muc hoa chat
                liqid = ((timeSpeed * speedSpray.toInt()) / 6000 + 1).toFloat()
                if (liqid > liquidlevel) {
                    binding.tvWarning.visibility = View.VISIBLE
                } else {
                        binding.tvWarning.visibility = View.GONE
                }
            }
        }
    }

    private fun estimateTime() {
        timeSpeed = ((thetich.toInt() * nongdo.toInt()) * 60 / speedSpray.toInt()) + 10
        val time: String = ConvertSectoDay(timeSpeed)
        binding.tvTimeEstimate.text = "Thời gian phun ước tính ${time} "
    }

    private fun setNongDo_TheTich() {
        binding.apply {
            room.text = program.NameProgram

            tvNongdo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                @SuppressLint("SetTextI18n")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    thetich = tvTheTich.text.toString()
                    nongdo = tvNongdo.text.toString()
                    if ((thetich.isNotEmpty()) && (nongdo.isNotEmpty())) {
                        timeSpeed =
                            ((thetich.toInt() * nongdo.toInt()) * 60 / speedSpray.toInt()) + 10
                        liqid = ((timeSpeed * speedSpray.toInt()) / 6000 + 1).toFloat()
                        if (liqid > liquidlevel) {
                            TransitionManager.beginDelayedTransition(binding.linear)
                            val visible = true
                            if (visible) {
                                binding.tvWarning.visibility = View.VISIBLE
                            }
                        } else {
                            TransitionManager.beginDelayedTransition(binding.linear)
                            val visible = true
                            if (visible) {
                                binding.tvWarning.visibility = View.GONE
                            }
                        }
                        val time: String = ConvertSectoDay(timeSpeed)
                        tvTimeEstimate.text = "Thời gian phun ước tính ${time} "
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            tvTheTich.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    thetich = tvTheTich.text.toString()
                    nongdo = tvNongdo.text.toString()
                    if ((thetich.isNotEmpty()) && (nongdo.isNotEmpty())) {
                        timeSpeed =
                            ((thetich.toInt() * nongdo.toInt()) * 60 / speedSpray.toInt()) + 10
                        liqid = ((timeSpeed * speedSpray.toInt()) / 6000 + 1).toFloat()
                        if (liqid > liquidlevel) {
                            TransitionManager.beginDelayedTransition(binding.linear)
                            val visible = true
                            if (visible) {
                                binding.tvWarning.visibility = View.VISIBLE
                            }
                        } else {
                            TransitionManager.beginDelayedTransition(binding.linear)
                            val visible = true
                            if (visible) {
                                binding.tvWarning.visibility = View.GONE
                            }
                        }
                        val time: String = ConvertSectoDay(timeSpeed)
                        tvTimeEstimate.text = "Thời gian phun ước tính ${time} "
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            if ((savedata.loadRoomSpraying().isNotEmpty())) {
                if (savedata.loadRoomSpraying() != savedata.loadRoom()) {
                    showNotiSpray()
                } else {
                    start()
                }
            }
            if (savedata.loadRoomSpraying().isEmpty()) {
                start()
            }
        }
        val negativeButtonClick = { _: DialogInterface, _: Int ->
        }
        with(builder) {
            setMessage("Chuẩn bị phun khử khuẩn. \nĐề nghị ra khỏi phòng!")
            setIcon(R.drawable.sos)
            setPositiveButton("Ok", DialogInterface.OnClickListener(positiveButtonClick))
            setNegativeButton("Hủy", DialogInterface.OnClickListener(negativeButtonClick))
        }
        builder.setTitle(Html.fromHtml("<font color='#b71c1c'>Cảnh báo</font>"))
        builder.show()
    }

    private fun start() {
        if (scaleActive) {
            if (( binding.tvWarning.visibility != View.VISIBLE) ){
                 next()
             }
            else {
                val builder1 =
                    AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
                val positiveButtonClick = { _: DialogInterface, _: Int ->

                }
                with(builder1) {
                    setMessage("Không đủ hóa chất")
                    setPositiveButton(
                        "Ok",
                        DialogInterface.OnClickListener(function = positiveButtonClick)
                    )

                }
                builder1.show()

            }
        }else{
            next()
        }

    }

    private fun next(){
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val sdf1 = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        timeCreate = sdf.format(Date())
        hourStart = sdf1.format(Date())
        numberOfRun += 1
        val b5 = (timeSpeed / 256)
        val b6 = (timeSpeed - b5 * 256)
        //truyen thoi gian phun
        if ((checkArray[1] != 0x02.toByte())) {

            checkOn(0x03, 0x02, 0x00, 0x00, b5, b6)
            Log.d("_UDP", "bat dau dem so 20s")
        }

        requireView().findNavController().navigate(
            ProgramDependFragmentDirections.actionProgramDependFragmentToProgramRetailFragment2(
                program = program,
                timecreate = timeCreate,
                timeSpeed = time1,
                hourStart = hourStart,
                timeSum = timeSpeed,
                theTich = binding.tvTheTich.text.toString(),
                nongdo = binding.tvNongdo.text.toString(),
                username = username,
                numberOfRun = numberOfRun,
                speedSpray = speedSpray
            )
        )
    }

    private fun showNotiSpray(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
        }
        with(builder) {
            setMessage("Đang phun tại ( ${savedata.loadRoomSpraying()} )")
            setPositiveButton(
                "Trở về",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
        }
        builder.show()
    }

   private fun ConvertSectoDay(time: Int):String {
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

    private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }




    private fun checkSum(b: ByteArray):Int{
        val sum = b[0] +b[1]+ b[2]+ b[3] +b[4]+ b[5]
        return sum
    }

    private fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        var a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        sendUDP(a)
        Log.d("_UDP", "bat dau dem so 20s")
    }
    private fun checkOn1(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int){
        var a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
        val B7 = checkSum(a)
        a=byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
        sendUDP(a)
        Log.d("_UDP", "lay hoa chat")
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
            //   Toast.makeText(context, "$sendData", Toast.LENGTH_SHORT).show()
            socket.send(sendPacket)
            Log.d("_UDP1", "$sendPacket")
            if (!socket.isClosed){
                socket.close()
            }
        } catch (e: IOException) {
            Log.e("_UDP1", "IOException: " + e.message)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Save -> {
                showDialogSave()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun showDialogSave(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            updateProgram()
        }
        val negativeButtonClick= { _: DialogInterface, _: Int ->
        }

        with(builder) {
            setMessage("Lưu chương trình?")
            setPositiveButton(
                "Lưu",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Hủy", DialogInterface.OnClickListener(negativeButtonClick))
        }
        builder.show()
    }

    private fun updateProgram(){
        val db = FirebaseFirestore.getInstance()
        db.collection("programs").document(program.id).update(mapOf(
            "thetich" to binding.tvTheTich.text.toString(),
            "nongdo" to binding.tvNongdo.text.toString()
        )).addOnSuccessListener {
            Toast.makeText(context, "Đã lưu chương trình", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { e ->
                Log.w("ADD", "Có lỗi xảy ra", e)

            }

    }

}
