package com.sanjeevdev.shopnow.listeners

import android.view.View
import com.sanjeevdev.shopnow.data.ProductList

interface ColorListener {
    fun selectColor(selectedColor:String, view: View)
}