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
import kotlinx.android.synthetic.main.fragment_account.*

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

        if (R.id.loginAccountRB == accountRG.checkedRadioButtonId){
            loginAccountLayout.visibility = View.VISIBLE
            createAccountLayout.visibility = View.GONE
            createAccountRB.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext,R.color.white))
            loginAccountRB.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext,R.color.dividerColor))
        }
        accountRG.setOnCheckedChangeListener { radioGroup, i ->
            when (i){
                R.id.loginAccountRB -> {
                    loginAccountLayout.visibility = View.VISIBLE
                    createAccountLayout.visibility = View.GONE
                    createAccountRB.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext,R.color.white))
                    loginAccountRB.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext,R.color.dividerColor))
                }
                R.id.createAccountRB ->{
                    loginAccountLayout.visibility = View.GONE
                    createAccountLayout.visibility = View.VISIBLE
                    loginAccountRB.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext,R.color.white))
                    createAccountRB.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext,R.color.dividerColor))
                }
            }
        }
        return view
    }
}