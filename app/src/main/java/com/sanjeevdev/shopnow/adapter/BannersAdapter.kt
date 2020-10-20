package com.sanjeevdev.shopnow.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sanjeevdev.shopnow.R
import com.smarteist.autoimageslider.SliderView
import com.smarteist.autoimageslider.SliderViewAdapter

class BannersAdapter(val imagesList: List<String>) : SliderViewAdapter<BannersAdapter.SliderAdapterVH>() {
    class SliderAdapterVH(val view: View) : SliderViewAdapter.ViewHolder(view) {

        val imagesBanner = view.findViewById<ImageView>(R.id.imagesBannerContainer)
        fun setImages(get: String) {
            Glide.with(view).load(get).into(imagesBanner)
        }
    }

    override fun getCount() = imagesList.size

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.banner_images_layout_container,parent,false)
        return SliderAdapterVH(view)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        viewHolder?.setImages(imagesList.get(position))
    }
}