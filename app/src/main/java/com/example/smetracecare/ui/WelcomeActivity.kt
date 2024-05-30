package com.example.smetracecare.ui

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    lateinit var role: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vlAdmin.setOnClickListener {
            role = "Admin"
            val spannableString = SpannableString(binding.tvAdmin.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvAdmin.text = spannableString

            binding.tvSupplier.text = binding.tvSupplier.text.toString()
            binding.tvSme.text = binding.tvSme.text.toString()
            binding.tvCustomer.text = binding.tvCustomer.text.toString()

        }
        binding.vlSupplier.setOnClickListener {
            role = "Supplier"

            val spannableString = SpannableString(binding.tvSupplier.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvSupplier.text = spannableString

            binding.tvAdmin.text = binding.tvAdmin.text.toString()
            binding.tvSme.text = binding.tvSme.text.toString()
            binding.tvCustomer.text = binding.tvCustomer.text.toString()

        }
        binding.vlSme.setOnClickListener {
            role = "SME"

            val spannableString = SpannableString(binding.tvSme.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvSme.text = spannableString

            binding.tvAdmin.text = binding.tvAdmin.text.toString()
            binding.tvSupplier.text = binding.tvSupplier.text.toString()
            binding.tvCustomer.text = binding.tvCustomer.text.toString()

        }
        binding.vlCustomer.setOnClickListener {
            role = "Customer"

            val spannableString = SpannableString(binding.tvCustomer.text)
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                spannableString.length,
                0
            ) // Add bold style
            binding.tvCustomer.text = spannableString

            binding.tvAdmin.text = binding.tvAdmin.text.toString()
            binding.tvSme.text = binding.tvSme.text.toString()
            binding.tvSupplier.text = binding.tvSupplier.text.toString()

        }

        binding.startedButton.setOnClickListener {
            if (role != "Customer") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Custommer Page
            }
        }
    }
}