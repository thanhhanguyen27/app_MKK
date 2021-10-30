package com.cpc1hn.uimkk.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.cpc1hn.uimkk.R
import com.cpc1hn.uimkk.databinding.ItemHistoryBinding
import com.cpc1hn.uimkk.model.History

class IconHistoryAdapter (var historys: List<History>, private val onItemButtonClick: OnItemButtonClick): RecyclerView.Adapter<IconHistoryAdapter.ViewHolder>() {
    class ViewHolder(
        var binding: ItemHistoryBinding,
        private val onItemButtonClick: OnItemButtonClick,
    ): RecyclerView.ViewHolder(binding.root){
        fun bindView(history: History) {
            binding.history= history
            binding.tvTimeRun.text= "Thời gian phun: ${history.TimeRun}"
            binding.tvTime.text= "Ngày phun: ${history.TimeCreate}"
            binding.tvError.text= history.checkError()
            binding.root.setOnClickListener{
                onItemButtonClick.onItemClick(history)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        val binding= DataBindingUtil.inflate<ItemHistoryBinding>(layoutInflater, R.layout.item_history,parent, false)
        return IconHistoryAdapter.ViewHolder(binding, onItemButtonClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val history = historys[position]
        holder.bindView(history)
    }

    override fun getItemCount(): Int {
        return historys.size
    }
    fun setListData(data:List<History>){
        this.historys = data
        notifyDataSetChanged()
    }
    interface OnItemButtonClick{
        fun onItemClick(history:History)
    }


}