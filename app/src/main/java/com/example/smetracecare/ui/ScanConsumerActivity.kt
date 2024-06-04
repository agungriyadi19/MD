package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.data.cameraPermissionRequest
import com.example.smetracecare.data.isPermissionGranted
import com.example.smetracecare.data.openPermissionSetting
import com.example.smetracecare.databinding.ActivityScanConsumerBinding
import com.google.mlkit.vision.barcode.common.Barcode

class ScanConsumerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanConsumerBinding
    private val cameraPermission = android.Manifest.permission.CAMERA

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                //start the scanner or camera
                startScanner()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanConsumerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.scanButtonBarcode.setOnClickListener(){
            Log.d("halo", "hallo")

            requestCameraAndStartScanner()
        }

        binding.tvScanButtonBarcode.setOnClickListener(){
            requestCameraAndStartScanner()
        }

        binding.cancelScan.setOnClickListener(){
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }

    private fun requestCameraAndStartScanner() {
        Log.d("izin kamera", isPermissionGranted(cameraPermission).toString())
        if (isPermissionGranted(cameraPermission)) {
            //start the scanner or camera
            startScanner()
        } else {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        when {
            shouldShowRequestPermissionRationale(cameraPermission) -> cameraPermissionRequest { openPermissionSetting() }
            else -> requestPermissionLauncher.launch(cameraPermission)
        }
    }

    private fun startScanner() {
        ScannerActivity.startScanner(this) { barcodes ->
            barcodes.forEach { barcode ->
                val type: String
                val content: String

                when (barcode.valueType) {
                    Barcode.TYPE_URL -> {
                        type = "URL"
                        content = barcode.url?.url.toString()
                    }

                    Barcode.TYPE_CONTACT_INFO -> {
                        type = "Contact"
                        content = barcode.contactInfo.toString()
                    }

                    Barcode.TYPE_WIFI -> {
                        type = "Wifi"
                        content =
                            "Network Name: ${barcode.wifi?.ssid}\nPassword: ${barcode.wifi?.password}"
                    }

                    Barcode.TYPE_TEXT -> {
                        type = "Other"
                        content = barcode.rawValue.toString()
                    }

                    else -> {
                        type = "Other"
                        content = barcode.rawValue.toString()
                    }
                }

                // Start the next activity
                val intent = Intent(this, ResultScanQrActivity::class.java).apply {
                    putExtra("TYPE", type)
                    putExtra("CONTENT", content)
                }
                startActivity(intent)
            }
        }
    }
}
