package com.example.smetracecare.ui

import android.os.Bundle
import android.content.Intent
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent =Intent(this, WelcomeActivity::class.java)

            binding.logo.animate()
                .setDuration(3000)
                .alpha(0f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }

            binding.appName.animate()
                .setDuration(3000)
                .alpha(0f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }

        }
    }