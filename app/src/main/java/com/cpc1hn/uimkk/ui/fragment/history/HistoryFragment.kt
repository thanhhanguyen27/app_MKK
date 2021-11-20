package com.cpc1hn.uimkk.ui.fragment.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.cpc1hn.uimkk.Adapter.IconHistoryAdapter
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.HistoryFragmentBinding
import com.cpc1hn.uimkk.dateToLong
import com.cpc1hn.uimkk.model.History
import com.cpc1hn.uimkk.showDialogShort
import com.cpc1hn.uimkk.ui.viewmodel.HistoryViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.lang.Exception
import java.net.InetAddress
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class  HistoryFragment : Fragment(), IconHistoryAdapter.OnItemButtonClick {

    companion object {
        fun newInstance() = HistoryFragment()
    }

    private lateinit var viewModel: HistoryViewModel
    private lateinit var binding: HistoryFragmentBinding
    private lateinit var historyAdapter: IconHistoryAdapter
    private var histories: ArrayList<History> = arrayListOf()
    private var mili:Long=0


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater, R.layout.history_fragment, container, false)
        setHasOptionsMenu(true)
        val toolbar= binding.toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.show()

        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        viewModel.getAllHistory()

        viewModel.getAllHistoryObserves().observe(viewLifecycleOwner, {
            historyAdapter.setListData(ArrayList(it))
            histories= ArrayList(it)
        })
        historyAdapter = IconHistoryAdapter(histories, this)
        binding.historyRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(context)
        }


        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        binding.tvDateStart.text = currentDateandTime
        binding.tvDateEnd.text = currentDateandTime
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.tvDateStart.setOnClickListener {

            val dpd = DatePickerDialog(
                requireActivity(), { _, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    if ((dayOfMonth < 10) && (monthOfYear < 9)) {
                        binding.tvDateStart.text = "0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year
                    } else if ((dayOfMonth > 10) && (monthOfYear < 9)){
                        binding.tvDateStart.text = "0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year
                    }
                    else if (dayOfMonth < 10) {
                        binding.tvDateStart.text = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                    } else if (monthOfYear < 9) {
                        binding.tvDateStart.text = "" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year
                    } else
                        binding.tvDateStart.text = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                    historyFilter()
                },
                year,
                month,
                day
            )
            dpd.show()
        }
        binding.tvDateEnd.setOnClickListener {
            val dpd = DatePickerDialog(
                requireActivity(), { _, year, monthOfYear, dayOfMonth ->
                    if ((dayOfMonth < 10) && (monthOfYear < 9)) {
                        binding.tvDateEnd.text = "0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year
                    } else if ((dayOfMonth > 10) && (monthOfYear < 9)){
                        binding.tvDateEnd.text = "0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year
                    }
                    else if (dayOfMonth < 10) {
                        binding.tvDateEnd.text = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                    } else if (monthOfYear < 9) {
                        binding.tvDateEnd.text = "" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year
                    } else
                        binding.tvDateEnd.text = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year
                    historyFilter()
                },
                year,
                month,
                day
            )
            dpd.show()
        }


        return binding.root
    }

    private fun historyFilter(){
        val start= convertDateToLong("${binding.tvDateStart.text}")
        val end= convertDateToLong("${binding.tvDateEnd.text}")
        viewModel.getHisstoryFilter(start, end).observe(viewLifecycleOwner, { list ->
            list.let {
                if (list.isEmpty()){
                    var visible = false
                    TransitionManager.beginDelayedTransition(binding.lnMain)
                    visible= !visible
                    if (visible){
                        binding.noView.visibility = View.VISIBLE
                    }
                }else {
                    binding.noView.visibility = View.GONE
                }
                historyAdapter.setListData(it)
            }
        })

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
    private fun searchName(searchText: String) {
        val searchTextQuery = "%$searchText%"
        viewModel.getHisByName(searchTextQuery)
            .observe(viewLifecycleOwner, { list ->
                list.let {
                    historyAdapter.setListData(it)
                }

            })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.history_menu, menu)
        val searchItem = menu.findItem(R.id.Search)
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.queryHint = "Tìm kiếm"
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    searchName(query)
                } else {
                    historyAdapter.historys = histories
                    historyAdapter.notifyDataSetChanged()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText.isNotEmpty()) {
                    searchName(newText)
                } else {
                    historyAdapter.historys = histories
                    historyAdapter.notifyDataSetChanged()
                }
                return true
            }

        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.DongBo -> {
                showAlert()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    private fun showAlert(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { _: DialogInterface, _: Int ->
            checkOverall()
        }
        val negativeButtonClick= {_:DialogInterface, _: Int ->

        }
        with(builder) {
            setTitle("Đồng bộ dữ liệu?")
            setMessage("Lưu ý chỉ thực hiện đồng bộ khi có kết nối internet.")
            setPositiveButton(
                "Đồng bộ",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            ).setNegativeButton("Không", DialogInterface.OnClickListener(negativeButtonClick) )
        }
        builder.show()
    }

    private fun checkOverall(){
        if (isInternetAvailable()){
            viewModel.deleteHistoryInfo()
            getHistoryFirebase()
        }else{
            showDialogShort("Có lỗi", "Kiểm tra lại kết nối internet của bạn")
        }
    }


    private fun upHistorytoFirebase(history: History){
                val historyFirebase= hashMapOf( "TimeStart" to history.TimeStart,
            "CodeMachine" to history.CodeMachine,
            "Concentration" to history.Concentration,
            "Volume" to history.Volume,
            "TimeEnd" to history.TimeEnd,
            "Creator" to history.Creator,
            "Room" to history.Room,
            "TimeRun" to history.TimeRun,
            "Error" to history.Error,
            "SpeedSpray" to history.SpeedSpray,
                    "TimeProgram" to history.TimeCreateProgram,
            "Status" to 1)
                val db = FirebaseFirestore.getInstance()
        db.collection("histories").add(historyFirebase)
            .addOnSuccessListener {

            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Có lỗi xảy ra $e", Toast.LENGTH_SHORT).show()
            }
    }

    //check internet
    private fun isInternetAvailable(): Boolean {
        return try {
            val ipAddr: InetAddress = InetAddress.getByName("google.com")
            //You can replace it with your name
            !ipAddr.equals("")
        } catch (e: Exception) {
            false
        }
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

    private fun getHistoryFirebase(){

        val db = FirebaseFirestore.getInstance()
        db.collection("histories").get()
            .addOnSuccessListener { result ->
                histories= ArrayList(result.map {
                    it.toObject<History>()
                })
                for (history in histories){
                    history.timeEndLong = dateToLong(history.TimeEnd, "yyyy/MM/dd HH:mm:ss")
                }

                viewModel.insertAll(histories)
                Toast.makeText(context, "Đồng bộ thành công", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
    }


    override fun onItemClick(history: History) {
        requireView().findNavController().navigate(
            HistoryFragmentDirections.actionNavHistoryToHistoryRetailFragment(
                history
            )
        )
    }
}