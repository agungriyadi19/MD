package com.example.smetracecare.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.smetracecare.R
import com.example.smetracecare.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    lateinit var role: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.vlAdmin.setOnClickListener{
            role = "Admin"
        }
        binding.vlSupplier.setOnClickListener{
            role = "Supplier"
        }
        binding.vlSme.setOnClickListener{
            role = "SME"
        }
        binding.vlCustomer.setOnClickListener{
            role = "Customer"
        }

        binding.startedButton.setOnClickListener {
            if(role != "Customer") {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                // Custommer Page
            }
        }
    }
}