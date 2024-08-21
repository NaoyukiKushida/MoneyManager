package com.moneymanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionListAdapter(
    private val transactionList: List<Transaction>,
    private val onItemClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val dateTextView: TextView = itemView.findViewById(R.id.date_text_view) // layoutファイルに合わせてIDを設定
//        val categoryTextView: TextView = itemView.findViewById(R.id.category_text_view)
//        val amountTextView: TextView = itemView.findViewById(R.id.amount_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_list_item, parent, false) // layoutファイルに合わせて変更
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]
//        holder.dateTextView.text = transaction.date
//        holder.categoryTextView.text = transaction.category
//        holder.amountTextView.text = transaction.amount.toString()
//
//        holder.itemView.setOnClickListener { onItemClick(transaction) }
    }

    override fun getItemCount(): Int = transactionList.size
}
