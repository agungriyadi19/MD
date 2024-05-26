package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
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