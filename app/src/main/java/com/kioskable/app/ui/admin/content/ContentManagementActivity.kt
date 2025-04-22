package com.kioskable.app.ui.admin.content

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.kioskable.app.R
import com.kioskable.app.databinding.ActivityContentManagementBinding
import com.kioskable.app.domain.model.Content
import com.kioskable.app.ui.display.ContentDisplayActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContentManagementActivity : AppCompatActivity(), ContentAdapter.ContentItemClickListener {

    private lateinit var binding: ActivityContentManagementBinding
    private val viewModel: ContentManagementViewModel by viewModels()
    private lateinit var contentAdapter: ContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentManagementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Setup RecyclerView
        setupRecyclerView()

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshContent()
        }

        // Setup FAB for adding new content
        binding.fabAddContent.setOnClickListener {
            val intent = Intent(this, ContentEditorActivity::class.java)
            startActivity(intent)
        }

        // Observe UI state
        observeUiState()
    }

    private fun setupRecyclerView() {
        contentAdapter = ContentAdapter(this) { viewHolder ->
            // Start drag when the handle is touched
            contentAdapter.touchHelper.startDrag(viewHolder)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ContentManagementActivity)
            adapter = contentAdapter
        }

        // Attach touch helper for drag and drop functionality
        contentAdapter.touchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ContentManagementViewModel.UiState.Loading -> showLoading()
                        is ContentManagementViewModel.UiState.Success -> showContent(state.content)
                        is ContentManagementViewModel.UiState.Empty -> showEmpty()
                        is ContentManagementViewModel.UiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showContent(content: List<Content>) {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
        contentAdapter.updateContents(content)
    }

    private fun showEmpty() {
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onEditClicked(content: Content) {
        val intent = Intent(this, ContentEditorActivity::class.java).apply {
            putExtra(ContentEditorActivity.EXTRA_CONTENT_ID, content.id)
        }
        startActivity(intent)
    }

    override fun onPreviewClicked(content: Content) {
        val intent = Intent(this, ContentDisplayActivity::class.java).apply {
            putExtra(ContentDisplayActivity.EXTRA_PREVIEW_MODE, true)
            putExtra(ContentDisplayActivity.EXTRA_CONTENT_ID, content.id)
        }
        startActivity(intent)
    }

    override fun onDeleteClicked(content: Content) {
        AlertDialog.Builder(this)
            .setTitle("Delete Content")
            .setMessage("Are you sure you want to delete this content?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteContent(content)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onToggleActive(content: Content, isActive: Boolean) {
        viewModel.setContentActive(content.id, isActive)
    }

    override fun onItemReordered(contents: List<Content>) {
        viewModel.reorderContent(contents)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_content_management, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_refresh -> {
                viewModel.refreshContent()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
} 