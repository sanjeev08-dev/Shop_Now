package com.sanjeevdev.shopnow.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.Result
import com.sanjeevdev.shopnow.R
import com.sanjeevdev.shopnow.activities.ProductDetailActivity
import com.sanjeevdev.shopnow.utils.Constants
import kotlinx.android.synthetic.main.fragment_q_r_scanner.*
import kotlinx.android.synthetic.main.fragment_q_r_scanner.view.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QRScannerFragment : Fragment(), ZXingScannerView.ResultHandler {
    private lateinit var scanner:ZXingScannerView
    var isFlashOn = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_q_r_scanner, container, false)
        scanner = view.findViewById(R.id.scanner)
        view.flashButton.setOnClickListener {
            if (isFlashOn){
                scanner.flash = false
                view.flashButton.setImageResource(R.drawable.ic_flash_on)
                isFlashOn = false
            }else{
                scanner.flash = true
                view.flashButton.setImageResource(R.drawable.ic_flash_off)
                isFlashOn = true
            }
        }
        view.reScanButton.setOnClickListener {
            scanner.setResultHandler(this)
            scanner.startCamera()
            scanner.setAutoFocus(true)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(requireActivity().applicationContext,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.CAMERA),
                    12
                )
            }
        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10 && grantResults.isEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity().applicationContext, "Permission denied WRITE", Toast.LENGTH_SHORT).show()
        } else {
            scanner.setResultHandler(this)
            scanner.startCamera()
            scanner.setAutoFocus(true)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scanner.stopCamera()
        scanner.setAutoFocus(false)
    }

    override fun handleResult(rawResult: Result?) {
        val productID = rawResult!!.text!!
        scanner.stopCamera()
        scanner.setAutoFocus(false)

        FirebaseFirestore.getInstance().collection(Constants.PRODUCTS).whereEqualTo(Constants.PRODUCTID,productID)
            .get()
            .addOnCompleteListener {
                if (!it.result!!.isEmpty){
                    reScanButton.visibility = View.GONE
                    val intent = Intent(requireActivity(), ProductDetailActivity::class.java)
                    intent.putExtra(Constants.PRODUCTID,productID)
                    startActivity(intent)
                }else{
                    reScanButton.visibility = View.VISIBLE
                    errorText.text = "Invalid QR Code Meta Data"
                }
            }
    }

    override fun onResume() {
        super.onResume()
        scanner.setResultHandler (this)
        scanner.startCamera()
        scanner.setAutoFocus(true)

    }

}