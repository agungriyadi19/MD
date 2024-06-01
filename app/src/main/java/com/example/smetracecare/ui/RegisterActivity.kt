package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smetracecare.R
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivityRegisterBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private val preferences = SharedPreferences.getInstance(dataStore)

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getRole().observe(this) { data ->
            if (data == "Supplier") {
                Glide.with(this)
                    .load(R.drawable.supplier_icon)
                    .into(binding.logo)
                binding.apply {
                    register.text = getString(R.string.register_supplier)
                }
            }
            if (data == "SME") {
                Glide.with(this)
                    .load(R.drawable.umkm_icon)
                    .into(binding.logo)
                binding.apply {
                    register.text = getString(R.string.register_sme)
                }
            }
        }

        onClicked()
    }

    private fun onClicked() {
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.showPassword.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                binding.edRegisterPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.edRegisterPasswordConfirm.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.edRegisterPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.edRegisterPasswordConfirm.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.edRegisterPassword.text?.let { binding.edRegisterPassword.setSelection(it.length) }
            binding.edRegisterPasswordConfirm.text?.let { binding.edRegisterPasswordConfirm.setSelection(it.length) }
        }

        binding.registerButton.setOnClickListener {
            binding.apply {
                edRegisterName.clearFocus()
                edRegisterEmail.clearFocus()
                edRegisterPassword.clearFocus()
                edRegisterPasswordConfirm.clearFocus()
            }
        }
    }
}