package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivitySupplierProfileBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.SupplierProfileViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class SupplierProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProfileBinding
    private val preferences = SharedPreferences.getInstance(dataStore)
    private val supplierProfileViewModel: SupplierProfileViewModel by lazy {
        ViewModelProvider(this)[SupplierProfileViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClicked()

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) { token ->
            dataStoreViewModel.getUserID().observe(this) { userId ->
                supplierProfileViewModel.getSupplierProfileResponse(token, userId)
            }
        }
        observeUserProfile()
    }

    private fun observeUserProfile() {
        supplierProfileViewModel.userProfile.observe(this) { user ->
            Log.d("data user", user.toString())
            binding.apply {
                if (user != null) {
                    tvDataName.text = user.result!!.name
                    tvDataEmail.text = user.result!!.email
                    tvDataDescription.text = user.result!!.description
                    tvDataPhone.text = user.result!!.phoneNumber
                    tvDataAddress.text = user.result!!.address
                }
            }
        }
    }

    private fun onClicked() {
        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SupplierHomeActivity::class.java))
        }

        binding.editProfileButton.setOnClickListener() {
            Log.d("test", "test")
            startActivity(Intent(this, SupplierProfileEditActivity::class.java))
        }
    }
}