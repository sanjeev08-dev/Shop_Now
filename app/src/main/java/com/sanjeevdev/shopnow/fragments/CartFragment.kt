package com.sanjeevdev.shopnow.fragments

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.activities.BuyProductActivity
import com.sanjeevdev.shopnow.activities.CartActivity
import com.sanjeevdev.shopnow.activities.MainActivity
import com.sanjeevdev.shopnow.adapter.CartAdapter
import com.sanjeevdev.shopnow.data.ProductList
import com.sanjeevdev.shopnow.listeners.ProductListener
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.fragment_cart.view.*

class CartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cart, container, false)!!
        val db = FirebaseFirestore.getInstance()
        val getCartProductAT = GetCartProductAT()
        getCartProductAT.execute(db)
        return view
    }
    inner class GetCartProductAT : AsyncTask<FirebaseFirestore, Int, List<String>>(),
        ProductListener {
        val productCartList = arrayListOf<ProductList>()
        val cartAdaptor = CartAdapter(productCartList, this)
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {

            getCartProducts(db[0])
            return emptyList()
        }

        private fun getCartProducts(db: FirebaseFirestore?) {
            val cartRecyclerView = view!!.findViewById<RecyclerView>(R.id.cartRecyclerView).apply {
                layoutManager = LinearLayoutManager(activity!!.applicationContext)
                setHasFixedSize(true)
                adapter = cartAdaptor
            }

            val customerID = FirebaseAuth.getInstance().currentUser!!.uid
            db!!.collection(Constants.CUSTOMER_COLLECTION).document(customerID).get()
                .addOnSuccessListener {
                    val cartItems = it.get(Constants.CART) as List<String>

                    if (cartItems.isEmpty()) {
                        cartRecyclerView.visibility = View.GONE
                        view!!.noProductAvailableTV.visibility = View.VISIBLE
                        productCartList.clear()
                        cartAdaptor.notifyDataSetChanged()
                    } else {
                        cartRecyclerView.visibility = View.VISIBLE
                        view!!.noProductAvailableTV.visibility = View.GONE
                        for (item in cartItems) {
                            productCartList.clear()
                            db.collection(Constants.PRODUCTS).document(item).get()
                                .addOnSuccessListener {
                                    val imagesList = it.get(Constants.IMAGE_URL) as List<*>
                                    val imageUrl = imagesList[0].toString()
                                    val name = it.get(Constants.NAME).toString()
                                    val price: String
                                    val quantity =
                                        it.get(Constants.QUANTITY).toString().toInt()
                                    if (quantity == 0) {
                                        price = "Out Of Stock"
                                    } else {
                                        price = it.get(Constants.PRICE).toString() + " Rs."
                                    }
                                    val productID = it.get(Constants.PRODUCTID).toString()
                                    val cart =
                                        ProductList("", imageUrl, name, price, productID)
                                    productCartList.add(cart)
                                    if (productCartList.size > 0) {
                                        cartAdaptor.notifyDataSetChanged()
                                    }
                                }
                                .addOnFailureListener {
                                    Log.e("Error", it.message.toString())
                                }
                        }
                    }
                }
        }

        override fun clickProduct(productList: ProductList, view: View) {
            val intent = Intent(activity!!.applicationContext,BuyProductActivity::class.java)
            intent.putExtra(Constants.PRODUCTID,productList.productID)
            startActivity(intent)
        }

        override fun longClickProduct(productList: ProductList, view: View) {
            val snackbar =
                Snackbar.make(view, "Do you want to remove ${productList.name} from cart", 3000)
                    .setAction(
                        "Yes"
                    ) {
                        FirebaseFirestore.getInstance().collection(Constants.CUSTOMER_COLLECTION)
                            .document(FirebaseAuth.getInstance().currentUser!!.uid).update(
                                Constants.CART,
                                FieldValue.arrayRemove(productList.productID)
                            ).addOnSuccessListener {
                                Toast.makeText(
                                    activity!!.applicationContext,
                                    "Product removed from cart",
                                    Toast.LENGTH_SHORT
                                ).show()
                                getCartProducts(FirebaseFirestore.getInstance())
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    activity!!.applicationContext,
                                    "Error ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
            snackbar.setActionTextColor(Color.parseColor("#FF3030"))
            snackbar.view.background = ContextCompat.getDrawable(activity!!.applicationContext, R.color.white)
            val textView =
                snackbar.view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.BLUE)
            snackbar.show()
        }
    }

}