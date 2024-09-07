package com.moneymanager.adapters // adaptersパッケージに配置する

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.moneymanager.data.models.Category

class CategorySpinnerAdapter(context: Context, categories: List<Category>) :
    ArrayAdapter<Category>(context, android.R.layout.simple_spinner_item, categories) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent) as TextView
        view.text = getItem(position)?.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent) as TextView
        view.text = getItem(position)?.name
        return view
    }
}