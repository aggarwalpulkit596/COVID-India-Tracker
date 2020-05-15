package com.example.tracker.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.tracker.R
import com.example.tracker.data.models.DistrictData
import kotlinx.android.synthetic.main.item_statelist.view.*


class DistrictAdapter(val list: List<DistrictData>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(parent.context)
            .inflate(R.layout.item_statelist, parent, false)
        val item = list[position]

        view.confirmedTv.apply {
            text = SpannableDelta(
                "${item.confirmed}\n ↑ ${item.delta.confirmed ?: "0"}",
                "#D32F2F",
                item.confirmed?.toString().length ?: 0
            )
        }
        view.activeTv.text = item.active.toString()

        view.recoveredTv.text = SpannableDelta(
            "${item.recovered}\n ↑ ${item.delta.recovered ?: "0"}",
            "#388E3C",
            item.recovered?.toString().length ?: 0
        )
        view.deceasedTv.text = SpannableDelta(
            "${item.deceased}\n ↑ ${item.delta.deceased ?: "0"}",
            "#FBC02D",
            item.deceased?.toString().length ?: 0
        )
        view.stateTv.text = item.district

        return view
    }

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount(): Int = list.size

}


