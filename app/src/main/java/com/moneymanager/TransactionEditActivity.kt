package com.moneymanager

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.moneymanager.adapters.CategorySpinnerAdapter
import com.moneymanager.data.models.TransactionEditModel
import com.moneymanager.data.models.TransactionType
import com.moneymanager.databinding.ActivityTransactionEditBinding
import com.moneymanager.repositories.CategoryRepository
import com.moneymanager.ui.common.CustomDatePicker
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

class TransactionEditActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTransactionEditBinding

    // TransactionModelの初期化
    private var transactionModel = TransactionEditModel() // デフォルトコンストラクタを使用

    private lateinit var datePicker: CustomDatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTransactionEditBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "登録・編集", LeftButtonType.BACK)

        // DatePickerの初期化
        datePicker = CustomDatePicker(this, viewBinding.textViewDate)

        // DatePicker に初期日付を設定
        datePicker.setDate(transactionModel.date)

        // カテゴリスピナーの設定
        val categories = CategoryRepository.getCategories()
        val adapter = CategorySpinnerAdapter(this, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.spinnerCategory.adapter = adapter

        // カテゴリスピナーの初期選択位置を「食費」に設定
        val defaultCategoryPosition = categories.indexOfFirst { it.name == "食費" }
        viewBinding.spinnerCategory.setSelection(defaultCategoryPosition)

        // 編集モードの場合、データを表示
//        val transactionId = intent.getIntExtra("transactionId", 0)
//
//        if (transactionId != 0) {
//            // 編集モード
//            val transaction = getTransactionById(transactionId) // データベースなどからデータを取得
//            // transaction を TransactionEditModel に変換して UI に表示
//        } else {
//            // 新規登録モード
//            // TransactionEditModel のデフォルト値を UI に表示
//        }


        // 各種リスナーの設定
        // 入出金タイプラジオボタン
        viewBinding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            transactionModel = transactionModel.copy(
                type = if (checkedId == R.id.radioButtonExpense) TransactionType.EXPENSE else TransactionType.INCOME
            )
        }

        // 入出金金額テキストフィールド
        viewBinding.editTextAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                val inputText = viewBinding.editTextAmount.text.toString()
                val amount = inputText.toIntOrNull() ?: 0
                viewBinding.editTextAmount.setText(amount.toString())
                viewBinding.editTextAmount.setSelection(amount.toString().length)

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
        }

        // カテゴリースピナー
        viewBinding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                transactionModel = transactionModel.copy(category = selectedCategory)
                viewBinding.selectedCategoryName.text = selectedCategory.name
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 何もしない
            }
        }

        // 日付ピッカー
        viewBinding.textViewDate.setOnClickListener {
            datePicker.show()
        }

        // 内容テキストフィールド
        viewBinding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                transactionModel = transactionModel.copy(content = s.toString())
            }
        })

        // 登録ボタン
        viewBinding.buttonRegister.setOnClickListener {
            // データを保存して終了
            // データベースなどに保存する処理を追加

            finish()
        }
    }


        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            if (ev?.action == MotionEvent.ACTION_DOWN) {
                val v = currentFocus
                if (v is EditText) {
                    val outRect = Rect()
                    v.getGlobalVisibleRect(outRect)
                    if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                        v.clearFocus()
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
            }
            return super.dispatchTouchEvent(ev)
        }

//    private fun getTransactionById(transactionId: Int): Transaction? {
        // データベースなどから transactionId に対応する Transaction を取得
        // ここでは仮の実装
//        return transactionList.find { it.transactionId == transactionId }
//    }
}
