package com.sanjeevdev.shopnow.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.sanjeevdev.shopnow.listeners.ProductListener
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.data.ProductList

class ProductsAdapter(val productList: List<ProductList>, val productListener: ProductListener)
    : RecyclerView.Adapter<ProductsAdapter.ProductAdapterVH>(){

    class ProductAdapterVH(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.productImage)
        val name = view.findViewById<TextView>(R.id.productName)
        val description = view.findViewById<TextView>(R.id.productDescription)
        val price = view.findViewById<TextView>(R.id.productPrice)
        val card = view.findViewById<MaterialCardView>(R.id.productCard)
        fun setData(productList: ProductList, productListener: ProductListener) {
            Glide.with(view).load(productList.image).into(image)
            name.text = productList.name
            description.text = productList.description
            price.text = productList.price
            card.setOnClickListener {
                productListener.clickProduct(productList)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductAdapterVH {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.products_item_container,parent,false)
        return ProductAdapterVH(view)
    }

    override fun onBindViewHolder(holder: ProductAdapterVH, position: Int) {
        holder.setData(productList.get(position),productListener)
    }

    override fun getItemCount() = productList.size
}