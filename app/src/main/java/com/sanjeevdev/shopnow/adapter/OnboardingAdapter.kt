package com.sanjeevdev.shopnow.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sanjeevdev.shopnow.data.OnboardingItem
import com.sanjeevdev.shopnow.R
import kotlinx.android.synthetic.main.onboarding_container.view.*

class OnboardingAdapter(val onboardingItem : List<OnboardingItem>): RecyclerView.Adapter<OnboardingAdapter.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val onboardingPic = view.picture
        val onboardingTopHeading = view.topHeading
        val onboardingLowerHeading = view.lowerHeading
        val onboardingDescription = view.discription

        fun setOnBoardingData(onboardingItem: OnboardingItem) {
            onboardingPic.setImageDrawable(onboardingItem.picture)
            onboardingTopHeading.setText(onboardingItem.topHeading)
            onboardingLowerHeading.setText(onboardingItem.lowerHeading)
            onboardingDescription.setText(onboardingItem.description)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.onboarding_container, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setOnBoardingData(onboardingItem.get(position))
    }

    override fun getItemCount() = onboardingItem.size
}