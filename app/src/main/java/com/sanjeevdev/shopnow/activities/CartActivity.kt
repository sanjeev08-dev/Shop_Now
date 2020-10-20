package com.sanjeevdev.shopnow.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.fragments.AccountFragment
import com.sanjeevdev.shopnow.fragments.MainFragment

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        supportFragmentManager.beginTransaction().replace(
            R.id.cartContainer,
            AccountFragment()
        ).commit()
    }
}