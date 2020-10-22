package com.sanjeevdev.shopnow.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.ImagesAdapter
import com.sanjeevdev.shopnow.adapter.ProductColorAdapter
import com.sanjeevdev.shopnow.utils.Constants
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)


        val db = FirebaseFirestore.getInstance()

        val getDataAsynTask = GetDataAsynTask()
        getDataAsynTask.execute(db)

        addToCartButton.setOnClickListener {
            val intent = Intent(this,CartActivity::class.java)
            if (FirebaseAuth.getInstance().currentUser == null){
                intent.apply {
                    putExtra(Constants.HAS_ACCOUNT,false)
                }
            }else{
                intent.apply {
                    putExtra(Constants.HAS_ACCOUNT,true)
                }
            }
            startActivity(intent)
        }
    }

    inner class GetDataAsynTask : AsyncTask<FirebaseFirestore, Int, List<String>>() {
        @SuppressLint("SetTextI18n")
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {
            val productID = intent.getStringExtra(Constants.PRODUCTID)!!
            db[0]!!.collection(Constants.PRODUCTS).document(productID).get()
                .addOnCompleteListener {
                    detailProductName.text = it.result!!.get(Constants.NAME) as String
                    detailProductBrand.text = it.result!!.get(Constants.BRAND) as String
                    detailProductDescription.text = it.result!!.get(Constants.DESCRIPTION) as String
                    val price = it.result!!.get(Constants.PRICE) as String
                    val quantity = it.result!!.get(Constants.QUANTITY).toString().toInt()
                    if (quantity == 0){
                        detailProductPrice.text = "Out Of Stock"
                        addToCartButton.visibility = View.GONE
                        buyNowButton.visibility = View.GONE
                    }else{
                        detailProductPrice.text = "$price Rs."
                    }
                    val imageList = it.result!!.get(Constants.IMAGE_URL) as List<String>
                    val colorsList = it.result!!.get(Constants.COLORS) as List<String>

                    val sliderImages: SliderView = findViewById(R.id.detailProductImages)
                    val adapter = ImagesAdapter(imageList)
                    sliderImages.setSliderAdapter(adapter)
                    sliderImages.setIndicatorAnimation(IndicatorAnimationType.SWAP) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderImages.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    sliderImages.startAutoCycle()

                    val productColorAdapter = ProductColorAdapter(colorsList, applicationContext)
                    val layoutManager = LinearLayoutManager(applicationContext)
                    layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                    detailProductColorsRecyclerView.layoutManager =
                        layoutManager
                    detailProductColorsRecyclerView.adapter = productColorAdapter
                    productColorAdapter.notifyDataSetChanged()

                }
            return emptyList()
        }

    }

    fun imageClick(view: View) {
        Toast.makeText(applicationContext, "Open Cart", Toast.LENGTH_SHORT).show()
    }
}