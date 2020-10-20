package com.sanjeevdev.shopnow.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sanjeevdev.shopnow.R

class ProductColorAdapter(private val productColorList: List<String>,val context: Context)
    : RecyclerView.Adapter<ProductColorAdapter.ProductColorAdapterVH>(){

    class ProductColorAdapterVH(val view: View) : RecyclerView.ViewHolder(view) {
        private val colorImage = view.findViewById<ImageView>(R.id.detailProductColor)!!
        fun setData(colorCode: String, context: Context) {
            ImageViewCompat.setImageTintList(colorImage, ColorStateList.valueOf(Color.parseColor(colorCode)))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductColorAdapterVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_color_container,parent,false)
        return ProductColorAdapterVH(view)
    }

    override fun onBindViewHolder(holder: ProductColorAdapterVH, position: Int) {
        holder.setData(productColorList[position],context)
    }

    override fun getItemCount() = productColorList.size
}