package com.example.smetracecare.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import id.zelory.compressor.Compressor
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivitySupplierProductAddBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smetracecare.R
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.MaterialAddViewModel
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

class PembatikAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductAddBinding
    private lateinit var currentPathPhoto: String
    private var getFile: File? = null
    private lateinit var fileFinal: File
    private lateinit var token: String
    private lateinit var supplierId: String
    private val preferences = SharedPreferences.getInstance(dataStore)
    private val materialAddViewModel: MaterialAddViewModel by lazy {
        ViewModelProvider(this)[MaterialAddViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        materialAddViewModel.message.observe(this){
            showToast(it)
        }

        materialAddViewModel.loading.observe(this){
            onLoading(it)
        }
        setupViewModel()
        setupSpinner()
        setupEditTextListener()
        setupButtonClickListeners()

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
            binding.edAddProductType.adapter = adapter
        }
    }

    private fun setupEditTextListener() {
        binding.edAddProductPrice.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.edAddProductPrice.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[Rp,.]".toRegex(), "")
                    val parsed = if (cleanString.isNotEmpty()) cleanString.toDouble() else 0.0
                    val formatted = formatRupiah(parsed)

                    current = formatted
                    binding.edAddProductPrice.setText(formatted)
                    binding.edAddProductPrice.setSelection(formatted.length)

                    binding.edAddProductPrice.addTextChangedListener(this)
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
                    this@PembatikAddActivity,
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

        binding.addProductButton.setOnClickListener {
            if (getFile == null) {
                showToast(resources.getString(R.string.uploadWarning))
                return@setOnClickListener
            }

            val addDescription = binding.edAddProductDesc.text.toString()
            if (addDescription.isEmpty()) {
                binding.edAddProductDesc.error = resources.getString(R.string.uploadDescWarning)
                return@setOnClickListener
            }

            uploadMaterial()
        }
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
                binding.edAddProductName.text.toString().toRequestBody("text/plain".toMediaType())
            val typePart = binding.edAddProductType.selectedItem.toString()
                .toRequestBody("text/plain".toMediaType())
            val pricePart =
                binding.edAddProductPrice.text.toString().replace("[Rp,.]".toRegex(), "")
                    .toRequestBody("text/plain".toMediaType())
            val supplierIdPart = supplierId.toRequestBody("text/plain".toMediaType())
            val descriptionPart =
                binding.edAddProductDesc.text.toString().toRequestBody("text/plain".toMediaType())
            materialAddViewModel.addMaterial(
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
        while (compressedFileSize > 1 * 40 * 1024) {
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
        val buffer = ByteArray(40)
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
            binding.edAddProductName.requestFocus()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectImg, this@PembatikAddActivity)
            getFile = myFile
            binding.ivDetailPhoto.setImageURI(selectImg)
            binding.edAddProductName.requestFocus()
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
            this@PembatikAddActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this@PembatikAddActivity, SupplierHomeFragment::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object {
        const val NAME_FILE = "MMddyyyy"
    }
}

