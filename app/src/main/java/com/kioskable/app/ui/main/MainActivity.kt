package com.kioskable.app.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kioskable.app.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.btnDisplay.setOnClickListener {
            // This will be implemented in a future step
            Toast.makeText(this, "Display module coming soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnExplore.setOnClickListener {
            // This will be implemented in a future step
            Toast.makeText(this, "Explore module coming soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnOrder.setOnClickListener {
            // This will be implemented in a future step
            Toast.makeText(this, "Order module coming soon", Toast.LENGTH_SHORT).show()
        }
        
        binding.btnStaff.setOnClickListener {
            // This will be implemented in a future step
            Toast.makeText(this, "Staff module coming soon", Toast.LENGTH_SHORT).show()
        }
    }
} 