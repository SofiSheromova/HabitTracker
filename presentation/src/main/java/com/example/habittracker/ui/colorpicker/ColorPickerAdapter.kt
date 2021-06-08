package com.example.habittracker.ui.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.habittracker.R


class ColorPickerAdapter(
    context: Context,
    private val values: List<Int>,
    private val onItemClickListener: OnItemClickListener
) : ArrayAdapter<Int>(context, -1, values) {

    interface OnItemClickListener {
        fun onItemClicked(color: Int)
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view: View = inflater.inflate(R.layout.color_picker_item_view, parent, false)
        val imageView: ImageView = view.findViewById<View>(R.id.color) as ImageView
        imageView.setColorFilter(values[position])
        view.setOnClickListener { onItemClickListener.onItemClicked(values[position]) }
        return view
    }
}
