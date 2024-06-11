package com.example.smetracecare.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.example.smetracecare.data.GetMaterial
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.databinding.ActivitySupplierProductDetailBinding

@Suppress("DEPRECATION")
class SupplierProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<MaterialDetail>(EXTRA_DATA) as MaterialDetail
        Log.d("detailStory", detailStory.toString())
        setStory(detailStory)

        binding.btnBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SupplierProductDetailActivity, SupplierHomeActivity::class.java))
        })
    }

    private fun setStory(detailStory: MaterialDetail) {
        Glide.with(this)
            .load(detailStory.image)
            .into(binding.ivDetailPhoto)

        binding.apply {
            tvDetailName.text = detailStory.name
            descDetail.text = detailStory.description
            tvDetailDate.text = detailStory.price
        }
    }

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
    }
}