package com.moneymanager

import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // 戻るボタンを表示し、押下時に前の画面に戻る
        val toolbarContainer: ConstraintLayout = findViewById(R.id.toolbar_container)
        ToolbarUtils.setupToolbar(this, toolbarContainer, "入出金明細", LeftButtonType.LOGOUT)

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
            }
        })

        // 初期表示を TransactionListFragment に設定
        viewPager.currentItem = 0
        bottomNavigationView.menu.getItem(0).isChecked = true


    }
}
