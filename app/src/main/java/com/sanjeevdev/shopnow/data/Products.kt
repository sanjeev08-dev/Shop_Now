package com.sanjeevdev.shopnow.data

data class Products(
    val brand: String,
    val category: String,
    val colors: List<String>,
    val description: String,
    val images: List<String>,
    val name: String,
    val price: String,
    val productID: String,
    val quantity: String
)