package com.moneymanager.repositories

import com.moneymanager.data.models.Category

object CategoryRepository {
    private val categories = listOf(
        Category(1, "食費"),
        Category(2, "日用品"),
        Category(3, "趣味・娯楽"),
        Category(4, "交際費"),
        Category(5, "交通費"),
        Category(6, "衣服・美容"),
        Category(7, "健康・医療"),
        Category(8, "自動車"),
        Category(9, "その他"),
    )

    fun getCategories(): List<Category> {
        return categories
    }
}