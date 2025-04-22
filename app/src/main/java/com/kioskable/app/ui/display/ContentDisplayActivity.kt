package com.kioskable.app.ui.display

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.webkit.WebViewClient
import android.widget.MediaController
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.kioskable.app.R
import com.kioskable.app.databinding.ActivityContentDisplayBinding
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.ContentType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContentDisplayActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentDisplayBinding
    private val viewModel: ContentDisplayViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Check if we're in preview mode
        val isPreviewMode = intent.getBooleanExtra(EXTRA_PREVIEW_MODE, false)
        val contentId = intent.getStringExtra(EXTRA_CONTENT_ID)
        
        // In preview mode we don't use kiosk mode and allow back navigation
        if (isPreviewMode) {
            // Show a toolbar with a back button
            supportActionBar?.show()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Content Preview"
        } else {
            // Setup kiosk mode
            setupKioskMode()
        }
        
        // Initialize WebView
        setupWebView()
        
        // If we're in preview mode and have a content ID, load that specific content
        if (isPreviewMode && contentId != null) {
            viewModel.loadContentForPreview(contentId)
        } else {
            // Observe UI state changes for normal kiosk mode
            observeState()
        }
    }
    
    private fun setupKioskMode() {
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Hide system bars in immersive mode
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
    
    private fun setupWebView() {
        binding.webView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ContentDisplayUiState.Loading -> showLoading()
                        is ContentDisplayUiState.Empty -> showEmpty()
                        is ContentDisplayUiState.Error -> showError(state.message)
                        is ContentDisplayUiState.Success -> displayContent(state.content)
                    }
                }
            }
        }
    }
    
    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            errorView.visibility = View.GONE
            contentContainer.visibility = View.GONE
        }
    }
    
    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            errorView.visibility = View.GONE
            contentContainer.visibility = View.GONE
        }
    }
    
    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            emptyView.visibility = View.GONE
            errorView.visibility = View.VISIBLE
            errorView.text = message
            contentContainer.visibility = View.GONE
        }
    }
    
    private fun displayContent(content: Content) {
        // Reset visibility of all content views
        binding.apply {
            progressBar.visibility = View.GONE
            emptyView.visibility = View.GONE
            errorView.visibility = View.GONE
            contentContainer.visibility = View.VISIBLE
            imageView.visibility = View.GONE
            videoView.visibility = View.GONE
            textView.visibility = View.GONE
            webView.visibility = View.GONE
        }
        
        // Display content based on its type
        when (content.type) {
            ContentType.IMAGE -> displayImage(content)
            ContentType.VIDEO -> displayVideo(content)
            ContentType.TEXT -> displayText(content)
            ContentType.WEB_LINK -> displayWebLink(content)
        }
    }
    
    private fun displayImage(content: Content) {
        binding.imageView.visibility = View.VISIBLE
        content.content.url?.let { url ->
            Glide.with(this)
                .load(url)
                .into(binding.imageView)
        }
    }
    
    private fun displayVideo(content: Content) {
        binding.videoView.visibility = View.VISIBLE
        
        content.content.url?.let { url ->
            binding.videoView.apply {
                setMediaController(MediaController(this@ContentDisplayActivity))
                setVideoURI(Uri.parse(url))
                setOnPreparedListener { mediaPlayer ->
                    mediaPlayer.isLooping = true
                    start()
                }
                setOnCompletionListener {
                    // Loop video until next content
                    start()
                }
                requestFocus()
            }
        }
    }
    
    private fun displayText(content: Content) {
        binding.textView.visibility = View.VISIBLE
        binding.textView.text = content.content.text ?: ""
    }
    
    private fun displayWebLink(content: Content) {
        binding.webView.visibility = View.VISIBLE
        content.content.link?.let { url ->
            binding.webView.loadUrl(url)
        }
    }
    
    override fun onPause() {
        super.onPause()
        // Stop video playback when activity is paused
        binding.videoView.suspend()
    }
    
    override fun onResume() {
        super.onResume()
        // Resume video playback when activity is resumed
        binding.videoView.resume()
        
        // Reapply kiosk mode settings
        setupKioskMode()
    }
    
    override fun onBackPressed() {
        // Check if we're in preview mode
        val isPreviewMode = intent.getBooleanExtra(EXTRA_PREVIEW_MODE, false)
        
        if (isPreviewMode) {
            // Allow back navigation in preview mode
            super.onBackPressed()
        } else {
            // Disable back button in kiosk mode
            // Do nothing
        }
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    companion object {
        const val EXTRA_PREVIEW_MODE = "extra_preview_mode"
        const val EXTRA_CONTENT_ID = "extra_content_id"
        const val DEFAULT_DISPLAY_DURATION = 10L // 10 seconds default
    }
} 