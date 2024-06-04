package com.example.smetracecare.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivitySupplierProductAddBinding
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.smetracecare.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat


class SupplierProductAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductAddBinding
    private lateinit var currentPathPhoto: String
    private var getFile: File? = null
    private var anyPhoto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener() {
            startActivity(Intent(this, SupplierHomeActivity::class.java))
        }

        // Setup Spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.options_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.edAddProductType.adapter = adapter
        }


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


        binding.buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)
            createCustomFile(application).also {
                val photoURI: Uri = FileProvider.getUriForFile(this@SupplierProductAddActivity, getString(R.string.package_name), it)
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
    }

    private fun formatRupiah(number: Double): String {
        val symbols = DecimalFormatSymbols(Locale("in", "ID"))
        symbols.currencySymbol = "Rp "
        symbols.monetaryDecimalSeparator = ','
        symbols.groupingSeparator = '.'

        val decimalFormat = DecimalFormat("#,###", symbols)
        return decimalFormat.format(number)
    }

    private fun uriToFile(selectImg: Uri, context: Context): File {
        val contentResolver: ContentResolver = context.contentResolver
        val myFile = createCustomFile(context)
        val inputStream = contentResolver.openInputStream(selectImg) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buffer = ByteArray(1024)
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
            anyPhoto = true
            binding.ivDetailPhoto.setImageBitmap(result)
            binding.edAddProductName.requestFocus()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectImg, this@SupplierProductAddActivity)
            getFile = myFile
            binding.ivDetailPhoto.setImageURI(selectImg)
            binding.edAddProductName.requestFocus()
        }
    }

    private val timeStamp: String = SimpleDateFormat(NAME_FILE, Locale.US).format(System.currentTimeMillis())

    private fun createCustomFile(context: Context): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(timeStamp, ".jpg", storageDir)
    }
    companion object {
        const val NAME_FILE = "MMddyyyy"
    }

}