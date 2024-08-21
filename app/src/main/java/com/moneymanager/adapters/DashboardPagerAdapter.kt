package com.moneymanager.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moneymanager.TransactionListFragment
import com.moneymanager.BalanceFragment

class DashboardPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> TransactionListFragment()
        1 -> BalanceFragment()
        else -> throw IllegalStateException("Invalid position")
    }
}
