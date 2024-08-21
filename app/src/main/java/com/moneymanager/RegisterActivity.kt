package com.moneymanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "新規ユーザー登録", LeftButtonType.BACK)

        val transactionButton = findViewById<Button>(R.id.transactionButton)
        transactionButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}