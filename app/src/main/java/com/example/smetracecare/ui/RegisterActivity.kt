package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smetracecare.R
import com.example.smetracecare.data.DataRegister
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivityRegisterBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import com.example.smetracecare.viewModel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {
    private val preferences = SharedPreferences.getInstance(dataStore)
    private lateinit var role: String
    private lateinit var binding: ActivityRegisterBinding
    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProvider(this)[RegisterViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getRole().observe(this) { data ->
            role = data
            if (data == "supplier") {
                Glide.with(this)
                    .load(R.drawable.supplier_icon)
                    .into(binding.logo)
                binding.apply {
                    register.text = getString(R.string.register_supplier)
                }
            }
            if (data == "umkm") {
                Glide.with(this)
                    .load(R.drawable.umkm_icon)
                    .into(binding.logo)
                binding.apply {
                    register.text = getString(R.string.register_sme)
                }
            }
        }

        onClicked()

        registerViewModel.message.observe(this) { msg ->
            registerResponse(registerViewModel.isError, msg)
        }

        registerViewModel.loading.observe(this) {
            onLoading(it)
        }
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun registerResponse(error: Boolean, msg: String) {
        if (!error) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = DataRegister(
                binding.edRegisterName.text.toString(),
                binding.edRegisterEmail.text.toString(),
                binding.edRegisterPassword.text.toString(),
                role,
                binding.edRegisterDesc.text.toString(),
                binding.edRegisterAddress.text.toString(),
                binding.edRegisterPhone.text.toString(),
                binding.edRegisterPasswordConfirm.text.toString()
            )
            registerViewModel.getRegisterResponse(user)

        } else {
            val responseTV = binding.result
            responseTV.text = msg
            responseTV.visibility = View.VISIBLE
            Toast.makeText(this, msg, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun onClicked() {
        binding.buttonBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.showPassword.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                binding.edRegisterPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.edRegisterPasswordConfirm.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.edRegisterPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.edRegisterPasswordConfirm.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
            binding.edRegisterPassword.text?.let { binding.edRegisterPassword.setSelection(it.length) }
            binding.edRegisterPasswordConfirm.text?.let {
                binding.edRegisterPasswordConfirm.setSelection(
                    it.length
                )
            }
        }

        binding.registerButton.setOnClickListener {
            binding.apply {
                edRegisterName.clearFocus()
                edRegisterEmail.clearFocus()
                edRegisterPassword.clearFocus()
                edRegisterPasswordConfirm.clearFocus()
            }

            val user = DataRegister(
                binding.edRegisterName.text.toString(),
                binding.edRegisterEmail.text.toString(),
                binding.edRegisterPassword.text.toString(),
                role,
                binding.edRegisterDesc.text.toString(),
                binding.edRegisterAddress.text.toString(),
                binding.edRegisterPhone.text.toString(),
                binding.edRegisterPasswordConfirm.text.toString()
            )
            Log.d("data regis", user.toString())
            registerViewModel.getRegisterResponse(user)
        }
    }
}