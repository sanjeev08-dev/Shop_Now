package com.sanjeevdev.shopnow.activities

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.fragments.CartFragment
import com.sanjeevdev.shopnow.fragments.MainFragment
import com.sanjeevdev.shopnow.fragments.ProductsFragment
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = FirebaseFirestore.getInstance()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MainFragment()
            ).commit()
        }

        val getDataAsyncTask = GetDataAsyncTask()
        getDataAsyncTask.execute(db)

    }

    inner class GetDataAsyncTask :
        AsyncTask<FirebaseFirestore, Int, List<String>>() {
        override fun doInBackground(vararg db: FirebaseFirestore?): List<String> {
            val dataSet = hashSetOf<String>()
            var menuList: List<String> = emptyList()
            db[0]?.collection(Constants.PRODUCTS)?.get()
                ?.addOnSuccessListener {
                    for (document in it) {
                        dataSet.add(document.data.getValue("category") as String)
                    }
                    menuList = dataSet.toList()
                    navigationDrawer.setAppbarTitleTV(Constants.HOME)

                    val menuItems: MutableList<com.shrikanthravi.customnavigationdrawer2.data.MenuItem> =
                        ArrayList()
                    menuItems.add(
                        com.shrikanthravi.customnavigationdrawer2.data.MenuItem(
                            Constants.HOME,
                            R.drawable.clothpic
                        )
                    )

                    for (element in menuList) {
                        menuItems.add(
                            com.shrikanthravi.customnavigationdrawer2.data.MenuItem(
                                element,
                                R.drawable.clothpic
                            )
                        )
                    }
                    menuItems.add(
                        com.shrikanthravi.customnavigationdrawer2.data.MenuItem(
                            Constants.CART_CAPS,
                            R.drawable.clothpic
                        )
                    )

                    navigationDrawer.menuItemList = menuItems
                    navigationDrawer.setOnMenuItemClickListener { it ->
                        val menuTitle = navigationDrawer.menuItemList.get(it).title
                        if (menuTitle == Constants.HOME) {
                            supportFragmentManager.beginTransaction().replace(
                                R.id.fragment_container,
                                MainFragment()
                            ).commit()
                        } else if (it in 0..menuList.size) {
                            val menuItem: String =
                                navigationDrawer.menuItemList.get(it).title.trim()
                            val bundle = Bundle()
                            bundle.putString(Constants.MENU_ITEM, menuItem)
                            val productsFragment = ProductsFragment()
                            productsFragment.arguments = bundle

                            supportFragmentManager.beginTransaction().replace(
                                R.id.fragment_container,
                                productsFragment
                            ).commit()
                        }else if(menuTitle == Constants.CART_CAPS){
                            supportFragmentManager.beginTransaction().replace(
                                R.id.fragment_container,
                                CartFragment()
                            ).commit()
                        }
                    }
                }?.addOnFailureListener {
                    Toast.makeText(applicationContext, "Unknown Error Occur", Toast.LENGTH_SHORT)
                        .show()
                }
            return emptyList()
        }
    }
}