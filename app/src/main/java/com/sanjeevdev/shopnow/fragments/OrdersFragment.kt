package com.sanjeevdev.shopnow.fragments

import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.adapter.OrderAdapter
import com.sanjeevdev.shopnow.adapter.ProductsAdapter
import com.sanjeevdev.shopnow.data.OrdersItem
import com.sanjeevdev.shopnow.data.ProductList
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_orders.*

class OrdersFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_orders, container, false)!!

        val db = FirebaseFirestore.getInstance()

        val getOrdersAsynTask = GetOrdersAsynTask()
        getOrdersAsynTask.execute(db)

        return view
    }
    inner class GetOrdersAsynTask : AsyncTask<FirebaseFirestore, Int, List<String>>(){
        val orderList = arrayListOf<OrdersItem>()
        val orderAdapter = OrderAdapter(orderList)
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {

            ordersRecyclerView.layoutManager =
                StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            ordersRecyclerView.adapter = orderAdapter
            db[0]?.collection(Constants.CUSTOMER_COLLECTION)!!.document(FirebaseAuth.getInstance().currentUser!!.uid).collection(Constants.ORDERS).get()
                .addOnCompleteListener {
                    if (it.isSuccessful && it.getResult() != null) {
                        orderList.clear()
                        for (documentSnapshot in it.result!!) {
                            val color = documentSnapshot.get(Constants.COLORS).toString()
                            val address = documentSnapshot.get(Constants.CUSTOMER_ADDRESS).toString()
                            val image = documentSnapshot.get(Constants.IMAGE_URL).toString()
                            val name = documentSnapshot.get(Constants.NAME).toString()
                            val orderDate = documentSnapshot.get(Constants.ORDER_DATE).toString()
                            val productID = documentSnapshot.get(Constants.PRODUCTID).toString()
                            val quantity = documentSnapshot.get(Constants.QUANTITY).toString()
                            val price = documentSnapshot.get(Constants.TOTAL_PRICE).toString()

                            val ordersItem = OrdersItem(color,image,name,price,productID,quantity, orderDate,address)
                            orderList.add(ordersItem)
                        }
                        if (orderList.size > 0) {
                            noOrderAvailableTV.visibility = View.GONE
                            orderAdapter.notifyDataSetChanged()
                        }else{
                            noOrderAvailableTV.visibility = View.VISIBLE
                        }
                    }
                }
            return emptyList()
        }

    }
}