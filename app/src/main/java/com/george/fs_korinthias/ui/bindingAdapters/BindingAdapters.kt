package com.george.fs_korinthias.ui.bindingAdapters

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.R
import com.george.fs_korinthias.ui.news_adapter.NewsAdapter
import com.george.fs_korinthias.ui.important.WeatherApiStatus
import com.squareup.picasso.Picasso

/**
 * When there is no MainInfo data (data is null), hide the [RecyclerView], otherwise show it.
 */
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<MainInfo>?) {
    val adapter = recyclerView.adapter as NewsAdapter
    adapter.submitList(data)
}

@BindingAdapter("newsIconDisplay")
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

@BindingAdapter("newsApiStatus")
fun bindStatusProgressBar(progressbar: ProgressBar, status: WeatherApiStatus?) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            progressbar.visibility = View.VISIBLE
            //Glide.with(statusImageView.context).load(R.drawable.loading_animation).into(statusImageView)
        }
        WeatherApiStatus.ERROR -> {
            progressbar.visibility = View.VISIBLE
            //statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        WeatherApiStatus.DONE -> {
            progressbar.visibility = View.GONE
        }
    }
}

@BindingAdapter("htmlToString")
fun bindTextViewHtml(textView: TextView, htmlValue: String?) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.text = Html.fromHtml(htmlValue, Html.FROM_HTML_MODE_COMPACT)
        Log.e("NEW_Adapter", Html.fromHtml(htmlValue, Html.FROM_HTML_MODE_COMPACT).toString())
    } else {
        textView.text = Html.fromHtml(htmlValue);
    }
}