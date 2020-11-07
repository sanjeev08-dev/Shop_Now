package com.sanjeevdev.shopnow.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.listeners.ColorListener
import kotlinx.android.synthetic.main.product_color_container.view.*

class ProductBuyColorAdapter(
    private val productColorList: List<String>,
    val context: Context,
    val listener: ColorListener
) : RecyclerView.Adapter<ProductBuyColorAdapter.ProductBuyColorAdapterVH>() {

    var lastSelectedPosition = -1;

    inner class ProductBuyColorAdapterVH(val view: View) : RecyclerView.ViewHolder(view) {
        private val colorImage = view.findViewById<ImageView>(R.id.detailProductColor)!!
        fun setData(colorCode: String, context: Context, listener: ColorListener, position: Int) {
            if (lastSelectedPosition == position) {
                view.detailProductColorSelected.visibility = View.VISIBLE
            } else {
                view.detailProductColorSelected.visibility = View.GONE
            }
            ImageViewCompat.setImageTintList(
                colorImage,
                ColorStateList.valueOf(Color.parseColor(colorCode))
            )
            colorImage.setOnClickListener {
                lastSelectedPosition = adapterPosition
                notifyDataSetChanged()
                listener.selectColor(colorCode, it)

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductBuyColorAdapterVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_color_container, parent, false)
        return ProductBuyColorAdapterVH(view)
    }

    override fun onBindViewHolder(holder: ProductBuyColorAdapterVH, position: Int) {
        holder.setData(productColorList[position], context, listener, position)
    }

    override fun getItemCount() = productColorList.size
}