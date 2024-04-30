package com.hikam.hikamsubmissionmachinelearningforandroid.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hikam.hikamsubmissionmachinelearningforandroid.R
import com.hikam.hikamsubmissionmachinelearningforandroid.adapter.NewAdapter
import com.hikam.hikamsubmissionmachinelearningforandroid.databinding.ActivityNewBinding
import com.hikam.hikamsubmissionmachinelearningforandroid.viewmodel.NewViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewBinding
    private lateinit var newAdapter: NewAdapter
    private lateinit var newViewModel: NewViewModel
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize views
        initView()

        // Setup bottom navigation
        setupBottomNavigation()

        // Initialize RecyclerView
        initRecyclerView()

        // Initialize ViewModel
        initViewModel()
    }

    // Initialize views
    private fun initView() {
        bottomNavigationView = findViewById(R.id.menu)
        newsRecyclerView = findViewById(R.id.rvNewList)
    }

    // Setup bottom navigation
    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.nav_new
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    navigateToMainActivity()
                    true
                }
                R.id.nav_new -> {
                    // Do nothing as already in NewActivity
                    true
                }
                else -> false
            }
        }
    }

    // Navigate to MainActivity
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    // Initialize RecyclerView
    private fun initRecyclerView() {
        newAdapter = NewAdapter()
        binding.rvNewList.apply {
            adapter = newAdapter
            layoutManager = LinearLayoutManager(this@NewActivity)
        }
    }

    // Initialize ViewModel
    private fun initViewModel() {
        newViewModel = ViewModelProvider(this)[NewViewModel::class.java]
        newViewModel.fetchHealthNews()
        newViewModel.newsList.observe(this, Observer { newsList ->
            newAdapter.submitList(newsList)
        })
        newViewModel.isLoading.observe(this@NewActivity) { showLoading(it) }
    }

    // Open news URL
    fun openNewsUrl(view: View) {
        val url = view.getTag(R.id.tvLink) as? String
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    // Show loading indicator
    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
