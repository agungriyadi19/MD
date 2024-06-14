package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smetracecare.databinding.ActivitySupplierProfileEditBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.SupplierProfileEditViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import com.example.smetracecare.data.dataStore

import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.UpdateProfileRequest

class SupplierProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProfileEditBinding
    private val preferences = SharedPreferences.getInstance(dataStore)
    private val supplierProfileEditViewModel: SupplierProfileEditViewModel by lazy {
        ViewModelProvider(this)[SupplierProfileEditViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SupplierProfileActivity::class.java))
        }

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) { token ->
            dataStoreViewModel.getUserID().observe(this) { userId ->
                setupUI(token, userId)
                supplierProfileEditViewModel.getSupplierProfile(token, userId)
            }
        }

        observeViewModel()
    }

    private fun setupUI(token: String, userId: String) {
        binding.apply {
            addProductButton.setOnClickListener {
                val name = edAddSupplierName.text.toString().trim()
                val email = edAddSupplierEmail.text.toString().trim()
                val phone = edAddSupplierPhone.text.toString().trim()
                val address = edAddSupplierAddress.text.toString().trim()
                val description = edAddSupplierDesc.text.toString().trim()

                if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && description.isNotEmpty()) {
                    val request = UpdateProfileRequest(name, email, phone, address, description)
                    supplierProfileEditViewModel.updateSupplierProfile(token, userId, request)
                } else {
                    Toast.makeText(this@SupplierProfileEditActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        supplierProfileEditViewModel.apply {
            loading.observe(this@SupplierProfileEditActivity) { isLoading ->
                // Show/hide loading indicator
            }

            message.observe(this@SupplierProfileEditActivity) { message ->
                Toast.makeText(this@SupplierProfileEditActivity, message, Toast.LENGTH_SHORT).show()
            }

            supplierProfile.observe(this@SupplierProfileEditActivity) { profile ->
                profile?.let {
                    binding.edAddSupplierName.setText(it.result?.name)
                    binding.edAddSupplierEmail.setText(it.result?.email)
                    binding.edAddSupplierPhone.setText(it.result?.phoneNumber)
                    binding.edAddSupplierAddress.setText(it.result?.address)
                    binding.edAddSupplierDesc.setText(it.result?.description)
                }
            }

            updateResponse.observe(this@SupplierProfileEditActivity) { response ->
                if (response != null) {
                    Toast.makeText(this@SupplierProfileEditActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

}