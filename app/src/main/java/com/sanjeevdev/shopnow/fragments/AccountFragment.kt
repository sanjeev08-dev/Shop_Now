package com.sanjeevdev.shopnow.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.utils.Constants
import com.wajahatkarim3.easyvalidation.core.view_ktx.minLength
import com.wajahatkarim3.easyvalidation.core.view_ktx.nonEmpty
import com.wajahatkarim3.easyvalidation.core.view_ktx.onlyNumbers
import com.wajahatkarim3.easyvalidation.core.view_ktx.validEmail
import kotlinx.android.synthetic.main.fragment_account.view.*
import java.util.concurrent.TimeUnit

class AccountFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)!!
        view.loginAccountButton.saveInitialState()
        view.createAccountButton.saveInitialState()

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
                verifyNumber(view,customerPhone,customerName,customerEmail,customerAddress)
            }
        }
        view.loginAccountButton.setOnClickListener {
            val customerPhone = view.customerLoginNumberET.text.toString().trim()

            if (!customerPhone.nonEmpty {
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
                })) {
            } else {
                FirebaseFirestore.getInstance().collection(Constants.CUSTOMER_COLLECTION).whereEqualTo(Constants.CUSTOMER_PHONE,"+91$customerPhone").get()
                    .addOnCompleteListener {
                        if (!it.result!!.isEmpty){
                            verifyNumber(view, customerPhone, "", "", "")
                        }else{
                            Toast.makeText(activity!!.applicationContext, "Please create account first!!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        return view
    }

    private fun verifyNumber(
        view: View,
        customerPhone: String,
        customerName: String?,
        customerEmail: String?,
        customerAddress: String?
    ) {
        val phoneNumber = "+91$customerPhone"
        view.createAccountButton.startAnimation()
        view.loginAccountButton.startAnimation()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber,
            60,
            TimeUnit.SECONDS,
            activity!!,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    view.createAccountButton.revertAnimation()
                    view.loginAccountButton.revertAnimation()
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Toast.makeText(activity!!.applicationContext, p0.message, Toast.LENGTH_SHORT).show()
                    Log.e("My Error",p0.localizedMessage)
                    view.createAccountButton.revertAnimation()
                    view.loginAccountButton.revertAnimation()
                }

                override fun onCodeSent(
                    code: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    val otpFragment = OTPFragment()
                    val args = Bundle()
                    args.putString(Constants.CUSTOMER_NAME,customerName)
                    args.putString(Constants.CUSTOMER_PHONE,customerPhone)
                    args.putString(Constants.CUSTOMER_EMAIL,customerEmail)
                    args.putString(Constants.CUSTOMER_ADDRESS,customerAddress)
                    args.putString(Constants.VERIFICATION_ID,code)
                    otpFragment.arguments = args
                    view.createAccountButton.revertAnimation()
                    view.loginAccountButton.revertAnimation()
                    activity!!.supportFragmentManager.beginTransaction().replace(R.id.cartContainer,otpFragment,"Fragment Open").commit()
                }
            }
        )

    }

}