package com.moneymanager.data.models

import java.util.Date

data class TransactionEditModel(
    val transactionId: Int = 0, // 新規作成時は0
    val type: TransactionType = TransactionType.EXPENSE, // デフォルトは支出
    val amount: Int = 0,
    val category: Category = Category(1, "食費"), // デフォルトは空のカテゴリ
    val date: Date = Date(), // デフォルト値として現在の日付を設定
    val content: String = ""
)

enum class TransactionType {
    EXPENSE,
    INCOME
}

data class Category(
    val id: Int,
    val name: String
)