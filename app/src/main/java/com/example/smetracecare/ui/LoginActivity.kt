package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.smetracecare.R
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivityLoginBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import com.bumptech.glide.Glide

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferences = SharedPreferences.getInstance(dataStore)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getRole().observe(this) { data ->
            if (data == "supplier") {
                Glide.with(this)
                    .load(R.drawable.supplier_icon)
                    .into(binding.logo)
                binding.apply {
                    login.text = getString(R.string.login_supplier)
                }
            }
            if (data == "umkm") {
                Glide.with(this)
                    .load(R.drawable.umkm_icon)
                    .into(binding.logo)
                binding.apply {
                    login.text = getString(R.string.login_sme)
                }
            }
        }
        onClicked()
    }

    private fun onClicked() {

        binding.buttonRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.showPassword.setOnCheckedChangeListener { _, checked ->
            binding.edLoginPassword.transformationMethod = if (checked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            binding.edLoginPassword.text?.let { binding.edLoginPassword.setSelection(it.length) }
        }

        binding.loginButton.setOnClickListener {
            binding.edLoginEmail.clearFocus()
            binding.edLoginPassword.clearFocus()

            val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
            dataStoreViewModel.getRole().observe(this) { data ->
                if (data == "supplier") {
                    val intent = Intent(this@LoginActivity, SupplierHomeActivity::class.java)
                    startActivity(intent)
                }
                if (data == "umkm") {
                    // UMKM Page
                    val intent = Intent(this@LoginActivity, SupplierHomeActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}