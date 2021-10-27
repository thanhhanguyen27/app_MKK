package com.cpc1hn.uimkk.Adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.cpc1hn.uimkk.model.IconProgram
import com.cpc1hn.uimkk.R

class IconMainAdapter(var context: Context, var icons: List<IconProgram>): BaseAdapter() {
    override fun getCount(): Int {
        return icons.size
    }

    override fun getItem(position: Int): Any {
        return icons.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context,R.layout.item_main, null)
        val icon: ImageView = view.findViewById(R.id.icon)
        val name: TextView = view.findViewById(R.id.textView)

        val programItem: IconProgram = icons.get(position)
        icon.setImageResource(programItem.icon)
        name.text= programItem.textName

        return view

    }


}