package com.moneymanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.moneymanager.databinding.ActivityAuthcodeBinding
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

private lateinit var viewBinding: ActivityAuthcodeBinding

class AuthcodeActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authcode)

        viewBinding = ActivityAuthcodeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "メールアドレス確認", LeftButtonType.BACK)

        auth.currentUser?.let {
            if (it.isEmailVerified) {
                // メール認証済みの場合
                goToDashboardActivity()
            } else {
                // メール認証未済の場合
                sendEmailVerification(it)
            }
        }
    }

    // 05_入出金明細画面への遷移処理
    private fun goToDashboardActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    // 確認メールの送信
    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification()?.addOnCompleteListener { task ->
            val textView = findViewById<TextView>(R.id.afterSendingTextView)
            val button = findViewById<Button>(R.id.authcodeButton)
            if (task.isSuccessful) {
                textView.text = getString(R.string.afterSendingText)
                button.text = getString(R.string.afterSendingButtonTitle)
                button.setOnClickListener {
                    startActivity(Intent(this, RegisterOrLoginActivity::class.java))
                    finish()
                }
            } else {
                textView.text = getString(R.string.failureSendingText)
                button.text = getString(R.string.resendButtonTitle)
                button.setOnClickListener {
                    sendEmailVerification(user)
                }
            }
        }
    }

    // 戻るボタン押下時の処理
    override fun onSupportNavigateUp(): Boolean {
        // 確認メールで認証は終わっていないが、戻れるように、サインアウトしておく
        // 認証が完了した際はログイン画面からログインできる想定
        singOut()
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun singOut() {
        val myApp = applicationContext as MyApplication
        myApp.auth.signOut()
        startActivity(Intent(this, RegisterOrLoginActivity::class.java))
        finish()
    }


}