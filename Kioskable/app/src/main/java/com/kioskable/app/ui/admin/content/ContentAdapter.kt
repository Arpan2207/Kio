package com.kioskable.app.ui.admin.content

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kioskable.app.R
import com.kioskable.app.databinding.ItemContentBinding
import com.kioskable.app.domain.model.Content
import com.kioskable.app.domain.model.ContentType
import java.time.format.DateTimeFormatter
import java.util.Collections

class ContentAdapter(
    private val itemClickListener: ContentItemClickListener,
    private val onStartDrag: (ContentViewHolder) -> Unit
) : RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {

    private val contentList = mutableListOf<Content>()
    private val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")

    // Touch helper for drag and drop
    val touchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.bindingAdapterPosition
            val toPosition = target.bindingAdapterPosition
            
            // Swap items in the list
            Collections.swap(contentList, fromPosition, toPosition)
            notifyItemMoved(fromPosition, toPosition)
            
            // Updates the order of items
            itemClickListener.onItemReordered(contentList)
            
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            // Not used for reordering
        }
    })

    fun updateContents(newContents: List<Content>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = contentList.size
            override fun getNewListSize(): Int = newContents.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return contentList[oldItemPosition].id == newContents[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = contentList[oldItemPosition]
                val newItem = newContents[newItemPosition]
                return oldItem == newItem
            }
        })

        contentList.clear()
        contentList.addAll(newContents)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(contentList[position])
    }

    override fun getItemCount(): Int = contentList.size

    inner class ContentViewHolder(private val binding: ItemContentBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Set click listeners
            binding.btnEdit.setOnClickListener {
                itemClickListener.onEditClicked(contentList[bindingAdapterPosition])
            }
            
            binding.btnPreview.setOnClickListener {
                itemClickListener.onPreviewClicked(contentList[bindingAdapterPosition])
            }
            
            binding.btnDelete.setOnClickListener {
                itemClickListener.onDeleteClicked(contentList[bindingAdapterPosition])
            }
            
            binding.toggleActive.setOnCheckedChangeListener { _, isChecked ->
                itemClickListener.onToggleActive(contentList[bindingAdapterPosition], isChecked)
            }
            
            // Setup drag handle
            binding.imageDragHandle.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    onStartDrag(this)
                }
                false
            }
        }

        fun bind(content: Content) {
            binding.apply {
                textTitle.text = content.title
                textType.text = "Type: ${content.type.name.lowercase().capitalize()}"
                textDuration.text = "Duration: ${content.duration ?: 10}s"
                toggleActive.isChecked = content.active

                // Show/hide scheduled indicator
                imageScheduled.visibility = if (content.scheduleStart != null || content.scheduleEnd != null) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // Set the appropriate preview image based on content type
                when (content.type) {
                    ContentType.IMAGE -> {
                        content.content.url?.let { url ->
                            Glide.with(itemView.context)
                                .load(url)
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .error(R.drawable.ic_launcher_foreground)
                                .into(imagePreview)
                        }
                    }
                    ContentType.VIDEO -> {
                        // For video, show a video thumbnail or placeholder
                        imagePreview.setImageResource(android.R.drawable.ic_media_play)
                    }
                    ContentType.TEXT -> {
                        // For text, show a text icon
                        imagePreview.setImageResource(android.R.drawable.ic_dialog_info)
                    }
                    ContentType.WEB_LINK -> {
                        // For web links, show a web icon
                        imagePreview.setImageResource(android.R.drawable.ic_menu_view)
                    }
                }
            }
        }
    }

    interface ContentItemClickListener {
        fun onEditClicked(content: Content)
        fun onPreviewClicked(content: Content)
        fun onDeleteClicked(content: Content)
        fun onToggleActive(content: Content, isActive: Boolean)
        fun onItemReordered(contents: List<Content>)
    }
} 