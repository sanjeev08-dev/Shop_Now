package com.sanjeevdev.shopnow.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.makeramen.roundedimageview.RoundedImageView
import com.sanjeevdev.shopnow.BuildConfig
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.ImagesAdapter
import com.sanjeevdev.shopnow.adapter.ProductColorAdapter
import com.sanjeevdev.shopnow.utils.Constants
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.activity_product_detail.*
import java.io.File
import java.io.FileOutputStream

class ProductDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        buyNowButton.saveInitialState()
        addToCartButton.saveInitialState()
        val db = FirebaseFirestore.getInstance()

        val getDataAsynTask = GetDataAsynTask()
        getDataAsynTask.execute(db)

        addToCartButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(this, AccountActivity::class.java))
                finish()
            } else {
                addToCartButton.startAnimation()
                val userID = FirebaseAuth.getInstance().currentUser!!.uid
                FirebaseFirestore.getInstance().collection(Constants.CUSTOMER_COLLECTION)
                    .document(userID).update(
                        Constants.CART,
                        FieldValue.arrayUnion(intent.getStringExtra(Constants.PRODUCTID)!!)
                    )
                    .addOnSuccessListener {
                        Toast.makeText(applicationContext, "Added to cart", Toast.LENGTH_SHORT)
                            .show()
                        addToCartButton.revertAnimation()
                        addToCartButton.background = ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.progress_button_background
                        )
                    }
                    .addOnFailureListener {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                        addToCartButton.revertAnimation()
                        addToCartButton.background = ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.progress_button_background
                        )
                    }
            }
        }
        buyNowButton.setOnClickListener {
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(this, AccountActivity::class.java))
                finish()
            } else {
                val productID = intent.getStringExtra(Constants.PRODUCTID)!!
                val intent = Intent(this, BuyProductActivity::class.java)
                intent.putExtra(Constants.PRODUCTID, productID)
                startActivity(intent)
                finish()
            }
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
                    if (quantity == 0) {
                        detailProductPrice.text = "Out Of Stock"
                        addToCartButton.visibility = View.GONE
                        buyNowButton.visibility = View.GONE
                    } else {
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

            shareButton.setOnClickListener {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            10
                        )
                    } else {
                        saveQR(productID)
                    }
                } else {
                    saveQR(productID)
                }

            }
            return emptyList()
        }

    }

    private fun saveQR(productID: String) {
        val writer = MultiFormatWriter()
        try {
            val bitMatrix = writer.encode(productID, BarcodeFormat.QR_CODE, 250, 250)
            detailProductQRCode.setImageBitmap(BarcodeEncoder().createBitmap(bitMatrix))
        } catch (e: Exception) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        detailProductQRCode.buildDrawingCache()
        val bitmap = detailProductQRCode.drawingCache!!
        try {
            val cachePath =
                File(Environment.getExternalStorageDirectory().absolutePath + "/Android/data/com.sanjeevdev.shopnow.activities")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/QRCodeImage.png")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.close()
        } catch (e: Exception) {
            Log.e("H", e.localizedMessage)
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
        val imagePath =
            File(Environment.getExternalStorageDirectory().absolutePath + "/Android/data/com.sanjeevdev.shopnow.activities")
        val filePath = File(imagePath, "/QRCodeImage.png")
        val contentUri = FileProvider.getUriForFile(
            applicationContext,
            BuildConfig.APPLICATION_ID + ".provider", filePath
        );
        if (contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND;
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, contentResolver.getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "Download ShowNow app from below link\nwww.shopnowapp.com \nAnd Scan this code for view Item \nGood Luck\nDeveloper - Sanjeev Kumar"
            )
            shareIntent.type = "image/png";
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 10 && grantResults.isEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Permission denied WRITE", Toast.LENGTH_SHORT).show()
        } else {
            saveQR(intent.getStringExtra(Constants.PRODUCTID)!!)
        }
    }


    fun imageClick(view: View) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, AccountActivity::class.java))
        } else {
            startActivity(Intent(this, CartActivity::class.java))
            finish()
        }
    }
}