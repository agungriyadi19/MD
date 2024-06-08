package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smetracecare.R
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivitySupplierHomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class SupplierHomeActivity : AppCompatActivity() {
    private val preferences = SharedPreferences.getInstance(dataStore)

    private lateinit var binding: ActivitySupplierHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicked()

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getName().observe(this) { data ->
            binding.apply {
                tvSupplierName.text = data
            }
        }
    }

    private fun onClicked() {
        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, SupplierProductAddActivity::class.java))
        }

        binding.btnProfile.setOnClickListener() {
            startActivity(Intent(this, SupplierProfileActivity::class.java))
        }
    }

}