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
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivitySmePembatikAddBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smetracecare.R
import com.example.smetracecare.data.SharedPreferences
import com.example.smetracecare.data.dataStore
import com.example.smetracecare.viewModel.DataStoreViewModel
import com.example.smetracecare.viewModel.PembatikAddViewModel
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

class SmePembatikAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmePembatikAddBinding
    private lateinit var currentPathPhoto: String
    private var getFile: File? = null
    private lateinit var fileFinal: File
    private lateinit var token: String
    private lateinit var smeId: String
    private val preferences = SharedPreferences.getInstance(dataStore)
    private val pembatikAddViewModel: PembatikAddViewModel by lazy {
        ViewModelProvider(this)[PembatikAddViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmePembatikAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pembatikAddViewModel.message.observe(this){
            showToast(it)
        }

        pembatikAddViewModel.loading.observe(this){
            onLoading(it)
        }
        setupViewModel()
        setupButtonClickListeners()
        observeViewModel()

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
            smeId = it
        }
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
                    this@SmePembatikAddActivity,
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

        binding.addPembatikButton.setOnClickListener {
            if (getFile == null) {
                showToast(resources.getString(R.string.uploadWarning))
                return@setOnClickListener
            }

            val addDescription = binding.edAddPembatikDesc.text.toString()
            if (addDescription.isEmpty()) {
                binding.edAddPembatikDesc.error = resources.getString(R.string.uploadDescWarning)
                return@setOnClickListener
            }

            uploadPembatik()
        }
    }

    private fun uploadPembatik() {
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
                binding.edAddPembatikName.text.toString().toRequestBody("text/plain".toMediaType())
            val typePart = binding.edAddPembatikStarted.text.toString().toRequestBody("text/plain".toMediaType())
            val smeIdPart = smeId.toRequestBody("text/plain".toMediaType())
            val descriptionPart =
                binding.edAddPembatikDesc.text.toString().toRequestBody("text/plain".toMediaType())
            // Logging the values
            Log.d("UploadPembatik", "token: $token")
            Log.d("UploadPembatik", "namePart: ${namePart.contentLength()} bytes | ${namePart.contentType()} | ${namePart.toString()}")
            Log.d("UploadPembatik", "descriptionPart: ${descriptionPart.contentLength()} bytes | ${descriptionPart.contentType()} | ${descriptionPart.toString()}")
            Log.d("UploadPembatik", "typePart: ${typePart.contentLength()} bytes | ${typePart.contentType()} | ${typePart.toString()}")
            Log.d("UploadPembatik", "smeIdPart: ${smeIdPart.contentLength()} bytes | ${smeIdPart.contentType()} | ${smeIdPart.toString()}")

            pembatikAddViewModel.addPembatik(
                imgMultiPart,
                token,
                namePart,
                descriptionPart,
                typePart,
                smeIdPart
            )
        }
        showToast("Uploading pembatik...")
        startActivity(Intent(this@SmePembatikAddActivity, SupplierHomeFragment::class.java))
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
            binding.edAddPembatikName.requestFocus()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectImg, this@SmePembatikAddActivity)
            getFile = myFile
            binding.ivDetailPhoto.setImageURI(selectImg)
            binding.edAddPembatikName.requestFocus()
        }
    }

    private val timeStamp: String =
        SimpleDateFormat(NAME_FILE, Locale.US).format(System.currentTimeMillis())

    private fun createCustomFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }

    private fun showToast(msg: String) {
        Log.d("add pembatik toast", msg)
        Toast.makeText(
            this@SmePembatikAddActivity,
            StringBuilder(getString(R.string.message)).append(msg),
            Toast.LENGTH_SHORT
        ).show()
        val intent = Intent(this@SmePembatikAddActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun observeViewModel() {
        pembatikAddViewModel.apply {
            loading.observe(this@SmePembatikAddActivity) { isLoading ->
                // Show/hide loading indicator
            }

            message.observe(this@SmePembatikAddActivity) { message ->
                Toast.makeText(this@SmePembatikAddActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val NAME_FILE = "MMddyyyy"
    }
}

