package com.sanjeevdev.shopnow.activities

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.CartAdapter
import com.sanjeevdev.shopnow.data.ProductList
import com.sanjeevdev.shopnow.fragments.CartFragment
import com.sanjeevdev.shopnow.listeners.ProductListener
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.activity_cart.*

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        supportFragmentManager.beginTransaction().replace(R.id.cartContainer,CartFragment()).commit()
    }


}