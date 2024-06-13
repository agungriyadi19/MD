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
import com.example.smetracecare.R
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivityLoginBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import com.bumptech.glide.Glide
import com.example.smetracecare.data.DataLogin
import com.example.smetracecare.viewModel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferences = SharedPreferences.getInstance(dataStore)
    private lateinit var role: String
    private val loginViewModel: LoginViewModel by lazy {
        ViewModelProvider(this)[LoginViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onClicked()

        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getRole().observe(this) { data ->
            role = data
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
                    .load(R.drawable.umkm_icon2)
                    .into(binding.logo)
                binding.apply {
                    login.text = getString(R.string.login_sme)
                }
            }
        }
        loginViewModel.message.observe(this) { msg ->
            loginResponse(loginViewModel.isError, msg, dataStoreViewModel)
        }

        loginViewModel.loading.observe(this) {
            onLoading(it)
        }
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

            if (dataValid()) {
                val requestLogin = DataLogin(
                    binding.edLoginEmail.text.toString().trim(),
                    binding.edLoginPassword.text.toString().trim(),
                    role
                )
                loginViewModel.getLoginResponse(requestLogin)

            } else {
                if (!binding.edLoginEmail.emailValid) binding.edLoginEmail.error =
                    getString(R.string.noEmail)
                if (!binding.edLoginPassword.passwordValid) binding.edLoginPassword.error =
                    getString(R.string.noPass)

                Toast.makeText(this, R.string.invalid_login, Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun dataValid(): Boolean {
        return binding.edLoginEmail.emailValid && binding.edLoginPassword.passwordValid
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun String.extractUserId(): String {
        return this.substringAfter("user-")
    }
    private fun loginResponse(error: Boolean, msg: String, userViewModel: DataStoreViewModel) {
        if (!error) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            val user = loginViewModel.userLogin.value
            userViewModel.saveLogin(true)
            user?.result!!.token.let { userViewModel.saveToken("Bearer $it") }
            user.result!!.userId.let {
                userViewModel.saveUserID(it.extractUserId())
            }

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
        } else {
            Log.d("msg", msg)
            Log.d("response", userViewModel.toString())
            val responseTV = binding.result
            responseTV.text = msg
            responseTV.visibility = View.VISIBLE
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }
}