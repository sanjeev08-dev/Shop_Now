package com.sanjeevdev.shopnow.activities

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

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
        lateinit var imageURL: String
        lateinit var productName: String
        lateinit var brandeName: String
        var oldQuantity: Int = 0
        var newQuantity: Int = 1
        lateinit var totalPrice: String
        var colorCode: String = "notSelect"
        lateinit var firebaseReference: FirebaseFirestore
        lateinit var productID: String
        lateinit var orderDate: String
        lateinit var address: String

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
            firebaseReference.collection(Constants.CUSTOMER_COLLECTION)
                .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {
                    address = it.getString(Constants.CUSTOMER_ADDRESS)!!
                    buyProductAddress.setText(address)
                }
            buyNow.setOnClickListener {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    val data = Date()
                    val simpleDateFormat = SimpleDateFormat("dd MMM yyyy , hh:mm a")
                    orderDate = simpleDateFormat.format(data)
                }
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
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Please select one product color",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return emptyList()
        }

        override fun selectColor(selectedColor: String, view: View) {
            colorCode = selectedColor
        }

        private fun submitOrder(isUpdateAddress: Boolean, address: String) {
            if (isUpdateAddress) {
                val map: HashMap<String, Any> = hashMapOf(
                    Constants.CUSTOMER_ADDRESS to address
                )
                addOrder(address)
                firebaseReference.collection(Constants.CUSTOMER_COLLECTION)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid)
                    .update(map)
                    .addOnSuccessListener {
                        Toast.makeText(
                            applicationContext,
                            "Address update successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        buyNow.stopAnimation()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            applicationContext,
                            "Address update failed",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    "${oldQuantity - newQuantity}"
                )

            saveOrderReceipt(
                productID,
                productName,
                brandeName,
                newQuantity,
                totalPrice,
                address,
                orderDate
            )
        }

        private fun saveOrderReceipt(
            productID: String,
            productName: String,
            brandeName: String,
            newQuantity: Int,
            totalPrice: String,
            address: String,
            orderDate: String
        ) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        10
                    )
                } else {
                    savePDF(
                        productID,
                        productName,
                        brandeName,
                        newQuantity,
                        totalPrice,
                        address,
                        orderDate
                    )
                }
            } else {
                savePDF(
                    productID,
                    productName,
                    brandeName,
                    newQuantity,
                    totalPrice,
                    address,
                    orderDate
                )
            }
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
                Constants.CUSTOMER_ADDRESS to address,
                Constants.ORDER_DATE to orderDate
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 10 && grantResults.isEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "Permission denied WRITE", Toast.LENGTH_SHORT).show()
        } else {

            val getDataAsynTask = GetDataAsynTask()
            savePDF(
                getDataAsynTask.productID,
                getDataAsynTask.productName,
                getDataAsynTask.brandeName,
                getDataAsynTask.newQuantity,
                getDataAsynTask.totalPrice,
                getDataAsynTask.address,
                getDataAsynTask.orderDate
            )
        }
    }

    private fun savePDF(
        productID: String,
        productName: String,
        brandeName: String,
        newQuantity: Int,
        totalPrice: String,
        address: String,
        orderDate: String
    ) {
        val myPdfDocument = PdfDocument()
        val myPaint = Paint()

        val myPageInfo = PdfDocument.PageInfo.Builder(1000, 1000, 1).create()!!
        val myPage = myPdfDocument.startPage(myPageInfo)!!
        val canvas = myPage.canvas!!

        //writing pdf code

        //add Header bg
        val bmpHeader = BitmapFactory.decodeResource(
            resources,
            R.drawable.pdfheaderbg
        )!!
        val scaledbmpHeader = Bitmap.createScaledBitmap(bmpHeader, 1000, 300, false)!!
        canvas.drawBitmap(scaledbmpHeader, 0f, 0f, myPaint)

        //Title Text Paint
        val titlePaint = Paint()
        titlePaint.textAlign = Paint.Align.CENTER
        titlePaint.typeface = ResourcesCompat.getFont(applicationContext, R.font.ubuntu_bold)
        titlePaint.textSize = 48F
        titlePaint.color = resources.getColor(R.color.colorPrimary)
        canvas.drawText("Order Receipt", 1000F / 2F, 300F / 2F, titlePaint)

        //Product Heading Paint
        val textHeadingPaint = Paint()
        textHeadingPaint.textAlign = Paint.Align.LEFT
        textHeadingPaint.typeface = ResourcesCompat.getFont(applicationContext, R.font.ubuntu_bold)
        textHeadingPaint.textSize = 30F
        textHeadingPaint.color = resources.getColor(android.R.color.black)

        canvas.drawText("Item Name :", 50F, 234F, textHeadingPaint)
        canvas.drawText("Brand :", 50F, 278F, textHeadingPaint)
        canvas.drawText("Quantity :", 50F, 322F, textHeadingPaint)
        canvas.drawText("Amount to pay :", 50F, 366F, textHeadingPaint)
        canvas.drawText("Delivery address :", 50F, 410F, textHeadingPaint)
        canvas.drawText("Order Date :", 50F, 454F, textHeadingPaint)

        //Product Details Paint
        val textPaint = Paint()
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = ResourcesCompat.getFont(applicationContext, R.font.ubuntu_regular)
        textPaint.textSize = 26F
        textPaint.color = resources.getColor(android.R.color.black)

        canvas.drawText(productName, 244f, 234f, textPaint)
        canvas.drawText(brandeName, 169f, 278f, textPaint)
        canvas.drawText(newQuantity.toString(), 212f, 322f, textPaint)
        canvas.drawText("${totalPrice} Rs.", 295f, 366f, textPaint)
        canvas.drawText(address, 329f, 410f, textPaint)
        canvas.drawText(orderDate, 252f, 454f, textPaint)

        //add Bottom bg
        val bmpBottom = BitmapFactory.decodeResource(
            resources,
            R.drawable.pdfbottombg
        )!!
        val scaledbmpBottom = Bitmap.createScaledBitmap(bmpBottom, 1000, 300, false)!!
        canvas.drawBitmap(scaledbmpBottom, 0f, 700.0f, myPaint)

        //Thanku Paint
        val thankuPaint = Paint()
        thankuPaint.textAlign = Paint.Align.LEFT
        thankuPaint.typeface = ResourcesCompat.getFont(applicationContext, R.font.ubuntu_bold)
        thankuPaint.textSize = 68F
        thankuPaint.color = resources.getColor(android.R.color.white)

        canvas.drawText("Thank You", 450f, 980f, thankuPaint)

        //Company name Paint
        val shopNowPaint = Paint()
        shopNowPaint.textAlign = Paint.Align.LEFT
        shopNowPaint.typeface = ResourcesCompat.getFont(applicationContext, R.font.ubuntu_medium)
        shopNowPaint.textSize = 24F
        shopNowPaint.color = resources.getColor(android.R.color.white)

        canvas.drawText("ShopNow Team", 801f, 980f, shopNowPaint)

        myPdfDocument.finishPage(myPage)

        val directoryFile =
            File(Environment.getExternalStorageDirectory().absolutePath + "/Shop Now")
        directoryFile.mkdirs()

        val file = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            File(
                directoryFile, "/Receipt_${
                    LocalDateTime.now().format(
                        DateTimeFormatter.BASIC_ISO_DATE
                    )
                }.pdf"
            )
        } else {
            File(directoryFile, "/Receipt_${Random.nextInt(10000, 99999)}.pdf")
        }
        try {
            myPdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(
                applicationContext,
                "Your Order Receipt is save to \n ${file.absolutePath}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
        }
        myPdfDocument.close()
    }
}