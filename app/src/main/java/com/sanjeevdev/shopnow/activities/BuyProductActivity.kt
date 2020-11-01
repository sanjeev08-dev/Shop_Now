package com.sanjeevdev.shopnow.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.ProductBuyColorAdapter
import com.sanjeevdev.shopnow.data.ColorsList
import com.sanjeevdev.shopnow.listeners.ColorListener
import com.sanjeevdev.shopnow.utils.Constants
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import kotlinx.android.synthetic.main.activity_buy_product.*
import java.text.FieldPosition

class BuyProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_product)
        buyProductQuantity.maxValue = 10

        val db = FirebaseFirestore.getInstance()

        val getDataAsynTask = GetDataAsynTask()
        getDataAsynTask.execute(db)

    }

    inner class GetDataAsynTask : AsyncTask<FirebaseFirestore, Int, List<String>>(),ColorListener {
        @SuppressLint("SetTextI18n")
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {
            val productID = intent.getStringExtra(Constants.PRODUCTID)!!
            db[0]!!.collection(Constants.PRODUCTS).document(productID).get()
                .addOnCompleteListener {
                    buyProductName.text = it.result!!.get(Constants.NAME) as String
                    buyProductBrandName.text =
                        "Sold by ${it.result!!.get(Constants.BRAND) as String}"
                    val quantity = it.result!!.get(Constants.QUANTITY).toString().toInt()
                    val price = it.result!!.get(Constants.PRICE).toString().toInt()
                    buyProductQuantity.maxValue = quantity
                    buyProductPrice.text = "${buyProductQuantity.progress*price}"

                    buyProductQuantity.doOnProgressChanged { numberPicker, progress, formUser ->
                        buyProductPrice.text = "${progress*price}"
                    }
                    val imageList = it.result!!.get(Constants.IMAGE_URL) as List<String>
                    val colorsList = it.result!!.get(Constants.COLORS) as List<String>

                    Glide.with(this@BuyProductActivity).load(imageList[0]).into(buyProductImage)

                    val productColorAdapter = ProductBuyColorAdapter(colorsList, applicationContext,this)
                    val layoutManager = LinearLayoutManager(applicationContext)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    buyProductColor.layoutManager =
                        layoutManager
                    buyProductColor.adapter = productColorAdapter
                    productColorAdapter.notifyDataSetChanged()

                }
            db[0]!!.collection(Constants.CUSTOMER_COLLECTION).document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {
                    buyProductAddress.setText(it.getString(Constants.CUSTOMER_ADDRESS))
                }
            return emptyList()
        }

        override fun selectColor(selectedColor: String, view: View) {
            Toast.makeText(applicationContext, selectedColor, Toast.LENGTH_SHORT).show()
        }

    }
}