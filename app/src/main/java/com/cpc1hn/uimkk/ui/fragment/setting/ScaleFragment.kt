package com.cpc1hn.uimkk.ui.fragment.setting

import android.content.DialogInterface
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
import com.cpc1hn.uimkk.ui.viewmodel.setting.ScaleViewModel
import java.io.IOException
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
                checkOn(0x02, 0x08, 0x00, 0x00, 0x00, 0x01)
                dialogScaleZero()
            }

            btMax.setOnClickListener {
                checkOn(0x02, 0x08, 0x00, 0x00, 0x00, 0x02)
                dialogScaleMax()
            }
        }


        return binding.root
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