package com.example.smetracecare.ui

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivityWelcomeBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.ViewModelFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = SharedPreferences.getInstance(dataStore)
        val dataStoreViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getRole().observe(this) { data ->
            role = data
        }

        binding.vlSupplier.setOnClickListener {
            dataStoreViewModel.saveRole("supplier")

            val spannableString = SpannableString(binding.tvSupplier.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvSupplier.text = spannableString

            binding.tvSme.text = binding.tvSme.text.toString()
            binding.tvCustomer.text = binding.tvCustomer.text.toString()

        }
        binding.vlSme.setOnClickListener {
            dataStoreViewModel.saveRole("umkm")

            val spannableString = SpannableString(binding.tvSme.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvSme.text = spannableString

            binding.tvSupplier.text = binding.tvSupplier.text.toString()
            binding.tvCustomer.text = binding.tvCustomer.text.toString()

        }
        binding.vlCustomer.setOnClickListener {
            dataStoreViewModel.saveRole("Customer")

            val spannableString = SpannableString(binding.tvCustomer.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvCustomer.text = spannableString

            binding.tvSme.text = binding.tvSme.text.toString()
            binding.tvSupplier.text = binding.tvSupplier.text.toString()

        }

        binding.startedButton.setOnClickListener {
            if (role != "Customer") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Customer Page
                val intent = Intent(this, ScanConsumerActivity::class.java)
                startActivity(intent)
            }
        }
    }
}