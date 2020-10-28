package com.sanjeevdev.shopnow.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.listeners.ProductListener
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.activities.ProductDetailActivity
import com.sanjeevdev.shopnow.adapter.BannersAdapter
import com.sanjeevdev.shopnow.adapter.ProductsAdapter
import com.sanjeevdev.shopnow.data.ProductList
import com.sanjeevdev.shopnow.utils.Constants
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        val db = FirebaseFirestore.getInstance()

        val getDataAsyncTask = GetBannersAsynTask()
        getDataAsyncTask.execute(db)

        return view
    }

    inner class GetBannersAsynTask : AsyncTask<FirebaseFirestore, Int, List<String>>(),
        ProductListener {
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {
            db[0]!!.collection(Constants.ROOT_COLLECTION).document(Constants.BANNERS).get()
                .addOnSuccessListener {
                    val imageList = it.get("banners") as List<String>
                    val sliderView: SliderView = view!!.findViewById(R.id.banners)

                    val adapter = BannersAdapter(imageList)

                    sliderView.setSliderAdapter(adapter)

                    sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                    sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    sliderView.startAutoCycle()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Banners loading failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            val productList = arrayListOf<ProductList>()
            val productAdapter = ProductsAdapter(productList, this)
            productsItemRecyclerView.layoutManager =
                LinearLayoutManager(activity!!.applicationContext)
            productsItemRecyclerView.adapter = productAdapter
            db[0]?.collection(Constants.PRODUCTS)!!.get()
                .addOnCompleteListener {
                    if (it.isSuccessful && it.getResult() != null) {
                        productList.clear()
                        for (documentSnapshot in it.result!!) {
                            val imagesList = documentSnapshot.get(Constants.IMAGE_URL) as List<*>
                            val imageUrl = imagesList[0].toString()
                            val name = documentSnapshot.get(Constants.NAME).toString()
                            val description = documentSnapshot.get(Constants.DESCRIPTION).toString()
                            val price: String
                            val quantity =
                                documentSnapshot.get(Constants.QUANTITY).toString().toInt()
                            if (quantity == 0) {
                                price = "Out Of Stock"
                            } else {
                                price = documentSnapshot.get(Constants.PRICE).toString() + " Rs."
                            }
                            val productID = documentSnapshot.get(Constants.PRODUCTID).toString()
                            val products =
                                ProductList(description, imageUrl, name, price, productID)
                            productList.add(products)
                        }
                        if (productList.size > 0) {
                            recentSearchText.visibility = View.VISIBLE
                            productAdapter.notifyDataSetChanged()
                        }
                    }
                }


            return emptyList()
        }

        override fun clickProduct(productList: ProductList, view: View) {
            val intent = Intent(activity!!,ProductDetailActivity::class.java)
            intent.putExtra(Constants.PRODUCTID,productList.productID)
            startActivity(intent)
        }

    }

}