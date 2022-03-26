package com.cpc1hn.uimkk.ui.fragment.setting

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.ScaleFragmentBinding
import com.cpc1hn.uimkk.showDialogShort
import com.cpc1hn.uimkk.ui.viewmodel.setting.ScaleViewModel
import java.io.IOException
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class ScaleFragment : Fragment() {

    companion object {
        fun newInstance() = ScaleFragment()
    }

    private lateinit var viewModel: ScaleViewModel
    private lateinit var binding: ScaleFragmentBinding
    private lateinit var ipAddress:String
    private var port: Int=0
    private lateinit var a:ByteArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.scale_fragment, container, false)
        val toolbar= binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ipAddress= "192.168.4.1"
        port=8080
        binding.apply {
            btZero.setOnClickListener {
                if (checkConnectivity(requireContext()) and isInternetAvailable()){
                    showDialogShort("","Chưa kết nối với máy phun.")
                }else if (!checkConnectivity(requireContext()) and !isInternetAvailable()){
                    showDialogShort("","Chưa kết nối với máy phun.")
                }else{
                    confirmScaleZero()
                }
            }

            btMax.setOnClickListener {
                if (checkConnectivity(requireContext()) and isInternetAvailable()){
                    showDialogShort("","Chưa kết nối với máy phun.")
                }else if (!checkConnectivity(requireContext()) and !isInternetAvailable()){
                    showDialogShort("","Chưa kết nối với máy phun.")
                }else{
                    confirmScaleMax()
                }
            }
        }


        return binding.root
    }
    fun checkConnectivity(context: Context): Boolean {

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo

        if(activeNetwork?.isConnected!=null){
            return activeNetwork.isConnected
        }
        else{
            return false
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
    }

    private fun confirmScaleZero(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            checkOn(0x02, 0x08, 0x00, 0x00, 0x00, 0x01)
            dialogScaleZero()
        }

        val negativeButtonClick= {_:DialogInterface, _: Int ->

        }
        with(builder) {
            setMessage("Bạn muốn căn chỉnh cân Zero?")
            setPositiveButton(
                "Có",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            ).setNegativeButton("Không", DialogInterface.OnClickListener(negativeButtonClick) )
        }
        builder.show()
    }

    private fun confirmScaleMax(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            checkOn(0x02, 0x08, 0x00, 0x00, 0x00, 0x02)
            dialogScaleMax()
        }
        val negativeButtonClick= {_:DialogInterface, _: Int ->

        }
        with(builder) {
            setMessage("Bạn muốn căn chỉnh cân Max?")
            setPositiveButton(
                "Có",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
            setNegativeButton("Không", DialogInterface.OnClickListener(negativeButtonClick) )

        }
        builder.show()
    }
    private fun dialogScaleMax(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            binding.btMax.setCheckMarkDrawable(R.drawable.ic_check_on)
        }

        with(builder) {
            setMessage("Căn chỉnh Max thành công")
            setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )

        }
        builder.show()
    }
    private fun dialogScaleZero(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            binding.btZero.setCheckMarkDrawable(R.drawable.ic_check_on)
        }

        with(builder) {
            setMessage("Căn chỉnh Zero thành công")
            setPositiveButton(
                "Ok",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )

        }
        builder.show()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ScaleViewModel::class.java)
        // TODO: Use the ViewModel
    }

}