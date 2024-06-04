package com.example.smetracecare.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivityResultScanQrBinding

class ResultScanQrActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanQrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val content = intent.getStringExtra("CONTENT")

        binding.result.text = content
    }
}
