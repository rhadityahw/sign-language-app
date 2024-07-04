package com.pk.signlanguageapp.ui.dictionary

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pk.signlanguageapp.data.response.DictionaryResponseItem
import com.pk.signlanguageapp.databinding.ItemDictionaryBinding

class DictionaryAdapter: ListAdapter<DictionaryResponseItem, DictionaryAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemDictionaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dictionary: DictionaryResponseItem) {
            binding.apply {
                tvDictionaryName.text = dictionary.nama
                btnExpand.setOnClickListener {
                    val intent = Intent(it.context, DetailDictionaryActivity::class.java)
                    intent.putExtra(DetailDictionaryActivity.EXTRA_DICTIONARY, dictionary)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDictionaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val huruf = getItem(position)
        holder.bind(huruf)
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<DictionaryResponseItem> =
            object : DiffUtil.ItemCallback<DictionaryResponseItem>() {
                override fun areContentsTheSame(oldItem: DictionaryResponseItem, newItem: DictionaryResponseItem): Boolean {
                    return oldItem.nama == newItem.nama
                }

                override fun areItemsTheSame(oldItem: DictionaryResponseItem, newItem: DictionaryResponseItem): Boolean {
                    return oldItem == newItem
                }
            }
    }
}