package com.moneymanager.data.models


data class TransactionListModel(
    val transactionId: Int, // 取引IDを追加
    val date: String,
    val content: String,
    val amount: Int,
    val iconResId: Int
)