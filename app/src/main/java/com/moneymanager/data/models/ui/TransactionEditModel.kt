package com.moneymanager.data.models.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionEditModel(
    val transactionId: String = "",
    val balanceType: BalanceType = BalanceType.EXPENSE, // デフォルトは支出
    val amount: Int = 0,
    val category: Category = Category(1, "食費"), // デフォルトは空のカテゴリ
    var date: String? = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()),
    val content: String = "",
)

enum class BalanceType {
    EXPENSE,
    INCOME
}

data class Category(
    val id: Int,
    val name: String,
)