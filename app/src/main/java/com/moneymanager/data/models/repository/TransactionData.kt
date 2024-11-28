package com.moneymanager.data.models.repository

// Realm とのデータ変換に特化したデータクラス
data class TransactionData(
    val transactionId: String = "",
    val balanceType: Int = 0, // 0: 支出, 1: 収入
    val category: Int,
    var date: String? = null,
    val amount: Int,
    val content: String
)