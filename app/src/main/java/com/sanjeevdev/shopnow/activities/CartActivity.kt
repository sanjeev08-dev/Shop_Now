package com.sanjeevdev.shopnow.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.fragments.AccountFragment
import com.sanjeevdev.shopnow.fragments.MainFragment
import com.sanjeevdev.shopnow.utils.Constants

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val hasAccount = intent.getBooleanExtra(Constants.HAS_ACCOUNT,false)
        if (hasAccount){
            supportFragmentManager.beginTransaction().replace(
                R.id.cartContainer,
                AccountFragment()
            ).commit()
        }else{
            supportFragmentManager.beginTransaction().replace(
                R.id.cartContainer,
                AccountFragment()
            ).commit()
        }
    }
}