package com.moneymanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class RegisterOrLoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_or_login)

        val registerButton = findViewById<Button>(R.id.registerButton)
        // ユーザー登録ボタンを押下
        registerButton.setOnClickListener {
            val intent = Intent(this@RegisterOrLoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        // ログインボタンを押下
        loginButton.setOnClickListener {
            val intent = Intent(this@RegisterOrLoginActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }


}