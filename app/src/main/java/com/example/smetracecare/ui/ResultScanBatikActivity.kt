package com.example.smetracecare.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smetracecare.R
import com.example.smetracecare.databinding.ActivityResultScanBatikBinding
import com.example.smetracecare.ml.Model3
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class ResultScanBatikActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultScanBatikBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultScanBatikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            displayImage(imageUri)
            classifyImage(imageUri)
        } else {
            Log.e(TAG, "No image URI provided")
            showToast(getString(R.string.no_image_uri))
            finish()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun displayImage(uri: Uri) {
        Log.d(TAG, "Displaying image: $uri")
        binding.resultImage.setImageURI(uri)
    }

    private fun classifyImage(imageUri: Uri) {
        try {
            val bitmap = getBitmapFromUri(imageUri)
            val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)

            // Ensure the bitmap is in ARGB_8888 format
            val argbBitmap = resizedBitmap.copy(Bitmap.Config.ARGB_8888, true)

            val model = Model3.newInstance(this)

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(argbBitmap)
            val byteBuffer = tensorImage.buffer

            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            showResults(outputFeature0)
            model.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error in classifying image", e)
            showToast("Error in classifying image: ${e.message}")
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }

    @SuppressLint("SetTextI18n", "StringFormatInvalid")
    private fun showResults(outputFeature0: TensorBuffer) {
        val scores = outputFeature0.floatArray
//        val labels = arrayOf("Label1", "Label2", "Label3") // Replace with your actual labels
        Log.d("hasil", scores.contentToString())
        val probabilities = listOf(
            scores.contentToString()
        )
        val labels = arrayOf(
            "Label1", "Label2", "Label3", "Label4", "Label5",
            "Label6", "Label7", "Label8", "Label9", "Label10",
            "Label11", "Label12"
        )
//        val labels = arrayOf(
//            "Batik Tambal",
//            "Batik Sekar Jagad",
//            "Batik Poleng",
//            "Batik Parang",
//            "Batik Pala",
//            "Batik Megamendung",
//            "Batik Lasem",
//            "Batik Kawung",
//            "Batik Insang",
//            "Batik Ikat Celup",
//            "Batik Dayak",
//            "Batik Cendrawasih",
//            "Batik Geblek Renteng",
//            "Batik Betawi",
//            "Batik Bali",
//        )

// Ensure that probabilities and labels have the same size
        if (probabilities.size == labels.size) {
            val result = probabilities.zip(labels)
            for ((probability, label) in result) {
                println("$label: $probability")
                Log.d("hasil", "$label: $probability")
            }
        } else {
            println("Number of probabilities does not match number of labels")
        }
        if (scores.isEmpty()) {
            binding.resultText.text = getString(R.string.classification_error, "No valid results")
            return
        }

        // Ensure the scores array length matches the labels array length
        if (scores.size != labels.size) {
            Log.e(TAG, "Scores and labels arrays size mismatch")
            binding.resultText.text = getString(R.string.classification_error, "Size mismatch")
            return
        }

        val maxIndex = scores.indices.maxByOrNull { scores[it] } ?: -1
        if (maxIndex != -1) {
//            val label = labels[maxIndex]
//            val score = scores[maxIndex]
//            if(score > 0.7) {
//                binding.resultText.text = "$label ${score.formatToString()}"
//            } else {
//                binding.resultText.text = "Gambar tidak terdeteksi"
//            }


            val resultBuilder = StringBuilder()
            for (i in labels.indices) {
                val label = labels[i]
                val score = scores[i]
                resultBuilder.append("$label: ${score.formatToString()}\n")
            }
            val result = resultBuilder.toString()
            binding.resultText.text = result
        } else {
            binding.resultText.text = getString(R.string.classification_error, "No valid results")
        }
    }

    @SuppressLint("DefaultLocale")
    private fun Float.formatToString(): String {
        return String.format("%.2f%%", this * 100)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val IMAGE_URI = "img_uri"
        const val TAG = "ResultScanBatikActivity"
    }
}
