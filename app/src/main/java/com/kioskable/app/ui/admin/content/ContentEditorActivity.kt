package com.kioskable.app.ui.admin.content

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kioskable.app.R
import com.kioskable.app.databinding.ActivityContentEditorBinding
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.ContentData
import com.kioskable.app.domain.model.ContentType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

@AndroidEntryPoint
class ContentEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContentEditorBinding
    private val viewModel: ContentEditorViewModel by viewModels()
    
    private var contentType: ContentType = ContentType.TEXT
    private var selectedImageUri: Uri? = null
    private var selectedVideoUri: Uri? = null
    private var startDateTime: LocalDateTime? = null
    private var endDateTime: LocalDateTime? = null
    
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    
    private val getImageContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this)
                    .load(uri)
                    .into(binding.imagePreview)
            }
        }
    }
    
    private val getVideoContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedVideoUri = uri
                binding.videoPreview.setVideoURI(uri)
                binding.videoPreview.start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        val contentId = intent.getStringExtra(EXTRA_CONTENT_ID)
        
        // Set title based on whether we're editing or creating
        supportActionBar?.title = if (contentId != null) "Edit Content" else "Add Content"
        
        // Setup content type dropdown
        setupContentTypeDropdown()
        
        // Setup listeners
        setupListeners()
        
        // If we're editing existing content, load it
        contentId?.let {
            viewModel.loadContent(it)
        }
        
        // Observe UI state
        observeUiState()
    }
    
    private fun setupContentTypeDropdown() {
        val contentTypes = ContentType.values().map { it.name.lowercase().capitalize() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, contentTypes)
        binding.dropdownContentType.setAdapter(adapter)
        
        binding.dropdownContentType.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            contentType = ContentType.values()[position]
            updateContentTypeVisibility()
        }
        
        // Set default content type
        binding.dropdownContentType.setText(contentTypes[0], false)
        contentType = ContentType.IMAGE
        updateContentTypeVisibility()
    }
    
    private fun updateContentTypeVisibility() {
        // Hide all content type specific layouts
        binding.imageContentLayout.visibility = View.GONE
        binding.videoContentLayout.visibility = View.GONE
        binding.textContentLayout.visibility = View.GONE
        binding.webLinkContentLayout.visibility = View.GONE
        
        // Show only the selected content type layout
        when (contentType) {
            ContentType.IMAGE -> binding.imageContentLayout.visibility = View.VISIBLE
            ContentType.VIDEO -> binding.videoContentLayout.visibility = View.VISIBLE
            ContentType.TEXT -> binding.textContentLayout.visibility = View.VISIBLE
            ContentType.WEB_LINK -> binding.webLinkContentLayout.visibility = View.VISIBLE
        }
    }
    
    private fun setupListeners() {
        // Select image button
        binding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getImageContent.launch(intent)
        }
        
        // Select video button
        binding.btnSelectVideo.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            getVideoContent.launch(intent)
        }
        
        // Schedule switch
        binding.switchScheduled.setOnCheckedChangeListener { _, isChecked ->
            binding.scheduleLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        
        // Date time pickers
        binding.btnStartDateTime.setOnClickListener {
            showDateTimePicker { dateTime ->
                startDateTime = dateTime
                binding.btnStartDateTime.text = dateTime.format(dateTimeFormatter)
            }
        }
        
        binding.btnEndDateTime.setOnClickListener {
            showDateTimePicker { dateTime ->
                endDateTime = dateTime
                binding.btnEndDateTime.text = dateTime.format(dateTimeFormatter)
            }
        }
        
        // Save button
        binding.btnSave.setOnClickListener {
            saveContent()
        }
        
        // Cancel button
        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
    }
    
    private fun showDateTimePicker(onDateTimeSelected: (LocalDateTime) -> Unit) {
        val calendar = Calendar.getInstance()
        
        // Show date picker dialog
        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            // After date is selected, show time picker
            TimePickerDialog(this, { _, hourOfDay, minute ->
                // Create LocalDateTime from selected date and time
                val dateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute)
                onDateTimeSelected(dateTime)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    
    private fun saveContent() {
        val title = binding.editTitle.text.toString()
        if (title.isBlank()) {
            showError("Title is required")
            return
        }
        
        // Get duration, default to 10 seconds if not provided
        val durationText = binding.editDuration.text.toString()
        val duration = if (durationText.isBlank()) 10 else durationText.toIntOrNull() ?: 10
        
        // Get content data based on type
        val contentData = when (contentType) {
            ContentType.IMAGE -> {
                if (selectedImageUri == null) {
                    showError("Please select an image")
                    return
                }
                ContentData(url = selectedImageUri.toString(), text = null, link = null)
            }
            ContentType.VIDEO -> {
                if (selectedVideoUri == null) {
                    showError("Please select a video")
                    return
                }
                ContentData(url = selectedVideoUri.toString(), text = null, link = null)
            }
            ContentType.TEXT -> {
                val text = binding.editTextContent.text.toString()
                if (text.isBlank()) {
                    showError("Text content is required")
                    return
                }
                ContentData(url = null, text = text, link = null)
            }
            ContentType.WEB_LINK -> {
                val url = binding.editWebUrl.text.toString()
                if (url.isBlank()) {
                    showError("Web URL is required")
                    return
                }
                ContentData(url = null, text = null, link = url)
            }
        }
        
        // Get active state
        val isActive = binding.switchActive.isChecked
        
        // Get schedule information
        val isScheduled = binding.switchScheduled.isChecked
        val scheduleStart = if (isScheduled) startDateTime else null
        val scheduleEnd = if (isScheduled) endDateTime else null
        
        // If scheduled but no dates are selected, show error
        if (isScheduled && (scheduleStart == null || scheduleEnd == null)) {
            showError("Please select both start and end date/time")
            return
        }
        
        // Create content object
        val content = Content(
            id = viewModel.contentId ?: "",
            title = title,
            type = contentType,
            content = contentData,
            duration = duration,
            order = 0, // Will be set by the backend
            active = isActive,
            scheduleStart = scheduleStart,
            scheduleEnd = scheduleEnd,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // Save content
        viewModel.saveContent(content)
    }
    
    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ContentEditorViewModel.UiState.Loading -> showLoading(true)
                        is ContentEditorViewModel.UiState.Success -> {
                            showLoading(false)
                            // If finished, close the activity
                            if (state.finished) {
                                finish()
                            }
                        }
                        is ContentEditorViewModel.UiState.ContentLoaded -> {
                            showLoading(false)
                            populateContentForm(state.content)
                        }
                        is ContentEditorViewModel.UiState.Error -> {
                            showLoading(false)
                            showError(state.message)
                        }
                    }
                }
            }
        }
    }
    
    private fun populateContentForm(content: Content) {
        // Set content ID for editing
        viewModel.contentId = content.id
        
        // Set form values
        binding.editTitle.setText(content.title)
        binding.editDuration.setText(content.duration?.toString() ?: "10")
        binding.switchActive.isChecked = content.active
        
        // Set content type
        val contentTypeIndex = ContentType.values().indexOf(content.type)
        binding.dropdownContentType.setText(
            content.type.name.lowercase().capitalize(),
            false
        )
        contentType = content.type
        updateContentTypeVisibility()
        
        // Set content based on type
        when (content.type) {
            ContentType.IMAGE -> {
                content.content.url?.let { url ->
                    Glide.with(this)
                        .load(url)
                        .into(binding.imagePreview)
                }
            }
            ContentType.VIDEO -> {
                content.content.url?.let { url ->
                    binding.videoPreview.setVideoPath(url)
                }
            }
            ContentType.TEXT -> {
                binding.editTextContent.setText(content.content.text)
            }
            ContentType.WEB_LINK -> {
                binding.editWebUrl.setText(content.content.link)
            }
        }
        
        // Set scheduling
        val isScheduled = content.scheduleStart != null || content.scheduleEnd != null
        binding.switchScheduled.isChecked = isScheduled
        binding.scheduleLayout.visibility = if (isScheduled) View.VISIBLE else View.GONE
        
        content.scheduleStart?.let {
            startDateTime = it
            binding.btnStartDateTime.text = it.format(dateTimeFormatter)
        }
        
        content.scheduleEnd?.let {
            endDateTime = it
            binding.btnEndDateTime.text = it.format(dateTimeFormatter)
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // If user tries to navigate back and there are unsaved changes
            // ask for confirmation
            if (hasUnsavedChanges()) {
                showDiscardChangesDialog()
                return true
            }
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    
    override fun onBackPressed() {
        if (hasUnsavedChanges()) {
            showDiscardChangesDialog()
            return
        }
        super.onBackPressed()
    }
    
    private fun hasUnsavedChanges(): Boolean {
        // This is a simplified version. In a real app, you would need to compare the current form values
        // with the original content to see if anything has changed.
        return binding.editTitle.text.isNotEmpty()
    }
    
    private fun showDiscardChangesDialog() {
        AlertDialog.Builder(this)
            .setTitle("Discard Changes")
            .setMessage("You have unsaved changes. Are you sure you want to discard them?")
            .setPositiveButton("Discard") { _, _ -> super.onBackPressed() }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    companion object {
        const val EXTRA_CONTENT_ID = "extra_content_id"
    }
} 