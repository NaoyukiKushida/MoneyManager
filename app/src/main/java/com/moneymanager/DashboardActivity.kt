package com.moneymanager

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.moneymanager.adapters.DashboardPagerAdapter
import com.moneymanager.ui.common.LeftButtonType
import com.moneymanager.ui.common.ToolbarUtils

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var toolbarContainer: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        toolbarContainer = findViewById(R.id.toolbar_container)

        // ログアウト処理を定義
        val onLogoutClick: () -> Unit = {
            // ログアウト確認ダイアログを表示
            AlertDialog.Builder(this)
                .setTitle("ログアウト")
                .setMessage("ログアウトしますか？") // 必要に応じてメッセージを追加
                .setPositiveButton("OK") { dialog, which ->
                    // ログアウトする
                    signOut()
                }
                .setNegativeButton("キャンセル", null) // キャンセルボタンは何もせずダイアログを閉じる
                .show()
        }

        // 戻るボタンを表示し、押下時に前の画面に戻る
        ToolbarUtils.setupToolbar(
            this,
            toolbarContainer,
            "入出金明細",
            LeftButtonType.LOGOUT,
            onLogoutClick // ログアウトコールバックを渡す
        )

        viewPager = findViewById(R.id.view_pager)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        val adapter = DashboardPagerAdapter(this)
        viewPager.adapter = adapter

        // BottomNavigationViewとViewPager2の連携
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_transaction_list -> {
                    viewPager.currentItem = 0
                    true // 処理が成功したことを示す
                }
                R.id.navigation_balance -> {
                    viewPager.currentItem = 1
                    true
                }
                else -> false // 処理されなかったアイテム
            }
        }


        // ViewPager2のページ変更時にBottomNavigationViewの選択状態を更新
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true

                // ページに対応するタイトルを設定
                val title = when (position) {
                    0 -> "入出金明細"
                    1 -> "収支"
                    else -> ""
                }
                ToolbarUtils.updateToolbarTitle(toolbarContainer, title)
            }
        })

        // 初期表示を TransactionListFragment に設定
        viewPager.currentItem = 0
        bottomNavigationView.menu.getItem(0).isChecked = true

    }

    private fun signOut() {
        val myApp = applicationContext as MyApplication
        myApp.auth.signOut()
        // ログアウト時にRealmデータベースを削除
//        (application as MyApplication).deleteRealm()
        startActivity(Intent(this, RegisterOrLoginActivity::class.java))
        finish()
    }
}
