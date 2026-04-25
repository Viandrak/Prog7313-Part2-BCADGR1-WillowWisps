package com.example.willowwallet.data.entities

data class CategoryTotal(
    val categoryId: Int?,
    val categoryName: String,
    val colorHex: String,
    val icon: String,
    val totalSpent: Double
)