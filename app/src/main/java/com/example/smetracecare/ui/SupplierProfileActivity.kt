package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smetracecare.databinding.ActivitySupplierProfileBinding

class SupplierProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SupplierHomeActivity::class.java))
        }

        binding.editProfileButton.setOnClickListener() {
            startActivity(Intent(this, SupplierProfileEditActivity::class.java))
        }

    }

}