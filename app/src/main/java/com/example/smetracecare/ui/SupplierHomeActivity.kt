package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smetracecare.databinding.ActivitySupplierHomeBinding

class SupplierHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicked()

        binding.btnProfile.setOnClickListener() {
            startActivity(Intent(this, SupplierProfileActivity::class.java))
        }
    }

    private fun onClicked() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SupplierProductAddActivity::class.java))
        }
    }

}