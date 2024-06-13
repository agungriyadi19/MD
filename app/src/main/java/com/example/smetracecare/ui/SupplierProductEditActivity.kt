package com.example.smetracecare.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import id.zelory.compressor.Compressor
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivitySupplierProductEditBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.smetracecare.R
import com.example.smetracecare.data.MaterialDetail
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.MaterialEditViewModel
import com.example.smetracecare.viewModel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat

class SupplierProductEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductEditBinding
    private lateinit var currentPathPhoto: String
    private var getFile: File? = null
    private lateinit var fileFinal: File
    private lateinit var token: String
    private lateinit var supplierId: String
    private val preferences = SharedPreferences.getInstance(dataStore)
    private val materialEditViewModel: MaterialEditViewModel by lazy {
        ViewModelProvider(this)[MaterialEditViewModel::class.java]
    }
    private lateinit var detailStory: MaterialDetail
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        materialEditViewModel.message.observe(this) {
            showToast(it)
        }

        materialEditViewModel.loading.observe(this) {
            onLoading(it)
        }
        detailStory =
            intent.getParcelableExtra<MaterialDetail>(SupplierProductDetailActivity.EXTRA_DATA) as MaterialDetail
        getFile = decodeBase64ToFile(detailStory.image, this@SupplierProductEditActivity)

        setPreviousData(detailStory)
        setupViewModel()
        setupSpinner()
        setupEditTextListener()
        setupButtonClickListeners()

    }

    private fun decodeBase64ToFile(base64Str: String, context: Context): File {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        val file = createCustomFile(context)
        FileOutputStream(file).use {
            it.write(decodedBytes)
            it.flush()
        }
        return file
    }

    private fun onLoading(it: Boolean) {
        binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(preferences))[DataStoreViewModel::class.java]
        dataStoreViewModel.getToken().observe(this) {
            token = it
        }
        dataStoreViewModel.getUserID().observe(this) {
            supplierId = it
        }
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.options_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.edEditProductType.adapter = adapter
        }
    }

    private fun setupEditTextListener() {
        binding.edEditProductPrice.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.edEditProductPrice.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.]".toRegex(), "")
                    val parsed = if (cleanString.isNotEmpty()) cleanString.toDouble() else 0.0
                    val formatted = formatRupiah(parsed)

                    current = formatted
                    binding.edEditProductPrice.setText(formatted)
                    binding.edEditProductPrice.setSelection(formatted.length)

                    binding.edEditProductPrice.addTextChangedListener(this)
                }
            }
        })
    }

    private fun setupButtonClickListeners() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, SupplierHomeFragment::class.java))
        }

        binding.buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)
            createCustomFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@SupplierProductEditActivity,
                    getString(R.string.package_name),
                    it
                )
                currentPathPhoto = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }

        binding.buttonGallery.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, R.string.select_your_image.toString())
            launcherIntentGallery.launch(chooser)
        }

        binding.editProductButton.setOnClickListener {
            if (getFile == null) {
                showToast(resources.getString(R.string.uploadWarning))
                return@setOnClickListener
            }

            val addDescription = binding.edEditProductDesc.text.toString()
            if (addDescription.isEmpty()) {
                binding.edEditProductDesc.error = resources.getString(R.string.uploadDescWarning)
                return@setOnClickListener
            }

            uploadMaterial()
        }
    }

    private fun setPreviousData(materialDetail: MaterialDetail) {
        binding.apply {
            edEditProductName.setText(materialDetail.name)
            edEditProductType.setSelection(
                getIndexByValue(
                    materialDetail.type,
                    resources.getStringArray(R.array.options_array)
                )
            )
            edEditProductPrice.setText(formatRupiah(materialDetail.price.toDouble()))
            edEditProductDesc.setText(materialDetail.description)
            // Assuming you are displaying the image using Glide
            Glide.with(this@SupplierProductEditActivity)
                .load(decodeBase64ToBitmap(materialDetail.image))
                .into(ivDetailPhoto)
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

    private fun getIndexByValue(value: String, array: Array<String>): Int {
        for (i in array.indices) {
            if (array[i] == value) {
                return i
            }
        }
        return 0 // Default to the first item if not found
    }

    private fun uploadMaterial() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val file = getFile as File
                fileFinal = compressFile(file)
            }

            val requestImageFile = fileFinal.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imgMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "image", fileFinal.name, requestImageFile
            )

            val namePart =
                binding.edEditProductName.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            val typePart = binding.edEditProductType.selectedItem.toString()
                .toRequestBody("text/plain".toMediaType())
            val pricePart =
                binding.edEditProductPrice.text.toString().replace("[Rp,.]".toRegex(), "")
                    .toRequestBody("text/plain".toMediaType())
            val supplierIdPart = supplierId.toRequestBody("text/plain".toMediaType())
            val descriptionPart =
                binding.edEditProductDesc.text.toString()
                    .toRequestBody("text/plain".toMediaType())
            materialEditViewModel.editMaterial(
                detailStory.materialId.substring(9),
                imgMultiPart,
                token,
                namePart,
                descriptionPart,
                typePart,
                supplierIdPart,
                pricePart
            )

        }
        val intent = Intent(this, SupplierHomeFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private suspend fun compressFile(file: File): File {
        var compressedFile: File? = null
        var compressedFileSize = file.length()
        while (compressedFileSize > 1 * 400 * 400) {
            compressedFile = withContext(Dispatchers.Default) {
                Compressor.compress(applicationContext, file)
            }
            compressedFileSize = compressedFile.length()
        }
        return compressedFile ?: file
    }

    private fun uriToFile(selectImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomFile(context)
        val inputStream = contentResolver.openInputStream(selectImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buffer = ByteArray(400)
        var len: Int
        while (inputStream.read(buffer).also { len = it } > 0) outputStream.write(buffer, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPathPhoto)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.ivDetailPhoto.setImageBitmap(result)
            binding.edEditProductName.requestFocus()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectImg, this@SupplierProductEditActivity)
            getFile = myFile
            binding.ivDetailPhoto.setImageURI(selectImg)
            binding.edEditProductName.requestFocus()
        }
    }

    private val timeStamp: String =
        SimpleDateFormat(NAME_FILE, Locale.US).format(System.currentTimeMillis())

    private fun createCustomFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun formatRupiah(number: Double): String {
        val symbols = DecimalFormatSymbols(Locale("in", "ID"))
        symbols.currencySymbol = "Rp "
        symbols.monetaryDecimalSeparator = ','
        symbols.groupingSeparator = '.'

        val decimalFormat = DecimalFormat("#,###", symbols)
        return decimalFormat.format(number)
    }

    private fun showToast(msg: String) {
        Log.d("add material toast", msg)
        Toast.makeText(
            this@SupplierProductEditActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this@SupplierProductEditActivity, SupplierHomeFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object {
        const val NAME_FILE = "MMddyyyy"
        const val EXTRA_DATA = "EXTRA_DATA"
    }
}

