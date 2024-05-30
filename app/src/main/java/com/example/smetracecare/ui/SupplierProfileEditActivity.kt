package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smetracecare.databinding.ActivitySupplierProfileEditBinding

class SupplierProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProfileEditBinding
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SupplierProfileActivity::class.java))
        }
    }

}