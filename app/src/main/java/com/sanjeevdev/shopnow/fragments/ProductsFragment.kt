package com.sanjeevdev.shopnow.fragments

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.listeners.ProductListener
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.activities.ProductDetailActivity
import com.sanjeevdev.shopnow.adapter.ProductsAdapter
import com.sanjeevdev.shopnow.data.ProductList
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.fragment_products.*

class ProductsFragment : Fragment() {
    lateinit var bundle: Bundle
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_products, container, false)
        bundle = this.arguments!!
        val db = FirebaseFirestore.getInstance()

        val getProductAsynTask = GetProductAsynTask()
        getProductAsynTask.execute(db)

        return view
    }

    inner class GetProductAsynTask : AsyncTask<FirebaseFirestore, Int, List<String>>(),
        ProductListener {
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {

            val productList = arrayListOf<ProductList>()
            val productAdapter = ProductsAdapter(productList, this)
            productRecyclerView.layoutManager = LinearLayoutManager(activity!!.applicationContext)
            productRecyclerView.adapter = productAdapter

            val menuItemString = bundle.getString(Constants.MENU_ITEM)!!

            db[0]?.collection(Constants.PRODUCTS)!!
                .whereEqualTo(Constants.CATEGORY, menuItemString)
                .get()
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
                            productAdapter.notifyDataSetChanged()
                        }
                    }
                }



            return emptyList()
        }

        override fun clickProduct(productList: ProductList, view: View) {
            val intent = Intent(activity!!, ProductDetailActivity::class.java)
            intent.putExtra(Constants.PRODUCTID,productList.productID)
            startActivity(intent)
        }

        override fun longClickProduct(productList: ProductList, view: View) {

        }

    }

}