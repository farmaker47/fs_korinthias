package com.george.fs_korinthias.ui.important

import android.util.ArraySet
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.ui.NewsApiStatus
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.HashMap

enum class WeatherApiStatus { LOADING, ERROR, DONE }

class ImportantViewModel : ViewModel() {

    companion object {
        const val BASE_URL_IMPORTANT = "http://www.fsk.gr/wordpress/?cat=204&paged="
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private var _titleList = MutableLiveData<ArrayList<MainInfo>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val titleList: LiveData<ArrayList<MainInfo>>
        get() = _titleList

    val _toUseArrayList = ArrayList<MainInfo>()

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<WeatherApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<WeatherApiStatus>
        get() = _status

    init {
        getImportantNews()
    }

    private fun getImportantNews() {

        /*Log.e("IMPORTANT", "IMPORTANT")
        Thread(Runnable {
            //run on UI
        }).start()*/

        coroutineScope.launch {
            val cookies = HashMap<String, String>()
            try {

                // status loading
                _status.postValue(WeatherApiStatus.LOADING)

                // 2 pages of important news
                for (i in 1..2) {
                    val importantResponse = Jsoup.connect(BASE_URL_IMPORTANT + i.toString())
                        .method(Connection.Method.GET)
                        .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                        .execute()
                    cookies.putAll(importantResponse.cookies())
                    val doc = importantResponse.parse()
                    //check if element exists
                    if (checkElement(doc.select(".main-content-column-1").first())) {
                        val image = doc.select(".main-content-column-1").select(".blog-block-2")
                            .select(".items").select(".image")
                        for ((index, element) in image.withIndex()) {
                            val datePlus =
                                doc.select(".blog-block-2").select(".items").select(".title")
                                    .select(".updated").text() + " "
                            val date = datePlus.substring(index * 16, index * 16 + 16)
                            val generalElement = MainInfo(
                                element.select(".image").select("img[alt]").attr("alt"),
                                element.select(".image").select("img[src]").attr("src"),
                                element.select(".image").select("a[href]").attr("href"),
                                date
                            )
                            _toUseArrayList.add(generalElement)

                        }
                    }

                    //_status.postValue(WeatherApiStatus.DONE)

                    withContext(Dispatchers.Main) {
                        // call to UI thread
                        _titleList.value = _toUseArrayList
                    }
                }

                withContext(Dispatchers.Main) {
                    // call to UI thread
                    _status.value = WeatherApiStatus.DONE
                    _titleList.value = _toUseArrayList
                }


            } catch (e: IOException) {
                Log.e("EXCEPTION", e.toString())
                _status.postValue(WeatherApiStatus.ERROR)
            }
        }


    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun checkElement(elem: Element?): Boolean {
        return elem != null
    }


}