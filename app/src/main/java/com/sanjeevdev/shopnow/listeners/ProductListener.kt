package com.sanjeevdev.shopnow.listeners

import com.sanjeevdev.shopnow.data.ProductList

interface ProductListener {
    fun clickProduct(productList: ProductList)
}