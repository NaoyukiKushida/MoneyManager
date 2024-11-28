package com.moneymanager.data.models

data class TransactionListModel(
    val transactionId: String = "",
    val date: String = "",
    val content: String = "",
    val amount: Int = 0,
    val iconResId: Int = 0
)