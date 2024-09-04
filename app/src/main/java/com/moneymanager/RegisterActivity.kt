package com.moneymanager

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.moneymanager.databinding.ActivityRegisterBinding
import com.moneymanager.ui.common.CustomDatePicker
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

/**
 * 新規ユーザー登録画面
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityRegisterBinding

    private var emailAddressText: String = ""
    private var passwordText: String = ""

    private val auth = Firebase.auth
    private lateinit var datePicker: CustomDatePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "新規ユーザー登録", LeftButtonType.BACK)

        // DatePickerの初期化
        datePicker = CustomDatePicker(this, viewBinding.textViewDate)

        // 日付ピッカー
        viewBinding.textViewDate.setOnClickListener {
            datePicker.show()
        }

        val checkBoxTerms: CheckBox = findViewById(R.id.checkBoxTerms)
        checkBoxTerms.buttonTintList = ContextCompat.getColorStateList(this, R.color.checkbox_tint_color)

        // 登録ボタン
        viewBinding.buttonRegister.setOnClickListener {
            emailAddressText = viewBinding.editTextEmailAddress.text.toString()
            passwordText = viewBinding.editTextPassword.text.toString()
            signUp(emailAddressText, passwordText)
        }

        // 入力項目の監視
        viewBinding.editTextEmailAddress.addTextChangedListener(textWatcher)
        viewBinding.editTextPassword.addTextChangedListener(textWatcher)
        viewBinding.checkBoxTerms.setOnCheckedChangeListener { _, _ -> validateForm() }

        // 初期状態では登録ボタンを非活性化
        viewBinding.buttonRegister.isEnabled = false
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateForm()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun validateForm() {
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(viewBinding.editTextEmailAddress.text.toString()).matches()
        val isPasswordValid = viewBinding.editTextPassword.text.toString().isNotEmpty()
        val isTermsChecked = viewBinding.checkBoxTerms.isChecked

        viewBinding.buttonRegister.isEnabled = isEmailValid && isPasswordValid && isTermsChecked
    }

    private fun signUp(emailAddress: String, password: String) {
        auth.createUserWithEmailAndPassword(emailAddress, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 03_認証メール送信画面へ遷移
                    val intent = Intent(this, AuthcodeActivity::class.java)
                    startActivity(intent)
                } else {
                    // エラー時の処理を記載
                    val exception = task.exception
                    var message = "Other exception occurred"
                    if (exception is FirebaseAuthWeakPasswordException) {
                        // パスワードが弱すぎる場合の処理
                        message = getString(R.string.firebaseException_weakPassword)
                    } else if (exception is FirebaseAuthInvalidCredentialsException) {
                        // メールアドレスが不正な場合
                        message = getString(R.string.firebaseException_invalidMailAddress)
                    } else if (exception is FirebaseAuthUserCollisionException) {
                        // すでに該当のメールアドレスのユーザーが存在する場合
                        message = getString(R.string.firebaseException_existedMailAddress)
                    }
                    Toast.makeText(
                        baseContext,
                        message,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
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