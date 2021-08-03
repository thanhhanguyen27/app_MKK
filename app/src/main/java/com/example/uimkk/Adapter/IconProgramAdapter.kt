package com.example.uimkk.Adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.uimkk.R
import com.example.uimkk.databinding.ItemProgramBinding
import com.example.uimkk.model.Program

class IconProgramAdapter( var programs: ArrayList<Program>, private val onItemButtonClick: OnItemButtonClick): RecyclerView.Adapter<IconProgramAdapter.ViewHolder>() {
    class ViewHolder(
        var binding: ItemProgramBinding,
        private val onItemButtonClick: OnItemButtonClick,
    ): RecyclerView.ViewHolder(binding.root){
        fun bindView(program: Program) {
            binding.program = program
            binding.timeCreatee.text= "Người tạo: ${program.creater}"
            binding.root.setOnClickListener{
                onItemButtonClick.onItemClick(program)
            }
            binding.btDelete.setOnClickListener {
                onItemButtonClick.delete(program)
            }
            binding.btSee.setOnClickListener {
                onItemButtonClick.seeMore(program)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater= LayoutInflater.from(parent.context)
        val binding= DataBindingUtil.inflate<ItemProgramBinding>(layoutInflater, R.layout.item_program,parent, false)
        return IconProgramAdapter.ViewHolder(binding, onItemButtonClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val program = programs[position]
        holder.bindView(program)
    }

    override fun getItemCount(): Int {
        return programs.size
    }

    fun remove(program:Program) {
        programs.remove(program)
    }

    interface OnItemButtonClick{
        fun onItemClick(program:Program)
        fun delete(program: Program)
        fun seeMore(program: Program)
    }



}