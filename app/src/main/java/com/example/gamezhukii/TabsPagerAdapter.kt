package com.example.gamezhukii

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TabsPagerAdapter(
    private val layouts: List<Int>
) : RecyclerView.Adapter<TabsPagerAdapter.PageHolder>() {

    class PageHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return PageHolder(view)
    }

    override fun onBindViewHolder(holder: PageHolder, position: Int) = Unit

    override fun getItemCount(): Int = layouts.size

    override fun getItemViewType(position: Int): Int = layouts[position]
}
