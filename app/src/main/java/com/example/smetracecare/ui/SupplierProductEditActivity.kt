package com.example.smetracecare.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smetracecare.databinding.ActivitySupplierProductAddBinding

@Suppress("DEPRECATION")
class SupplierProductEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}