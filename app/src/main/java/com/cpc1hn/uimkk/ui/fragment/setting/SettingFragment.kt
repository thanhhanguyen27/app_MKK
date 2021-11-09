package com.cpc1hn.uimkk.ui.fragment.setting


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.cpc1hn.uimkk.*
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.SettingFragmentBinding
import com.cpc1hn.uimkk.ui.viewmodel.SettingViewModel
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress

class SettingFragment : Fragment() {

    companion object {
        fun newInstance() = SettingFragment()
    }

    private lateinit var viewModel: SettingViewModel
    private lateinit var binding: SettingFragmentBinding
    private lateinit var a: ByteArray
    private lateinit var ipAddress: String
    private var port: Int = 0
    private var B3 = 0x00
    private var B4 = 0x00
    private var B5 = 0x00
    private lateinit var radioGroup: RadioGroup
    private lateinit var saveData: SaveData
    private val TAG = "_SETTING"
    private var socketReceive = DatagramSocket()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.show()
        binding = DataBindingUtil.inflate(inflater, R.layout.setting_fragment, container, false)

        //loadLanguageState()
        saveData = SaveData(requireContext())
        val Lang = saveData.loadLanguageState()
        if (Lang == "vi") {
            binding.btVietnamese.isChecked = true
        } else if (Lang == "en") {
            binding.btEnglish.isChecked = true
        }
        if (saveData.loadTempSetting().isNotEmpty()){
            binding.tvTemp.setText(saveData.loadTempSetting())
        }
        ipAddress = "192.168.4.1"
        port = 8080
        radioGroup = binding.radioGroup

        viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
        Log.d(TAG, "Setting")

        if (saveData.loadSpray() != 0){
            binding.tvSpeedNow.text= "hiện tại ${saveData.loadSpray()}ml/p"
        }else{
            binding.tvSpeedNow.text= "30ml/phút"
        }
//
//        viewModel.getAllSet()
//        viewModel.getAllChecksObserves().observe(viewLifecycleOwner,{
//            if (it!=null) {
//                setClass = it
//                fillSettingInfo(it)
//            }
//        })
//        viewModel.getUser()
//        viewModel.getAllChecksObservesUser().observe(viewLifecycleOwner, {
//            if (it != null) {
//                binding.tvName.setText(it.name)
//                binding.tvMail.setText(it.email)
//            }
//        })



        getData()
        //Lay du lieu len may
        receiveData1()

        binding.apply {
            lnActive.setOnClickListener { findNavController().navigate(SettingFragmentDirections.actionNavSettingToActivateFragment()) }
            lnScale.setOnClickListener { findNavController().navigate(SettingFragmentDirections.actionNavSettingToScaleFragment()) }

//set speed
            lnSpeed.setOnClickListener (object : View.OnClickListener {
                override fun onClick(v: View?) {
                    TransitionManager.beginDelayedTransition(transition)
                    if (lnSetSpeed.visibility== View.GONE){
                        lnSetSpeed.visibility= View.VISIBLE
                        tvLineSpeed.visibility= View.GONE
                        tvMoreSpeed.animate().rotation(180f).start()
                        btSaveSpeed.setOnClickListener {
                            if (edtSpeed.text.isNotEmpty()){
                                if (( edtSpeed.text.toString().toInt() >=30) && ( edtSpeed.text.toString().toInt()<=37)) {
                                   // binding.edtSpeed.text = edtSpeed.text
                                    checkOn(0x02, 0x06, 0x00, 0x00, 0x00, edtSpeed.text.toString().toInt())
                                    Log.d("_UDP", "speed: ${edtSpeed.text.toString().toInt()}")
                                    hideKeyboard()
                                    Toast.makeText(context,"Đã lưu tốc độ phun", Toast.LENGTH_SHORT).show()
                                }
                                if ( (edtSpeed.text.toString().toInt()<30) or (edtSpeed.text.toString().toInt()>37)){
                                    hideKeyboard()
                                    warningSpeed()
                                }
                            }else{
                                hideKeyboard()
                                val builder = AlertDialog.Builder(
                                    requireContext(),
                                    R.style.AlertDialogTheme
                                )
                                val positiveButtonClick = { _: DialogInterface, _: Int ->
                                }
                                with(builder) {
                                    setMessage("Bạn chưa nhập tốc độ phun")
                                    setPositiveButton(
                                        "OK",
                                        DialogInterface.OnClickListener(function = positiveButtonClick)
                                    )
                                }
                                builder.show()
                            }
                        }
                    }else{
                        lnSetSpeed.visibility = View.GONE
                        tvLineSpeed.visibility = View.VISIBLE
                        tvMoreSpeed.animate().rotation(0f).start()
                    }
                }

            })
//set Temp
            lnTemp.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    TransitionManager.beginDelayedTransition(transition)
                    if (lnSetTemp.visibility== View.GONE) {
                        lnSetTemp.visibility = View.VISIBLE
                        tv3.visibility = View.GONE
                        moreSetTemp.animate().rotation(180f).start()

                        btSave.setOnClickListener {
                            if (binding.tvTemp.text.isNotEmpty()) {
                                if ((binding.tvTemp.text.toString()
                                        .toInt() > 100) or (binding.tvTemp.text.toString()
                                        .toInt() < 0)
                                ) {
                                    hideKeyboard()
                                    val builder = AlertDialog.Builder(
                                        requireContext(),
                                        R.style.AlertDialogTheme
                                    )
                                    val positiveButtonClick =
                                        { _: DialogInterface, _: Int ->

                                        }
                                    with(builder) {
                                        setMessage("Nhiệt độ giới hạn từ 0-100°C. \nMời nhập lại")
                                        setPositiveButton(
                                            "OK",
                                            DialogInterface.OnClickListener(function = positiveButtonClick)
                                        )
                                    }
                                    builder.show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Đã cài mức nhiệt tới hạn",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    //  viewModel.update(setClass)
                                    checkOn(
                                        0x02,
                                        0x05,
                                        0x00,
                                        0x00,
                                        0x00,
                                        binding.tvTemp.text.toString().toInt()
                                    )
                                    saveData.setTempSetting(binding.tvTemp.text.toString())
                                }
                            } else {
                                hideKeyboard()
                                val builder = AlertDialog.Builder(
                                    requireContext(),

                                    R.style.AlertDialogTheme
                                )
                                val positiveButtonClick = { _: DialogInterface, _: Int ->
                                }
                                with(builder) {
                                    setMessage("Vui lòng cài đặt mức nhiệt!")
                                    setPositiveButton(
                                        "OK",
                                        DialogInterface.OnClickListener(function = positiveButtonClick)
                                    )
                                }
                                builder.show()
                            }
                        }
                    } else {
                        lnSetTemp.visibility = View.GONE
                        tv3.visibility = View.VISIBLE
                        moreSetTemp.animate().rotation(0f).start()
                    }
                }
            })

            return binding.root
        }
    }

    private fun warningSpeed(){
        val builder = AlertDialog.Builder(
            requireContext(),
            R.style.AlertDialogTheme
        )
        val positiveButtonClick = { _: DialogInterface, _: Int ->
        }
        with(builder) {
            setMessage("Tốc độ phun có giá trị từ 30-37 ml/phút")
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
        }
        builder.show()
    }

        private fun onRestartApp() {
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()

        }


       private fun onRadioButtonClick() {
            val pref = requireContext().getSharedPreferences("RADIOBUTTON", Context.MODE_PRIVATE)
            val editor = pref.edit()
            binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                if (checkedId == R.id.btVietnamese) {
                    dialogEnglish()
                    editor.putString(CHECK, checkedId.toString())
                    editor.apply()
                }
                if (checkedId == R.id.btEnglish) {
                    dialogVietnamese()
                    editor.putString(CHECK, checkedId.toString())
                    editor.apply()
                }
            }
        }

      private  fun dialogVietnamese() {
            val pref = requireContext().getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            val positiveButtonClick = { _: DialogInterface, _: Int ->
                saveData.setLocale("en")
                editor.putString(LANGUAGE, "en")
                editor.apply()
                onRestartApp()
            }
            with(builder) {
                setMessage("Đổi ngôn ngữ sang tiếng Anh? \nApp sẽ khởi động lại. ")
                setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener(function = positiveButtonClick)
                )
            }
            builder.setTitle(Html.fromHtml("Xác nhận"))
            builder.show()
        }

      private  fun dialogEnglish() {
            val pref = requireContext().getSharedPreferences(SHARE_NAME, Context.MODE_PRIVATE)
            val editor = pref.edit()
            val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            val positiveButtonClick = { _: DialogInterface, _: Int ->
                saveData.setLocale("vi")
                editor.putString(LANGUAGE, "vi")
                editor.apply()
                onRestartApp()
            }
            with(builder) {
                setMessage(
                    "Change the language to Vietnamses? \n" +
                            "The app will restart."
                )
                setPositiveButton(
                    "OK",
                    DialogInterface.OnClickListener(function = positiveButtonClick)
                )
            }
            builder.setTitle(Html.fromHtml("Confirm"))
            builder.show()
        }

//
//       private fun setLanguage() {
//            binding.lnLanguage.setOnClickListener(object : View.OnClickListener {
//                var visible: Boolean = false
//                override fun onClick(v: View?) {
//                    TransitionManager.beginDelayedTransition(binding.transition)
//                    visible = !visible
//                    if (visible) {
//                        binding.radioGroup.visibility = View.VISIBLE
//                        binding.moreLanguge.animate().rotation(180f).start()
//                        onRadioButtonClick()
//                    } else {
//                        binding.radioGroup.visibility = View.GONE
//                        binding.moreLanguge.animate().rotation(0f).start()
//                        onRadioButtonClick()
//                    }
//                }
//            })
//
//        }

      private  fun getData() {
            //get data LED, Scale, Buzzer, Fan
            //checkOn(0x02, 0x09, B3, B4, B5, 0x01)
            //get data Temp, Speed
            checkOn(0x02, 0x09, B3, B4, B5, 0x02)

        }


       private fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

    private fun checkSum(b: ByteArray): Int {
        return b[0] + b[1] + b[2] + b[3] + b[4] + b[5]
    }

        private fun checkOn(B1: Int, B2: Int, B3: Int, B4: Int, B5: Int, B6: Int) {
            a = byteArrayOfInts(B1, B2, B3, B4, B5, B6)
            val B7 = checkSum(a)
            a = byteArrayOfInts(B1, B2, B3, B4, B5, B6, B7)
            sendUDP(a)
        }


       private fun sendUDP(messageStr: ByteArray) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            try {
//Open a port to send the package
                val socket = DatagramSocket(8082)
                socket.reuseAddress = true
                socket.broadcast = true
                //socketReceive= DatagramSocket(port)
                val sendPacket = DatagramPacket(
                    messageStr,
                    messageStr.size,
                    InetAddress.getByName(ipAddress),
                    port
                )
                socket.send(sendPacket)
                Log.d("_UDP2", "$sendPacket")
                if (!socket.isClosed) {
                    socket.close()
                    Log.d("_UDP", "closed")
                }
            } catch (e: IOException) {
                Log.e("_UDP23", "IOException: " + e.message)
            }
        }

    private fun receiveData1() {
            var buffer = ByteArray(6566)
            object : Thread() {
                override fun run() {
                    try {
                        //val socketReceive = DatagramSocket(portNum)
                        socketReceive = DatagramSocket(null)
                        socketReceive.reuseAddress = true
                        socketReceive.broadcast = true
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
                //receive Temp, Speed
                if ((b[0] == 0x02.toByte()) && (b[1] == 0x09.toByte()) && (b[5] == 0x02.toByte()) && (b[6] == checkSum(
                        b
                    ).toByte())
                ) {
                    Log.d("_UDP", "Temp, Speed")
                    binding.tvTemp.setText(b[3].toInt().toString())
//                    if ((b[4].toInt() >= 30) && (b[4].toInt()) <= 37) {
//                        binding.tvSpeed1.setText(b[4].toInt().toString())
//                        saveData.setSpray(b[4].toInt().toString())
//                    }
                }
//                //Receive LED, Scale, Fan, Buzzer
//                if ((b[0] == 0x02.toByte()) && (b[1] == 0x09.toByte()) && (b[6] == checkSum(b).toByte())) {
//                    Log.d("_UDP", "LED, Scale, Fan, Buzzer")
//                    if (b[2] == 0x01.toByte()) {
//                        binding.btSwitchLED.isChecked = true
//                    } else if (b[2] == (0x00).toByte()) {
//                        binding.btSwitchLED.isChecked = false
//                    }
//                    if (b[3] == 0x01.toBScale.isChecked = true
//                    } else if yte()) {
////                        binding.btSwitch(b[3] == 0x00.toByte()) {
//                        binding.btSwitchScale.isChecked = false
//                    }
//                    if (b[4] == 0x01.toByte()) {
//                        binding.btSwitchBuzzer.isChecked = true
//                    } else if (b[4] == 0x00.toByte()) {
//                        binding.btSwitchBuzzer.isChecked = false
//                    }
//                    if (b[5] == 0x01.toByte()) {
//                        binding.btSwitchFan.isChecked = true
//                    } else if (b[5] == 0x00.toByte()) {
//                        binding.btSwitchFan.isChecked = false
//                    }
//                }
            }

       }



        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)
            viewModel = ViewModelProviders.of(this).get(SettingViewModel::class.java)
            // TODO: Use the ViewModel
        }

//    fun dialogScaleMax(){
//        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
//        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
//
//        }
//
//        with(builder) {
//            setMessage("Căn chỉnh Max thành công")
//            setPositiveButton(
//                "Ok",
//                DialogInterface.OnClickListener(function = positiveButtonClick)
//            )
//
//        }
//        builder.show()
//    }
//    fun dialogScaleZero(){
//        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
//        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
//
//        }
//
//        with(builder) {
//            setMessage("Căn chỉnh Zero thành công")
//            setPositiveButton(
//                "Ok",
//                DialogInterface.OnClickListener(function = positiveButtonClick)
//            )
//
//        }
//        builder.show()
//    }


}

