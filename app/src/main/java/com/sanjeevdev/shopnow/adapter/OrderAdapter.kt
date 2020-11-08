package com.sanjeevdev.shopnow.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.sanjeevdev.shopnow.listeners.ProductListener
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.data.OrdersItem
import com.sanjeevdev.shopnow.data.ProductList
import kotlinx.android.synthetic.main.order_product_container.view.*
import kotlinx.android.synthetic.main.products_item_container.view.*

class OrderAdapter(val ordersItem: List<OrdersItem>)
    : RecyclerView.Adapter<OrderAdapter.OrderAdapterVH>(){

    class OrderAdapterVH(val view: View) : RecyclerView.ViewHolder(view) {

        fun setData(ordersItem: OrdersItem) {
            Glide.with(view).load(ordersItem.images).into(view.orderProductImage)
            view.orderProductName.text = ordersItem.name
            if (ordersItem.quantity.toInt() == 1){
                view.orderProductQuantity.text = ordersItem.quantity+" Item"
            }else{
                view.orderProductQuantity.text = ordersItem.quantity+" Items"
            }
            view.orderProductAmount.text = ordersItem.price+" Rs."
            view.orderProductAddress.text = ordersItem.address
            view.orderProductDate.text = ordersItem.orderDate
            ImageViewCompat.setImageTintList(view.orderProductColor, ColorStateList.valueOf(Color.parseColor(ordersItem.color)))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderAdapterVH {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.order_product_container,parent,false)
        return OrderAdapterVH(view)
    }

    override fun onBindViewHolder(holder: OrderAdapterVH, position: Int) {
        holder.setData(ordersItem.get(position))
    }

    override fun getItemCount() = ordersItem.size
}