package com.george.fs_korinthias.ui.bindingAdapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.ui.important.ImportantNewsAdapter

/**
 * When there is no MainInfo data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<String>?) {
    val adapter = recyclerView.adapter as ImportantNewsAdapter
    adapter.submitList(data)
}