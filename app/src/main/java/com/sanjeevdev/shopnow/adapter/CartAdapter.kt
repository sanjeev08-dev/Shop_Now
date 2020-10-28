package com.sanjeevdev.shopnow.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.data.ProductList
import com.sanjeevdev.shopnow.listeners.ProductListener
import kotlinx.android.synthetic.main.adaptor_cart.view.*

class CartAdapter(
    private val productCartList: List<ProductList>,
    val listener: ProductListener
)
    : RecyclerView.Adapter<CartAdapter.ProductCartAdapterVH>(){


    class ProductCartAdapterVH(val view: View) : RecyclerView.ViewHolder(view) {
        fun setData(productCartList: ProductList, listener: ProductListener) {
            Glide.with(view).load(productCartList.image).into(view.productCartImage)
            view.productCartName.text = productCartList.name
            view.productCartPrice.text = productCartList.price

            view.productCartCard.setOnLongClickListener {
                listener.clickProduct(productCartList,it)
                return@setOnLongClickListener true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCartAdapterVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adaptor_cart,parent,false)!!
        return ProductCartAdapterVH(view)
    }

    override fun onBindViewHolder(holder: ProductCartAdapterVH, position: Int) {
        holder.setData(productCartList.get(position),listener)
    }

    override fun getItemCount() = productCartList.size
}