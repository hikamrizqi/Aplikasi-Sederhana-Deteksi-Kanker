package com.hikam.hikamsubmissionmachinelearningforandroid.view

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hikam.hikamsubmissionmachinelearningforandroid.databinding.ActivityResultBinding
import com.hikam.hikamsubmissionmachinelearningforandroid.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Display image and perform classification
        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        if (imageUri != null) {
            val image = Uri.parse(imageUri)
            displayImage(image)
            classifyImage(image)
        } else {
            showError("No image URI provided")
            finish()
        }
    }

    // Classify the image using TensorFlow Lite model
    private fun classifyImage(image: Uri) {
        val imageClassifierHelper = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(errorMessage: String) {
                    showError(errorMessage)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.let { showResults(it) }
                }
            }
        )
        imageClassifierHelper.classifyStaticImage(image)
    }

    // Display classification results
    private fun showResults(results: List<Classifications>) {
        val topResult = results[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }

        binding.resultText.text = "$label ${score.formatToString()}"
    }

    // Display the image
    private fun displayImage(uri: Uri) {
        binding.resultImage.setImageURI(uri)
    }

    // Show error message using Toast
    private fun showError(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}
