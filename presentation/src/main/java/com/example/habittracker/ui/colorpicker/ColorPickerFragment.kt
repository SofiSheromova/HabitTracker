package com.example.habittracker.ui.colorpicker

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.GridView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.habittracker.R

class ColorPickerFragment : DialogFragment(), ColorPickerAdapter.OnItemClickListener {
    private lateinit var viewModel: ColorPickerViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ColorPickerViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val colors = resources.getIntArray(R.array.colorPickerColors).toList()
        val adapter = ColorPickerAdapter(requireActivity(), colors, this)

        return activity?.let {
            val gridView = createGridView(it)
            gridView.adapter = adapter

            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.color_picker_title))
                .setView(gridView)
                .setNegativeButton(getString(R.string.cancel), null)
            builder.create()
        }
            ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun createGridView(context: Context): GridView {
        return GridView(context).apply {
            numColumns = resources.getInteger(R.integer.colors_count_in_row)
            gravity = Gravity.CENTER
            columnWidth = GridView.AUTO_FIT
            val itemSpacing = resources.getDimension(R.dimen.color_picker_item_spacing).toInt()
            verticalSpacing = itemSpacing
            setPadding(itemSpacing, itemSpacing, itemSpacing, itemSpacing)
            stretchMode = GridView.STRETCH_SPACING_UNIFORM
        }
    }

    override fun onItemClicked(color: Int) {
        viewModel.setColor(color)
        dismiss()
    }
}