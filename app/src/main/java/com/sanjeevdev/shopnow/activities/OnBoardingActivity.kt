package com.sanjeevdev.shopnow.activities

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.viewpager2.widget.ViewPager2
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.OnboardingAdapter
import com.sanjeevdev.shopnow.data.OnboardingItem
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.activity_on_boarding.*

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var onboardingAdapter: OnboardingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        if (sharedPreferences.getBoolean(Constants.IS_NOT_FIRST,false)){
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }

        setupOnBoardingItem()
        onBoardingViewPager.adapter = onboardingAdapter
        setupOnBoardingIndicators()
        setCurrentOnBoardingIndicator(0)

        onBoardingViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentOnBoardingIndicator(position)
            }
        })

        startButton.setOnClickListener {
            if (onBoardingViewPager.currentItem + 1 < onboardingAdapter.itemCount) {
                onBoardingViewPager.setCurrentItem(onBoardingViewPager.currentItem + 1)
            } else {
                val editor = sharedPreferences.edit()
                editor.apply {
                    putBoolean(Constants.IS_NOT_FIRST,true)
                }
                editor.apply()

                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
        skipButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.apply {
                putBoolean(Constants.IS_NOT_FIRST,true)
            }
            editor.apply()
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }
    }


    private fun setupOnBoardingItem() {

        val onboardingItem: ArrayList<OnboardingItem> = ArrayList<OnboardingItem>()

        val onboardingItem1 =
            ContextCompat.getDrawable(applicationContext, R.drawable.basketpic)?.let {
                OnboardingItem(
                    it,
                    getString(R.string.home_accesseries_title),
                    getString(R.string.home_accesseries_middle),
                    getString(
                        R.string.home_accesseries_discription
                    )
                )
            }
        val onboardingItem2 =
            ContextCompat.getDrawable(applicationContext, R.drawable.clothpic)?.let {
                OnboardingItem(
                    it,
                    getString(R.string.cloths_accesseries_title),
                    getString(R.string.cloths_accesseries_middle),
                    getString(
                        R.string.cloths_accesseries_discription
                    )
                )
            }
        val onboardingItem3 =
            ContextCompat.getDrawable(applicationContext, R.drawable.mobilepic)?.let {
                OnboardingItem(
                    it,
                    getString(R.string.mobile_accesseries_title),
                    getString(R.string.mobile_accesseries_middle),
                    getString(
                        R.string.mobile_accesseries_discription
                    )
                )
            }

        if (onboardingItem1 != null) {
            onboardingItem.add(onboardingItem1)
        }
        if (onboardingItem2 != null) {
            onboardingItem.add(onboardingItem2)
        }
        if (onboardingItem3 != null) {
            onboardingItem.add(onboardingItem3)
        }

        onboardingAdapter = OnboardingAdapter(onboardingItem)
    }

    private fun setupOnBoardingIndicators() {
        val indicators: Array<ImageView?> = arrayOfNulls<ImageView>(onboardingAdapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in 0..indicators.size - 1) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            indicatorsLayout.addView(indicators[i])
        }
    }

    private fun setCurrentOnBoardingIndicator(index: Int) {
        val childCount = indicatorsLayout.childCount
        for (i in 0..childCount - 1) {
            val imageView = indicatorsLayout.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
        if (index == onboardingAdapter.itemCount - 1) {
            startButton.text = getString(R.string.start)
        } else {
            startButton.text = getString(R.string.next)
        }
    }

}