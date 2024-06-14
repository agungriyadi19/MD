package com.example.smetracecare.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivitySmeMaterialDetailBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.MaterialDeleteViewModel
import com.example.smetracecare.viewModel.SupplierProfileViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Suppress("DEPRECATION")
class SmeMaterialDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmeMaterialDetailBinding
    private val supplierProfileViewModel: SupplierProfileViewModel by lazy {
        ViewModelProvider(this)[SupplierProfileViewModel::class.java]
    }
    private val preferences = SharedPreferences.getInstance(dataStore)
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmeMaterialDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        val detailStory = intent.getParcelableExtra<MaterialDetail>(EXTRA_DATA) as MaterialDetail
        setStory(detailStory)

        binding.btnBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SmeMaterialDetailActivity, SupplierHomeFragment::class.java))
        })
        var supplierId = detailStory.supplierId.substringAfter('-')
        Log.d("supplierId", supplierId)
        dataStoreViewModel.getToken().observe(this) { token ->
            supplierProfileViewModel.getSupplierProfileResponse(token, supplierId)
        }
        observeUserProfile()

        binding.buttonOrder.setOnClickListener {

            val phoneNumber = "6285155115494"
            val message = "Hallo Estetik 🙂"

            // Create intent to open WhatsApp with the provided phone number and message
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val url = "https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}"
                data = Uri.parse(url)
            }

            // Verify that there is an activity to handle the intent before starting it
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                startActivity(intent)
                // Handle scenario where WhatsApp or a suitable app is not installed
                // You can display a message or fallback to another method
            }
        }
    }

    private fun observeUserProfile() {
        supplierProfileViewModel.userProfile.observe(this) { user ->
            Log.d("data user", user.toString())
            binding.apply {
                if (user != null) {
                    tvDataNameSupplier.text = user.result!!.name
                    tvDataEmail.text = user.result!!.email
                    tvDataDescriptionSupplier.text = user.result!!.description
                    tvDataPhone.text = user.result!!.phoneNumber
                    tvDataAddress.text = user.result!!.address
                }
            }
        }
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
        const val EXTRA_MATERIAL_ID = "EXTRA_MATERIAL_ID"
    }
}