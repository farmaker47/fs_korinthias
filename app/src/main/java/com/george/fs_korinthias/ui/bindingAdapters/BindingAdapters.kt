package com.george.fs_korinthias.ui.bindingAdapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.R
import com.george.fs_korinthias.ui.important.ImportantNewsAdapter
import com.george.fs_korinthias.ui.important.MainInfo
import com.george.fs_korinthias.ui.important.WeatherApiStatus
import com.squareup.picasso.Picasso

/**
 * When there is no MainInfo data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MainInfo>?) {
    val adapter = recyclerView.adapter as ImportantNewsAdapter
    adapter.submitList(data)
}

@BindingAdapter("weatherIconDisplay")
fun bindWeatherIcon(iconImageView: ImageView, htmlValue: String?) {
    Picasso.get().load(htmlValue).into(iconImageView)
}

@BindingAdapter("weatherApiStatus")
fun bindStatus(statusImageView: ImageView, status: WeatherApiStatus?) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
            //Glide.with(statusImageView.context).load(R.drawable.loading_animation).into(statusImageView)
        }
        WeatherApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        WeatherApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}