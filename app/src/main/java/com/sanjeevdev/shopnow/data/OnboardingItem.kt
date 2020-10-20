package com.sanjeevdev.shopnow.data

import android.graphics.drawable.Drawable

data class OnboardingItem(
    val picture:Drawable,
    val topHeading:String,
    val lowerHeading:String,
    val description:String
)