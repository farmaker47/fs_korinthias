package com.george.fs_korinthias.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.ui.important.ImportantViewModel
import com.george.fs_korinthias.ui.important.WeatherApiStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.selects.select
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.HashMap

enum class NewsApiStatus { LOADING, ERROR, DONE }

class DetailsViewModel(detailsInfo: MainInfo?, app: Application) : AndroidViewModel(app) {

    private val _selectedNews = MutableLiveData<MainInfo>()

    // The external LiveData for the SelectedNews
    val selectedNews: LiveData<MainInfo>
        get() = _selectedNews

    private val _selectedText = MutableLiveData<String>()

    // The external LiveData for the SelectedNews
    val selectedText: LiveData<String>
        get() = _selectedText

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    // The internal MutableLiveData that stores the status of the most recent request
    private val _statusProgress = MutableLiveData<NewsApiStatus>()

    // The external immutable LiveData for the request status
    val statusProgress: LiveData<NewsApiStatus>
        get() = _statusProgress

    // Initialize the _selectedNews MutableLiveData
    init {
        _selectedText.value = " "
        _selectedNews.value = detailsInfo
        getNews()
    }

    private fun getNews() {

        /*Log.e("IMPORTANT", "IMPORTANT")
        Thread(Runnable {
            //run on UI
        }).start()*/

        //_statusProgress.value = NewsApiStatus.LOADING

        coroutineScope.launch {
            //delay(1000)
            val cookies = HashMap<String, String>()
            try {

                // status loading
                withContext(Dispatchers.Main) {
                    // call to UI thread
                    _statusProgress.value = NewsApiStatus.LOADING
                }

                val importantResponse = Jsoup.connect(_selectedNews.value?.link)
                    .method(Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .execute()
                cookies.putAll(importantResponse.cookies())
                val doc = importantResponse.parse()

                //check if element exists
                if (checkElement(doc.select("div[itemprop=articleBody]").first())) {
                    Log.e("NEW", doc.select("div[itemprop=articleBody]").select("p").toString())

                    withContext(Dispatchers.Main) {
                        // call to UI thread
                        _selectedText.value =
                            doc.select("div[itemprop=articleBody]").select("p").toString()
                        _statusProgress.value = NewsApiStatus.DONE
                    }

                }


                //_status.value=WeatherApiStatus.DONE
                //_selectedText.postValue(doc.select("div[itemprop=articleBody]").select("p").toString())


            } catch (e: IOException) {
                Log.e("EXCEPTION", e.toString())
                //_status.value=WeatherApiStatus.ERROR
                //_statusProgress.postValue(NewsApiStatus.ERROR)
                withContext(Dispatchers.Main) {
                    // call to UI thread
                    _statusProgress.value = NewsApiStatus.ERROR
                }
            }


        }

        //_statusProgress.value = NewsApiStatus.DONE

    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    private fun checkElement(elem: Element?): Boolean {
        return elem != null
    }


}