package com.sanjeevdev.shopnow.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.fragment_o_t_p.view.*

class OTPFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_o_t_p, container, false)!!

        val customerName = arguments!!.getString(Constants.CUSTOMER_NAME)!!
        val customerPhone = arguments!!.getString(Constants.CUSTOMER_PHONE)!!
        val customerEmail = arguments!!.getString(Constants.CUSTOMER_EMAIL)!!
        val customerAddress = arguments!!.getString(Constants.CUSTOMER_ADDRESS)!!
        val verificationID = arguments!!.getString(Constants.VERIFICATION_ID)!!

        setupOTPInputs(view)

        view.verifyOTPButton.setOnClickListener {
            if (view.inputCode1.text.toString().trim().isEmpty() ||
                view.inputCode2.text.toString().trim().isEmpty() ||
                view.inputCode3.text.toString().trim().isEmpty() ||
                view.inputCode4.text.toString().trim().isEmpty() ||
                view.inputCode5.text.toString().trim().isEmpty() ||
                view.inputCode6.text.toString().trim().isEmpty()
            ){
                Toast.makeText(activity!!.applicationContext, "Please enter valid code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            view.verifyOTPButton.startAnimation()
            val code = view.inputCode1.text.toString().trim()+
                    view.inputCode2.text.toString().trim()+
                    view.inputCode3.text.toString().trim()+
                    view.inputCode4.text.toString().trim()+
                    view.inputCode5.text.toString().trim()+
                    view.inputCode6.text.toString().trim()

            if (verificationID != null){
                val phoneAuthCredential = PhoneAuthProvider.getCredential(verificationID,code)
                FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener {
                    if (it.isSuccessful){
                        val customerID = FirebaseAuth.getInstance().currentUser!!.uid
                        val map = hashMapOf(
                            Constants.CUSTOMER_NAME to customerName,
                            Constants.CUSTOMER_PHONE to customerPhone,
                            Constants.CUSTOMER_EMAIL to customerEmail,
                            Constants.CUSTOMER_ADDRESS to customerAddress,
                            "customerID" to customerID
                        )
                        FirebaseFirestore.getInstance().collection(Constants.CUSTOMER_COLLECTION).document(customerID).set(map).addOnSuccessListener {
                            view.verifyOTPButton.revertAnimation()
                            Toast.makeText(activity!!.applicationContext, "Success", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {exception ->
                            view.verifyOTPButton.revertAnimation()
                            Toast.makeText(activity!!.applicationContext, "Failed ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        view.verifyOTPButton.revertAnimation()
                        Toast.makeText(activity!!.applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return view
    }

    private fun setupOTPInputs(view: View) {
        view.inputCode1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    view.inputCode2.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        view.inputCode2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    view.inputCode3.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        view.inputCode3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    view.inputCode4.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        view.inputCode4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    view.inputCode5.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        view.inputCode5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s.toString().trim().isNotEmpty()) {
                    view.inputCode6.requestFocus()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}