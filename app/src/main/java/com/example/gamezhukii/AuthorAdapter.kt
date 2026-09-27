package com.example.gamezhukii

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

data class Author(val name: String, val photoResource: Int)

class AuthorAdapter(
    context: Context,
    private val authors: List<Author>
) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = authors.size

    override fun getItem(position: Int): Author = authors[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_author, parent, false)
        val author = getItem(position)
        view.findViewById<ImageView>(R.id.authorPhoto).setImageResource(author.photoResource)
        view.findViewById<TextView>(R.id.authorName).text = author.name
        return view
    }
}
