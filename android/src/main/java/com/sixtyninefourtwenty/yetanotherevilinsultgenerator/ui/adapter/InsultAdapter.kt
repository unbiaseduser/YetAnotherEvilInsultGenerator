package com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemDetailsLookup.ItemDetails
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.R
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.ListItemInsultBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.drawableContent
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.tint

class InsultAdapter(private val onInsultClick: (Insult) -> Unit) : ListAdapter<Insult, InsultAdapter.ViewHolder>(INSULT_DIFFER) {

    lateinit var selectionTracker: SelectionTracker<Long>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemInsultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding) { onInsultClick(getItem(it)) }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), selectionTracker)
    }

    class DetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            return recyclerView.findChildViewUnder(e.x, e.y)?.let {
                (recyclerView.getChildViewHolder(it) as? ViewHolder)?.getItemDetails()
            }
        }
    }

    inner class KeyProvider : ItemKeyProvider<Long>(SCOPE_CACHED) {
        override fun getKey(position: Int): Long {
            return getItem(position).number.toLong()
        }

        override fun getPosition(key: Long): Int {
            return currentList.indexOfFirst { it.number.toLong() == key }
        }
    }

    class ViewHolder(private val binding: ListItemInsultBinding, private val onInsultClick: (Int) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(insult: Insult, selectionTracker: SelectionTracker<Long>) {
            binding.root.isChecked = selectionTracker.isSelected(insult.number.toLong())
            binding.insult.text = insult.insult

            if (insult.isFavorite) {
                binding.favoriteIcon.drawableContent = ContextCompat.getDrawable(binding.root.context, R.drawable.favorite)?.tint(Color.RED)
            } else {
                binding.favoriteIcon.drawableContent = null
            }
        }

        fun getItemDetails() = object : ItemDetails<Long>() {
            override fun getPosition(): Int = absoluteAdapterPosition

            override fun getSelectionKey(): Long? {
                val adapter = bindingAdapter as? InsultAdapter
                return adapter?.getItem(absoluteAdapterPosition)?.number?.toLong()
            }

        }

        init {
            binding.root.setOnClickListener { onInsultClick(absoluteAdapterPosition) }
        }
    }

    companion object {
        private val INSULT_DIFFER = object : DiffUtil.ItemCallback<Insult>() {
            override fun areItemsTheSame(oldItem: Insult, newItem: Insult): Boolean {
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(oldItem: Insult, newItem: Insult): Boolean {
                return oldItem == newItem
            }

        }
    }
}