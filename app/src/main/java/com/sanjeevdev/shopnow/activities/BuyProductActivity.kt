package com.sanjeevdev.shopnow.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.awesomedialog.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.ProductBuyColorAdapter
import com.sanjeevdev.shopnow.listeners.ColorListener
import com.sanjeevdev.shopnow.utils.Constants
import it.sephiroth.android.library.numberpicker.doOnProgressChanged
import kotlinx.android.synthetic.main.activity_buy_product.*

class BuyProductActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_product)
        buyNow.saveInitialState()

        val db = FirebaseFirestore.getInstance()

        val getDataAsynTask = GetDataAsynTask()
        getDataAsynTask.execute(db)

    }

    inner class GetDataAsynTask : AsyncTask<FirebaseFirestore, Int, List<String>>(), ColorListener {
        private lateinit var imageURL: String
        private lateinit var productName: String
        private lateinit var brandeName: String
        private var oldQuantity: Int = 0
        private var newQuantity: Int = 0
        private lateinit var totalPrice: String
        private var colorCode: String = "notSelect"
        private lateinit var firebaseReference: FirebaseFirestore
        private lateinit var productID: String

        @SuppressLint("SetTextI18n")
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {
            productID = intent.getStringExtra(Constants.PRODUCTID)!!
            firebaseReference = db[0]!!
            firebaseReference.collection(Constants.PRODUCTS).document(productID).get()
                .addOnCompleteListener {

                    productName = it.result!!.get(Constants.NAME) as String
                    brandeName = it.result!!.get(Constants.BRAND) as String
                    oldQuantity = it.result!!.get(Constants.QUANTITY).toString().toInt()

                    buyProductName.text = productName
                    buyProductBrandName.text = "Sold by $brandeName"
                    val price = it.result!!.get(Constants.PRICE).toString().toInt()
                    buyProductQuantity.maxValue = oldQuantity
                    buyProductPrice.text = "${buyProductQuantity.progress * price}"

                    buyProductQuantity.doOnProgressChanged { numberPicker, progress, formUser ->
                        newQuantity = progress
                        buyProductPrice.text = "${progress * price}"
                    }
                    totalPrice = buyProductPrice.text as String
                    val imageList = it.result!!.get(Constants.IMAGE_URL) as List<String>
                    val colorsList = it.result!!.get(Constants.COLORS) as List<String>

                    imageURL = imageList[0]
                    Glide.with(this@BuyProductActivity).load(imageURL).into(buyProductImage)

                    val productColorAdapter =
                        ProductBuyColorAdapter(colorsList, applicationContext, this)
                    val layoutManager = LinearLayoutManager(applicationContext)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    buyProductColor.layoutManager =
                        layoutManager
                    buyProductColor.adapter = productColorAdapter
                    productColorAdapter.notifyDataSetChanged()

                }
            var address: String = ""
            firebaseReference.collection(Constants.CUSTOMER_COLLECTION)
                .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {
                    address = it.getString(Constants.CUSTOMER_ADDRESS)!!
                    buyProductAddress.setText(address)
                }
            buyNow.setOnClickListener {
                if (colorCode != "notSelect") {
                    buyNow.startAnimation()
                    if (address != buyProductAddress.text.toString()) {
                        AwesomeDialog.build(this@BuyProductActivity)
                            .title(
                                "Delivery Address",
                                ResourcesCompat.getFont(applicationContext, R.font.ubuntu_bold),
                                ContextCompat.getColor(
                                    this@BuyProductActivity,
                                    android.R.color.black
                                )
                            )
                            .body(
                                "This address is different from registered address.\n Do you want to change it now?",
                                ResourcesCompat.getFont(applicationContext, R.font.ubuntu_regular),
                                ContextCompat.getColor(
                                    this@BuyProductActivity,
                                    android.R.color.black
                                )
                            )
                            .icon(R.drawable.ic_address)
                            .onPositive("Yes, Do It") {
                                submitOrder(true, buyProductAddress.text.toString())
                            }.onNegative("Not Yet Now") {
                                submitOrder(false, address)
                            }
                    } else {
                        submitOrder(false, address)
                    }
                }else{
                    Toast.makeText(applicationContext, "Please select one product color", Toast.LENGTH_SHORT).show()
                }
            }
            return emptyList()
        }

        override fun selectColor(selectedColor: String, view: View) {
            colorCode = selectedColor
        }

        private fun submitOrder(isUpdateAddress: Boolean, address: String) {
            if (isUpdateAddress) {
                val map:HashMap<String,Any> = hashMapOf(
                    Constants.CUSTOMER_ADDRESS to address
                )
                addOrder(address)
                firebaseReference.collection(Constants.CUSTOMER_COLLECTION)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update(map)
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Address update successfully", Toast.LENGTH_SHORT).show()
                        buyNow.stopAnimation()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, "Address update failed", Toast.LENGTH_SHORT).show()
                    }
            } else {
                addOrder(address)
                buyNow.stopAnimation()
                finish()
            }

            FirebaseFirestore.getInstance().collection(Constants.CUSTOMER_COLLECTION)
                .document(FirebaseAuth.getInstance().currentUser!!.uid).update(
                    Constants.CART,
                    FieldValue.arrayRemove(productID)
                )
            FirebaseFirestore.getInstance().collection(Constants.PRODUCTS)
                .document(productID).update(
                    Constants.QUANTITY,
                    "${oldQuantity-newQuantity}"
                )

        }

        private fun addOrder(address: String) {
            val map = hashMapOf(
                Constants.PRODUCTID to productID,
                Constants.IMAGE_URL to imageURL,
                Constants.BRAND to brandeName,
                Constants.NAME to productName,
                Constants.QUANTITY to newQuantity,
                Constants.TOTAL_PRICE to totalPrice,
                Constants.COLORS to colorCode,
                Constants.CUSTOMER_ADDRESS to address
            )
            firebaseReference.collection(Constants.CUSTOMER_COLLECTION)
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection(Constants.ORDERS)
                .add(map)
                .addOnFailureListener {
                    Toast.makeText(applicationContext, it.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
        }
    }
}