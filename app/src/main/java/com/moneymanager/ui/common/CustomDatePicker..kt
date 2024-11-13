package com.moneymanager.ui.common

import android.content.Context
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CustomDatePicker(
    private val context: Context,
    private val textView: TextView,
    private val onDateSelected: (String) -> Unit, // コールバックの引数の型をStringに変更
) {

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
            val formattedDate = dateFormat.format(calendar.time)
            textView.text = formattedDate
            onDateSelected(formattedDate) // フォーマット済みの日付文字列を渡す
        }
    }

    fun show() {
        datePicker.show((context as AppCompatActivity).supportFragmentManager, "datePicker")
    }

    fun getDate(): Date {
        return calendar.time
    }

    fun setDate(dateString: String) {
        val parsedDate = try {
            // SimpleDateFormat を使用して Date オブジェクトに変換 (必要に応じてフォーマットを調整)
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
            inputFormat.parse(dateString)
        } catch (e: ParseException) {
            Log.e("CustomDatePicker", "Failed to parse date: $dateString", e)
            null
        }

        calendar.time = parsedDate ?: Date()

        // yyyy/MM/dd 形式で textView に設定
        textView.text = dateFormat.format(calendar.time)
    }
}