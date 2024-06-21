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
import com.example.smetracecare.data.PembatikDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.databinding.ActivitySmePembatikDetailBinding
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.PembatikDeleteViewModel
import com.example.smetracecare.viewModel.ViewModelFactory


@Suppress("DEPRECATION")
class SmePembatikDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmePembatikDetailBinding
    private val pembatikDeleteViewModel: PembatikDeleteViewModel by lazy {
        ViewModelProvider(this)[PembatikDeleteViewModel::class.java]
    }
    private val preferences = SharedPreferences.getInstance(dataStore)
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmePembatikDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            token = it
        }

        val detailStory = intent.getParcelableExtra<PembatikDetail>(EXTRA_DATA) as PembatikDetail
        setStory(detailStory)

        binding.editButton.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@SmePembatikDetailActivity, SmePembatikEditActivity::class.java)
            intent.putExtra(EXTRA_DATA, detailStory) // Meneruskan pembatikId ke SmePembatikEditActivity
            startActivity(intent)
        })

        binding.btnBack.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@SmePembatikDetailActivity, SupplierHomeFragment::class.java))
        })
        var pembatikId = detailStory.profileId.substringAfter('-')
        binding.buttonDelete.setOnClickListener {
            showDeleteConfirmationDialog(pembatikId)
        }
    }

    private fun showDeleteConfirmationDialog(pembatikId: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Penghapusan")
            .setMessage("Apakah Anda yakin ingin menghapus?")
            .setPositiveButton("Ya") { dialog, which ->
                // Aksi penghapusan di sini
                pembatikDeleteViewModel.DeletePembatik(token, pembatikId)
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

    private fun setStory(detailStory: PembatikDetail) {
        Glide.with(this)
            .load(decodeBase64ToBitmap(detailStory.image))
            .into(binding.ivDetailPhoto)

        binding.apply {
            tvDataName.text = detailStory.name
            tvDataStarted.text = detailStory.startedYear
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

    companion object {
        const val EXTRA_DATA = "EXTRA_DATA"
        const val EXTRA_MATERIAL_ID = "EXTRA_MATERIAL_ID"
    }
}