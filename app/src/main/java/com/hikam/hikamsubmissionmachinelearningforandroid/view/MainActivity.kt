package com.hikam.hikamsubmissionmachinelearningforandroid.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.hikam.hikamsubmissionmachinelearningforandroid.R
import com.hikam.hikamsubmissionmachinelearningforandroid.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavigationView: BottomNavigationView

    // Variables for image manipulation
    private var currentImageUri: Uri? = null
    private var croppedImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavigationView = findViewById(R.id.menu)

        // Set bottom navigation item listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // TODO: Handle navigation to home
                    true
                }
                R.id.nav_new -> {
                    // Navigate to NewActivity and clear back stack
                    val intent = Intent(this, NewActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // Set click listeners for buttons
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener {
            currentImageUri?.let {
                analyzeImage(it)
                moveToResult()
            } ?: run {
                showToast(getString(R.string.empty_image_warning))
            }
        }
    }

    // Launch gallery to pick image
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Activity result launcher for picking images from gallery
    private val launcherGallery =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                currentImageUri = uri
                showImage()
                startUCrop(uri)
            } else {
                Log.d(TAG, "No media selected")
            }
        }

    // Start UCrop to perform image cropping
    private fun startUCrop(sourceUri: Uri) {
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    // Handle UCrop activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                cropImage(resultUri)
            } ?: showToast("Failed to crop image")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            showToast("Crop error: ${cropError?.message}")
        }
    }

    // Crop the image and display it
    private fun cropImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        croppedImage = uri
    }

    // Display the selected image
    private fun showImage() {
        currentImageUri?.let {
            Log.d(TAG, "Showing image: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    // Analyze the displayed image
    private fun analyzeImage(uri: Uri) {
        val intent = Intent(this, ResultActivity::class.java)
        croppedImage?.let { uri ->
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    // Move to result activity
    private fun moveToResult() {
        Log.d(TAG, "Moving to ResultActivity")
        val intent = Intent(this, ResultActivity::class.java)
        croppedImage?.let { uri ->
            intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            startActivity(intent)
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    // Show toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
