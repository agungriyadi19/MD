package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            val intent = Intent(this@LoginActivity, SupplierHomeActivity::class.java)
            startActivity(intent)
        }
    }
}