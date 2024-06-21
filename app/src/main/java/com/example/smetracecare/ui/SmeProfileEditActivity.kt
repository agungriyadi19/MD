package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.smetracecare.databinding.ActivitySmeProfileEditBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.SmeProfileEditViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import com.example.smetracecare.data.dataStore

import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.UpdateProfileRequest

class SmeProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmeProfileEditBinding
    private val preferences = SharedPreferences.getInstance(dataStore)
    private val smeProfileEditViewModel: SmeProfileEditViewModel by lazy {
        ViewModelProvider(this)[SmeProfileEditViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmeProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SmeProfileActivity::class.java))
        }

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) { token ->
            dataStoreViewModel.getUserID().observe(this) { userId ->
                setupUI(token, userId)
                smeProfileEditViewModel.getProfile(token, userId)
            }
        }

        observeViewModel()
    }

    private fun setupUI(token: String, userId: String) {
        binding.apply {
            addProductButton.setOnClickListener {
                val name = edAddSmeName.text.toString().trim()
                val email = edAddSmeEmail.text.toString().trim()
                val phone = edAddSmePhone.text.toString().trim()
                val address = edAddSmeAddress.text.toString().trim()
                val description = edAddSmeDesc.text.toString().trim()

                if (name.isNotEmpty() && email.isNotEmpty() && phone.isNotEmpty() && address.isNotEmpty() && description.isNotEmpty()) {
                    val request = UpdateProfileRequest(name, email, phone, address, description)
                    smeProfileEditViewModel.updateProfile(token, userId, request)
                } else {
                    Toast.makeText(this@SmeProfileEditActivity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }

            btnBack.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        smeProfileEditViewModel.apply {
            loading.observe(this@SmeProfileEditActivity) { isLoading ->
                // Show/hide loading indicator
            }

            message.observe(this@SmeProfileEditActivity) { message ->
                Toast.makeText(this@SmeProfileEditActivity, message, Toast.LENGTH_SHORT).show()
            }

            Profile.observe(this@SmeProfileEditActivity) { profile ->
                profile?.let {
                    binding.edAddSmeName.setText(it.loginResult?.name)
                    binding.edAddSmeEmail.setText(it.loginResult?.email)
                    binding.edAddSmePhone.setText(it.loginResult?.phoneNumber)
                    binding.edAddSmeAddress.setText(it.loginResult?.address)
                    binding.edAddSmeDesc.setText(it.loginResult?.description)
                }
            }

            updateResponse.observe(this@SmeProfileEditActivity) { response ->
                if (response != null) {
                    Toast.makeText(this@SmeProfileEditActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

}