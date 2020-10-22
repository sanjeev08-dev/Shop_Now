package com.sanjeevdev.shopnow.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.radiobutton.MaterialRadioButton
import com.sanjeevdev.shopnow.R
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.onlyNumbers
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)!!

        val loginAccountRB = view.findViewById<MaterialRadioButton>(R.id.loginAccountRB)!!
        val createAccountRB = view.findViewById<MaterialRadioButton>(R.id.createAccountRB)!!
        val loginAccountLayout = view.findViewById<LinearLayout>(R.id.loginAccountLayout)!!
        val createAccountLayout = view.findViewById<LinearLayout>(R.id.createAccountLayout)!!
        val accountRG = view.findViewById<RadioGroup>(R.id.accountRG)!!

        if (R.id.loginAccountRB == accountRG.checkedRadioButtonId) {
            loginAccountLayout.visibility = View.VISIBLE
            createAccountLayout.visibility = View.GONE
            createAccountRB.setBackgroundColor(
                ContextCompat.getColor(
                    activity!!.applicationContext,
                    R.color.white
                )
            )
            loginAccountRB.setBackgroundColor(
                ContextCompat.getColor(
                    activity!!.applicationContext,
                    R.color.dividerColor
                )
            )
        }
        accountRG.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.loginAccountRB -> {
                    loginAccountLayout.visibility = View.VISIBLE
                    createAccountLayout.visibility = View.GONE
                    createAccountRB.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.white
                        )
                    )
                    loginAccountRB.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.dividerColor
                        )
                    )
                }
                R.id.createAccountRB -> {
                    loginAccountLayout.visibility = View.GONE
                    createAccountLayout.visibility = View.VISIBLE
                    loginAccountRB.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.white
                        )
                    )
                    createAccountRB.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!.applicationContext,
                            R.color.dividerColor
                        )
                    )
                }
            }
        }

        view.createAccountButton.setOnClickListener {
            val customerName = view.customerNameET.text.toString().trim()
            val customerPhone = view.customerNumberET.text.toString().trim()
            val customerEmail = view.customerEmailET.text.toString().trim()
            val customerAddress = view.customerAddressET.text.toString().trim()

            if (!customerName.nonEmpty {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Name $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerNameET.requestFocus()
                } || !customerPhone.nonEmpty {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Phone $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerNumberET.requestFocus()
                } || !customerPhone.onlyNumbers {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Phone $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerNumberET.requestFocus()
                } || !customerPhone.minLength(10, callback = {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Phone $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerNumberET.requestFocus()
                }) || !customerEmail.nonEmpty {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Email $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerEmailET.requestFocus()
                } || !customerEmail.validEmail {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Email $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerEmailET.requestFocus()
                } || !customerAddress.nonEmpty {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Customer Address $it",
                        Toast.LENGTH_SHORT
                    ).show()
                    view.customerAddressET.requestFocus()
                }) {
            } else {
                Toast.makeText(activity!!.applicationContext, "All right", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return view
    }

}