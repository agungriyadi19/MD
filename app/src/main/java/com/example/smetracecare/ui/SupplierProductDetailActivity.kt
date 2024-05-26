package com.example.smetracecare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smetracecare.databinding.ActivitySupplierProductDetailBinding

@Suppress("DEPRECATION")
class SupplierProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}