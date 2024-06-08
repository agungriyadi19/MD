package com.example.smetracecare.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.databinding.ActivityResultScanBatikBinding
import com.example.smetracecare.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultScanBatikActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanBatikBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanBatikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                contextValue = this,
                classifierListenerValue = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMsg: String) {
                        Log.d(TAG, "Error: $errorMsg")
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { showResults(it) }
                    }
                }
            )
            imageClassifierHelper.classifyImage(imageUri)
        } else {
            Log.e(TAG, "No image URI provided")
            finish()
        }

        binding.btnBack.setOnClickListener {
            val moveBack = Intent(this@ResultScanBatikActivity, ScannerBatikActivity::class.java)
            startActivity(moveBack)
        }
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    @SuppressLint("SetTextI18n")
    private fun showResults(results: List<Classifications>) {
        val topResult = results[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resultText.text = "$label ${score.formatToString()}"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "image_picker"
        const val RESULT_TEXT = "result_text"
        const val REQUEST_HISTORY_UPDATE = 1
    }
}
