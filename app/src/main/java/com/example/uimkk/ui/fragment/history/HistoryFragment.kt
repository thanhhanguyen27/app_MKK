package com.example.uimkk.ui.fragment.history

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uimkk.Adapter.IconHistoryAdapter
import com.example.uimkk.MainActivity
import com.example.uimkk.R
import com.example.uimkk.databinding.HistoryFragmentBinding
import com.example.uimkk.model.History
import com.example.uimkk.ui.viewmodel.HistoryViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
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
    private var historiesFilter:  ArrayList<History> = arrayListOf()
    private var TAG="_GETHISTORY"
    private var mili:Long=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.show()
        binding= DataBindingUtil.inflate(inflater, R.layout.history_fragment, container, false)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        viewModel.getAllHistory()
        viewModel.getAllHistoryObserves().observe(viewLifecycleOwner, {
            historyAdapter.setListData(ArrayList(it))
        })
        historyAdapter = IconHistoryAdapter(histories, this)
        binding.historyRecyclerView.apply {
            adapter = historyAdapter
            layoutManager = LinearLayoutManager(context)
        }


        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        binding.tvDateStart.setText(currentDateandTime)
        binding.tvDateEnd.setText(currentDateandTime)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        binding.tvDateStart.setOnClickListener {

            val dpd = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    if ((dayOfMonth < 10) && (monthOfYear < 10)) {
                        binding.tvDateStart.setText("0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year)
                    } else if (dayOfMonth < 10) {
                        binding.tvDateStart.setText("0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                    } else if (monthOfYear < 10) {
                        binding.tvDateStart.setText("" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year)
                    } else
                        binding.tvDateStart.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
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
                requireActivity(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    if ((dayOfMonth < 10) && (monthOfYear < 10)) {
                        binding.tvDateEnd.setText("0" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year)
                    } else if (dayOfMonth < 10) {
                        binding.tvDateEnd.setText("0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                    } else if (monthOfYear < 10) {
                        binding.tvDateEnd.setText("" + dayOfMonth + "/0" + (monthOfYear + 1) + "/" + year)
                    } else
                        binding.tvDateEnd.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
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

    fun historyFilter(){
        val start= convertDateToLong("${binding.tvDateStart.text}")
        val end= convertDateToLong("${binding.tvDateEnd.text}")
        viewModel.getHisstoryFilter(start, end).observe(viewLifecycleOwner, { list ->
            list.let {
                if (list.isEmpty()){
                    var visible = false
                    TransitionManager.beginDelayedTransition(binding.lnMain)
                    visible= !visible
                    if (visible){
                        binding.noView.setVisibility(View.VISIBLE)
                    }
                }else {
                    binding.noView.setVisibility(View.GONE)
                }
                historyAdapter.setListData(it)
            }
            Log.d(TAG, "${list}")
        })

    }

    fun convertDateToLong(date: String): Long {
        try {
            val f: DateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val d = f.parse(date)
            mili = d!!.getTime()
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
        searchView.setQueryHint("Tìm kiếm")
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


    fun showAlert(){
        val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
        val positiveButtonClick = { dialog: DialogInterface, which: Int ->
            viewModel.deleteHistoryInfo()
            getHistoryFirebase()
            viewModel.insertAll(getHistoryFirebase())
        }
        with(builder) {
            setMessage("Đồng bộ dữ liệu")
            setPositiveButton(
                "OK",
                DialogInterface.OnClickListener(function = positiveButtonClick)
            )
        }
        builder.show()
    }

    fun getHistoryFirebase():ArrayList<History>{
        val db = FirebaseFirestore.getInstance()
        db.collection("histories").get()
            .addOnSuccessListener { result ->
                histories= ArrayList(result.map {
                    it.toObject<History>()
                })
                Log.d(TAG, histories.toString())
                historyAdapter.historys = histories

                for (i in 0..histories.size-1){
                    histories[i].status=""
                }
                historyAdapter.historys = histories
               // historyAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
        return histories
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        // TODO: Use the ViewModel
    }


    override fun onItemClick(history: History) {
        requireView().findNavController().navigate(
            HistoryFragmentDirections.actionHistoryFragmentToHistoryRetailFragment(
                history
            )
        )
    }

}