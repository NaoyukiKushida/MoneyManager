package com.moneymanager

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionListAdapter
    private val transactionList = mutableListOf<Transaction>() // 仮のデータ

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transaction_list, container, false)

        recyclerView = view.findViewById(R.id.transaction_list_recycler_view) // RecyclerViewのIDを修正
        adapter = TransactionListAdapter(transactionList) { transaction ->
            // 項目クリック時の処理（編集画面への遷移など）
            val intent = Intent(requireContext(), TransactionEditActivity::class.java)
            // intent.putExtra("transaction", transaction) // データを渡す（詳細な実装は後ほど）
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 新規登録ボタンのクリックリスナーと画像設定
        val addButton: ImageButton = view.findViewById(R.id.add_transaction_button)
        addButton.setOnClickListener {
            val intent = Intent(requireContext(), TransactionEditActivity::class.java)
            startActivity(intent)
        }

        return view
    }
}

// 仮のデータクラス（実際のアプリではデータベースなどから取得）
data class Transaction(val date: String, val category: String, val amount: Int)
