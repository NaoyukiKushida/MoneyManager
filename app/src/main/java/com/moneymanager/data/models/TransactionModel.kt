package com.moneymanager.data.models

data class TransactionModel(
    val type: TransactionType,
    val amount: Int,
    val category: Category,
    val content: String
)

enum class TransactionType {
    EXPENSE,
    INCOME
}

data class Category(
    val id: Int,
    val name: String
)