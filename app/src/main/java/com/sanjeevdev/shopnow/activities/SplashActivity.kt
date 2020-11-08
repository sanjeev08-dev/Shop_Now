package com.sanjeevdev.shopnow.activities

import android.content.Intent
import com.daimajia.androidanimations.library.Techniques
import com.sanjeevdev.shopnow.R
import com.viksaa.sssplash.lib.activity.AwesomeSplash
import com.viksaa.sssplash.lib.cnst.Flags
import com.viksaa.sssplash.lib.model.ConfigSplash

class SplashActivity : AwesomeSplash() {
    override fun initSplash(configSplash: ConfigSplash?) {
        /* you don't have to override every property */

        //Customize Circular Reveal
        configSplash?.backgroundColor = R.color.colorPrimary //any color you want form colors.xml
        configSplash?.animCircularRevealDuration = 1500 //int ms
        configSplash?.revealFlagX = Flags.REVEAL_RIGHT  //or Flags.REVEAL_LEFT
        configSplash?.revealFlagY = Flags.REVEAL_BOTTOM //or Flags.REVEAL_TOP
        //Choose LOGO OR PATH if you don't provide String value for path it's logo by default

        //Customize Logo
        configSplash?.logoSplash = R.drawable.shopicon //or any other drawable
        configSplash?.animLogoSplashDuration = 1000 //int ms
        configSplash?.animLogoSplashTechnique =
            Techniques.FadeInDown //choose one form Techniques (ref: https://github.com/daimajia/AndroidViewAnimations)

        //Customize Title
        configSplash?.titleSplash = "Shop Now"
        configSplash?.titleTextColor = R.color.white
        configSplash?.titleTextSize = 30f //float value
        configSplash?.animTitleDuration = 1000
        configSplash?.animTitleTechnique = Techniques.SlideInRight
        configSplash?.titleFont =
            "fonts/ubuntu_bold.ttf" //provide string to your font located in assets/fonts/
    }

    override fun animationsFinished() {
        startActivity(Intent(applicationContext, OnBoardingActivity::class.java))
        finish()
    }


}