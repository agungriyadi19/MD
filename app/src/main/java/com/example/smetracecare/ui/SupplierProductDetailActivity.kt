package com.example.smetracecare.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import com.bumptech.glide.Glide
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.databinding.ActivitySupplierProductDetailBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Suppress("DEPRECATION")
class SupplierProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detailStory = intent.getParcelableExtra<MaterialDetail>(EXTRA_DATA) as MaterialDetail
        setStory(detailStory)

        binding.editButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SupplierProductDetailActivity, SupplierProductEditActivity::class.java))
        })

        binding.btnBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SupplierProductDetailActivity, SupplierHomeActivity::class.java))
        })
    }

    private fun setStory(detailStory: MaterialDetail) {
        Glide.with(this)
            .load(decodeBase64ToBitmap(detailStory.image))
            .into(binding.ivDetailPhoto)

        binding.apply {
            tvDataName.text = detailStory.name
            tvDataType.text = detailStory.type
            tvDataPrice.text = formatRupiah(detailStory.price.toDouble())
            tvDataDescription.text = detailStory.description
        }
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedString = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }

    private fun formatRupiah(number: Double): String {
        val symbols = DecimalFormatSymbols(Locale("in", "ID"))
        symbols.currencySymbol = "Rp "
        symbols.monetaryDecimalSeparator = ','
        symbols.groupingSeparator = '.'

        val decimalFormat = DecimalFormat("#,###", symbols)
        return decimalFormat.format(number)
    }
    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
    }
}