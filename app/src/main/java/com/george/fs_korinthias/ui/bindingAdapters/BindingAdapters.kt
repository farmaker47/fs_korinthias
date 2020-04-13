package com.george.fs_korinthias.ui.bindingAdapters

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.george.fs_korinthias.InfoOnoma
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.R
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterDetails
import com.george.fs_korinthias.ui.detailsNews.NewsApiStatus
import com.george.fs_korinthias.ui.adapters.EfimeriesAdapterMain
import com.george.fs_korinthias.ui.adapters.NewsAdapter
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

@BindingAdapter("listDataEfimeriesMain")
fun bindRecyclerViewEfimeriesMain(recyclerView: RecyclerView, data: List<MainEfimeries>?) {
    val adapter = recyclerView.adapter as EfimeriesAdapterMain
    adapter.submitList(data)
}

@BindingAdapter("listDataEfimeriesDetails")
fun bindRecyclerViewEfimeriesDetails(recyclerView: RecyclerView, data: List<InfoOnoma>?) {
    val adapter = recyclerView.adapter as EfimeriesAdapterDetails
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
            //statusImageView.visibility = View.VISIBLE
            //statusImageView.setImageResource(R.drawable.loading_animation)
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

@BindingAdapter("progressApiStatus")
fun bindStatusProgressBarImportantAndAllNews(progressbar: ProgressBar, status: WeatherApiStatus?) {
    when (status) {
        WeatherApiStatus.LOADING -> {
            progressbar.visibility = View.VISIBLE
            //Glide.with(statusImageView.context).load(R.drawable.loading_animation).into(statusImageView)
        }
        WeatherApiStatus.ERROR -> {
            progressbar.visibility = View.GONE
            //statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        WeatherApiStatus.DONE -> {
            progressbar.visibility = View.GONE
        }
    }
}

@BindingAdapter("newsApiStatus")
fun bindStatusProgressBar(progressbar: ProgressBar, status: NewsApiStatus?) {
    when (status) {
        NewsApiStatus.LOADING -> {
            progressbar.visibility = View.VISIBLE
            //Glide.with(statusImageView.context).load(R.drawable.loading_animation).into(statusImageView)
        }
        NewsApiStatus.ERROR -> {
            progressbar.visibility = View.GONE
            //statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        NewsApiStatus.DONE -> {
            progressbar.visibility = View.GONE
        }
    }
}

@BindingAdapter("newsApiStatusImageViewDetails")
fun bindStatusImageViewDetails(imageView: ImageView, status: NewsApiStatus?) {
    when (status) {
        NewsApiStatus.LOADING -> {
            imageView.visibility = View.GONE
            //Glide.with(statusImageView.context).load(R.drawable.loading_animation).into(statusImageView)
        }
        NewsApiStatus.ERROR -> {
            imageView.visibility = View.VISIBLE
            imageView.setImageResource(R.drawable.ic_connection_error)
        }
        NewsApiStatus.DONE -> {
            imageView.visibility = View.GONE
        }
    }
}

@BindingAdapter("htmlToString")
fun bindTextViewHtml(textView: TextView, htmlValue: String?) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.text = Html.fromHtml(htmlValue, Html.FROM_HTML_MODE_COMPACT)
        //Log.e("NEW_Adapter", Html.fromHtml(htmlValue, Html.FROM_HTML_MODE_COMPACT).toString())
    } else {
        textView.text = Html.fromHtml(htmlValue);
    }
}

@BindingAdapter("efimeriesToString")
fun bindTextViewEfimeriesImerominia(textView: TextView, textValue: String?) {

    val date2020 = textValue?.split(" 2020")
    val tilefono = date2020!![1].split("274")

    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.text = Html.fromHtml("<b>" + id + "</b> "date2020!![0] + "\n" + tilefono[0] + "\n" + "274" + tilefono[1], Html.FROM_HTML_MODE_COMPACT)
    } else {
        textView.text = Html.fromHtml(htmlValue);
    }*/
    if (tilefono.size == 2) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text =
                Html.fromHtml(
                    "<b>" + date2020!![0] + "</b>" + "<br>" + tilefono[0] + "<br>" + "274" + tilefono[1],
                    Html.FROM_HTML_MODE_COMPACT
                )
        } else {
            textView.text =
                Html.fromHtml("<b>" + date2020!![0] + "</b>" + "<br>" + tilefono[0] + "<br>" + "274" + tilefono[1]);
        }

    } else if (tilefono.size == 3) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text =
                Html.fromHtml(
                    "<b>" + date2020!![0] + "</b>" + "<br>" + tilefono[0] + "<br>" + "274" + tilefono[1] + "274" + tilefono[2],
                    Html.FROM_HTML_MODE_COMPACT
                )
        } else {
            textView.text =
                Html.fromHtml("<b>" + date2020!![0] + "</b>" + "<br>" + tilefono[0] + "<br>" + "274" + tilefono[1]+ "274" + tilefono[2]);
        }


        /*textView.text =
            date2020!![0] + "\n" + tilefono[0] + "\n" + "274" + tilefono[1] + "274" + tilefono[2]*/
    }


}