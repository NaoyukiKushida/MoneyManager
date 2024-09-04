package com.moneymanager

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.moneymanager.databinding.ActivityLoginBinding
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

class LoginActivity : AppCompatActivity() {

    private val auth = Firebase.auth

    private var mailAddressText: String = ""
    private var passwordText: String = ""

    private lateinit var viewBinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "ログイン", LeftButtonType.BACK)

        // ログインボタン
        viewBinding.signInButton.setOnClickListener {
            signIn()
        }

        // 入力項目の監視
        viewBinding.editTextEmailAddress.addTextChangedListener(textWatcher)
        viewBinding.editTextPassword.addTextChangedListener(textWatcher)

        // 初期状態では登録ボタンを非活性化
        viewBinding.signInButton.isEnabled = false
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

        viewBinding.signInButton.isEnabled = isEmailValid && isPasswordValid
    }

    // サインイン処理
    private fun signIn() {
        val mailAddressEditText = findViewById<EditText>(R.id.editTextEmailAddress)
        mailAddressText = mailAddressEditText.text.toString()
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        passwordText = passwordEditText.text.toString()
        auth.signInWithEmailAndPassword(mailAddressText, passwordText)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    goToAuthcodeActivity()
                } else {
                    // セキュリティ上Exceptionの情報は隠蔽する
                    Toast.makeText(
                        baseContext,
                        "invalid EmailAddress or Password",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    // 03_認証メール送信画面への遷移処理
    private fun goToAuthcodeActivity() {
        val intent = Intent(this, AuthcodeActivity::class.java)
        startActivity(intent)
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