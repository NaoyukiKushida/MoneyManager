package com.moneymanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // FirebaseAuth インスタンス

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance() // FirebaseAuth インスタンスを取得

        // ログイン済みかどうかをチェック
        val currentUser = auth.currentUser
        if (currentUser == null) {
            // 未ログイン
            val intent = Intent(this, RegisterOrLoginActivity::class.java)
            startActivity(intent)
            finish() // MainActivity を終了
        } else {
            // ログイン済み
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish() // MainActivity を終了
        }
    }
}