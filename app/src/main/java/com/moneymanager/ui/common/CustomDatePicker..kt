package com.moneymanager.ui.common

import android.content.Context
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class CustomDatePicker(private val context: Context, private val textView: TextView) {

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

    private val datePicker = MaterialDatePicker.Builder.datePicker()
        .setTitleText("日付を選択")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    init {
        // 日付が選択されたときの処理
        datePicker.addOnPositiveButtonClickListener { selection ->
            calendar.timeInMillis = selection
            textView.text = dateFormat.format(calendar.time)
        }
    }

    fun show() {
        datePicker.show((context as AppCompatActivity).supportFragmentManager, "datePicker")
    }

    fun getDate(): Date {
        return calendar.time
    }

    fun setDate(date: Date) {
        calendar.time = date
        textView.text = dateFormat.format(date)
    }
}