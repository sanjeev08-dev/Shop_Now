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

class ImagesAdapter(val imagesList: List<String>) : SliderViewAdapter<ImagesAdapter.SliderAdapterVH>() {
    class SliderAdapterVH(val view: View) : SliderViewAdapter.ViewHolder(view) {

        val imageSlide = view.findViewById<ImageView>(R.id.imagesContainer)!!
        fun setImages(get: String) {
            Glide.with(view).load(get).into(imageSlide)
        }
    }

    override fun getCount() = imagesList.size

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.images_layout_container,parent,false)
        return SliderAdapterVH(view)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterVH?, position: Int) {
        viewHolder?.setImages(imagesList.get(position))
    }
}