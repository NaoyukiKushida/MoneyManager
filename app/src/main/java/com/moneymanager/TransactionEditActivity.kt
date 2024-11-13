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
import androidx.lifecycle.lifecycleScope
import com.moneymanager.entity.TransactionEntity
import com.moneymanager.adapters.CategorySpinnerAdapter
import com.moneymanager.data.models.repository.TransactionData
import com.moneymanager.data.models.ui.TransactionEditModel
import com.moneymanager.data.models.ui.BalanceType
import com.moneymanager.data.models.ui.Category
import com.moneymanager.databinding.ActivityTransactionEditBinding
import com.moneymanager.repositories.CategoryRepository
import com.moneymanager.repositories.TransactionRepository
import com.moneymanager.ui.common.CustomDatePicker
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.launch
import java.util.UUID

class TransactionEditActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityTransactionEditBinding
    private val config = RealmConfiguration.create(schema = setOf(TransactionEntity::class))
    private val realm: Realm = Realm.open(config)

    // TransactionModelの初期化
    private var transactionModel = TransactionEditModel() // デフォルトコンストラクタを使用
    // CustomDatePickerの初期化
    private lateinit var datePicker: CustomDatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTransactionEditBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "登録", LeftButtonType.BACK)

        // DatePickerの初期化
        datePicker = CustomDatePicker(this, viewBinding.textViewDate) { selectedDate ->
            // transactionModel の date を更新
            transactionModel = transactionModel.copy(date = selectedDate)
        }

        // DatePicker に初期日付を設定
        transactionModel.date?.let { datePicker.setDate(it) }
        viewBinding.textViewDate.text = transactionModel.date

        // カテゴリスピナーの設定
        val categories = CategoryRepository.getCategories()
        val adapter = CategorySpinnerAdapter(this, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        viewBinding.spinnerCategory.adapter = adapter

        // カテゴリスピナーの初期選択位置を「食費」に設定
        val defaultCategoryPosition = categories.indexOfFirst { it.name == "食費" }
        viewBinding.spinnerCategory.setSelection(defaultCategoryPosition)

        // 編集モードの場合、データを表示
        val transactionId = intent.getStringExtra("transactionId")
        if (transactionId != null) {
            ToolbarUtils.setupToolbar(
                this,
                toolbarContainer,
                "編集",
                LeftButtonType.BACK,
                null,
                rightButtonDrawableId = R.drawable.ic_delete,
                onRightButtonClick = {
                    lifecycleScope.launch {
                        TransactionRepository.deleteTransactions(realm, transactionId)
                        finish()
                    }
                }
            )
            loadTransactionForEdit (transactionId)
            viewBinding.buttonRegister.text = getString(R.string.modify_button_text)
        }

        // 各種リスナーの設定
        setupListeners()

        // 登録ボタン
        viewBinding.buttonRegister.setOnClickListener {
            registerTransaction()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun setupListeners() {
        // 入出金タイプラジオボタン
        viewBinding.radioGroupType.setOnCheckedChangeListener { _, checkedId ->
            updateBalanceType(checkedId == R.id.radioButtonExpense)
        }

        // 入出金金額テキストフィールド
        viewBinding.editTextAmount.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
                val inputText = viewBinding.editTextAmount.text.toString()
                val amount = inputText.toIntOrNull() ?: 0
                viewBinding.editTextAmount.setText(amount.toString())
                viewBinding.editTextAmount.setSelection(amount.toString().length)

                // transactionModelのamountを更新
                transactionModel = transactionModel.copy(amount = amount)
            }
        }

        // カテゴリースピナー
        viewBinding.spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateCategory(position)
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
                updateContent(s.toString())
            }
        })
    }

    private fun updateBalanceType(isExpense: Boolean) {
        transactionModel = transactionModel.copy(
            balanceType = if (isExpense) BalanceType.EXPENSE else BalanceType.INCOME
        )
    }

    private fun updateCategory(position: Int) {
        val categories = CategoryRepository.getCategories()
        val selectedCategory = categories[position]
        transactionModel = transactionModel.copy(category = selectedCategory)
        viewBinding.selectedCategoryName.text = selectedCategory.name
    }

    private fun updateContent(content: String) {
        transactionModel = transactionModel.copy(content = content)
    }

    private fun registerTransaction() {
        lifecycleScope.launch {
            // 新規登録の場合、transactionId を UUID で生成
            val transactionId = transactionModel.transactionId.ifBlank { UUID.randomUUID().toString() }

            val transactionData = TransactionData(
                transactionId = transactionId, // 生成したIDまたは既存のIDを設定
                balanceType = if (transactionModel.balanceType == BalanceType.EXPENSE) 0 else 1,
                category = transactionModel.category.id,
                date = transactionModel.date,
                amount = transactionModel.amount,
                content = transactionModel.content
            )

            TransactionRepository.saveTransaction(realm, transactionData)
        }

        finish()
    }

    private fun loadTransactionForEdit(transactionId: String) {
        lifecycleScope.launch {
            // transactionId に対応する TransactionEntityのデータを取得
            val transactionEntity = realm.writeBlocking {
                this.query(TransactionEntity::class, "transactionId == $0", transactionId).first().find()
            }

            if (transactionEntity != null) {
                val transactionData = TransactionRepository.fromEntityToData(transactionEntity)
                transactionModel = TransactionEditModel(
                    transactionId = transactionData.transactionId,
                    balanceType = if (transactionData.balanceType == 0) BalanceType.EXPENSE else BalanceType.INCOME,
                    category = CategoryRepository.getCategories().find { it.id == transactionData.category } ?: Category(0, ""),
                    date = transactionData.date,
                    amount = transactionData.amount,
                    content = transactionData.content
                )

                // UIに値を設定
                updateUIWithTransactionModel()
            } else {
                // エラー処理: transactionIdに対応するデータが見つからない場合
            }
        }
    }

    private fun updateUIWithTransactionModel() {
        // transactionModelの値に基づいてUIを更新
        viewBinding.editTextAmount.setText(transactionModel.amount.toString())
        viewBinding.editTextContent.setText(transactionModel.content)
        viewBinding.textViewDate.text = (transactionModel.date)

        val categories = CategoryRepository.getCategories()
        val categoryPosition = categories.indexOfFirst { it.id == transactionModel.category.id }
        viewBinding.spinnerCategory.setSelection(categoryPosition)

        val balanceTypeId = if (transactionModel.balanceType == BalanceType.EXPENSE) R.id.radioButtonExpense else R.id.radioButtonIncome
        viewBinding.radioGroupType.check(balanceTypeId)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
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

}
