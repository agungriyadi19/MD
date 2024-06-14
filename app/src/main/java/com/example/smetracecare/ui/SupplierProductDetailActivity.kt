package com.example.smetracecare.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivitySupplierProductDetailBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.MaterialDeleteViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Suppress("DEPRECATION")
class SupplierProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductDetailBinding
    private val materialDeleteViewModel: MaterialDeleteViewModel by lazy {
        ViewModelProvider(this)[MaterialDeleteViewModel::class.java]
    }
    private val preferences = SharedPreferences.getInstance(dataStore)
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        val detailStory = intent.getParcelableExtra<MaterialDetail>(EXTRA_DATA) as MaterialDetail
        setStory(detailStory)

        binding.editButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SupplierProductDetailActivity, SupplierProductEditActivity::class.java)
            intent.putExtra(EXTRA_DATA, detailStory) // Meneruskan materialId ke SupplierProductEditActivity
            startActivity(intent)
        })

        binding.btnBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SupplierProductDetailActivity, SupplierHomeFragment::class.java))
        })
        var materialId = detailStory.materialId.substringAfter('-')
        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmationDialog(materialId)
        }
    }

    private fun showDeleteConfirmationDialog(materialId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Penghapusan")
            .setMessage("Apakah Anda yakin ingin menghapus?")
            .setPositiveButton("Ya") { dialog, which ->
                // Aksi penghapusan di sini
                materialDeleteViewModel.DeleteMaterial(token, materialId)
                val intent = Intent(this, SupplierHomeFragment::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
            }
            .show()
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