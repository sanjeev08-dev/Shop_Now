package com.sanjeevdev.shopnow.listeners

import android.view.View
import com.sanjeevdev.shopnow.data.ProductList

interface ProductListener {
    fun clickProduct(productList: ProductList, view: View)
}