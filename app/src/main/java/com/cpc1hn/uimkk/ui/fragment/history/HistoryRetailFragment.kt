package com.cpc1hn.uimkk.ui.fragment.history

import android.annotation.SuppressLint
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.convertSectoDay
import com.cpc1hn.uimkk.databinding.HistoryRetailFragmentBinding
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.modifyDateLayout
import com.cpc1hn.uimkk.ui.viewmodel.history.HistoryRetailViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class HistoryRetailFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryRetailFragment()
    }

    private lateinit var viewModel: HistoryRetailViewModel
    private lateinit var binding: HistoryRetailFragmentBinding
    private lateinit var history: History

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.history_retail_fragment, container, false)
        val toolbar= binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(this).get(HistoryRetailViewModel::class.java)
        // sendUDP(binding.tvCreater.text.toString())
        history= HistoryRetailFragmentArgs.fromBundle(requireArguments()).history
       // val time= longToDate(history.timeEndLong, "dd/MM/yyyy")
        Log.d("_HIS", "${history.Creator} ${history.TimeEnd}")
        binding.history= history
        binding.tvTimeEnd.text= modifyDateLayout(history.TimeEnd)
        binding.tvTimeCreate.text= modifyDateLayout(history.TimeStart)
        binding.tvError.text= history.checkError()
        binding.tvTimeRun.text= convertSectoDay(history.TimeRun)

        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryRetailViewModel::class.java)
        // TODO: Use the ViewModel
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_delete, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.Delete -> {
                showNotify()
                true
            } else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun showNotify(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            deleteHistory()
        }
        val negativeButtonClick = { _: DialogInterface, _: Int ->
        }
        with(builder) {
            setMessage("Xóa bản ghi này?")
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

    private fun deleteHistory(){
        val db = FirebaseFirestore.getInstance()
        db.collection("history").whereEqualTo("TimeCreate", history.TimeStart).get().addOnSuccessListener {
            for (document in it){
                db.collection("history").document(document.id).delete()
                viewModel.deleteHistoryInfo()
                requireActivity().onBackPressed()
            }
        }
    }
}