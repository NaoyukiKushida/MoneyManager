package com.moneymanager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.moneymanager.databinding.FragmentBalanceBinding
import com.moneymanager.databinding.FragmentTransactionListBinding

class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_balance, container, false)
    }
}
