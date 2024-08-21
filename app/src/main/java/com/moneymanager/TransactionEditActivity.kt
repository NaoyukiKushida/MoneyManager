package com.moneymanager

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.SurfaceControl
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.moneymanager.databinding.ActivityTransactionEditBinding
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

class TransactionEditActivity : AppCompatActivity() {

    private lateinit var dateEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var amountEditText: EditText

    private lateinit var viewBinding: ActivityTransactionEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityTransactionEditBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "登録・編集", LeftButtonType.BACK)

//        dateEditText = findViewById(R.id.date_edit_text)
//        categoryEditText = findViewById(R.id.category_edit_text)
//        amountEditText = findViewById(R.id.amount_edit_text)

        // 編集モードの場合、データを表示
        val transaction = intent.getParcelableExtra<SurfaceControl.Transaction>("transaction")
        if (transaction != null) {
//            dateEditText.setText(transaction.date)
//            categoryEditText.setText(transaction.category)
//            amountEditText.setText(transaction.amount.toString())
        }

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

        viewBinding.buttonRegister.setOnClickListener {
            // データを保存して終了
            val newTransaction = Transaction(
                dateEditText.text.toString(),
                categoryEditText.text.toString(),
                amountEditText.text.toString().toIntOrNull() ?: 0
            )
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
}
