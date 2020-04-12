package com.george.fs_korinthias.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.george.fs_korinthias.MainInfo
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

enum class NewsApiStatus { LOADING, ERROR, DONE }

class DetailsViewModel(detailsInfo: MainInfo?, app: Application) : AndroidViewModel(app) {

    // Only if there are Images in html
    private val _selectedImages = MutableLiveData<ArrayList<String>>()
    // The external LiveData for the SelectedImages
    val selectedImages: LiveData<ArrayList<String>>
        get() = _selectedImages


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

    val _toUseArrayList = ArrayList<String>()

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
                            doc.select("div[itemprop=articleBody]").outerHtml()

                        // If doc contains youtube link
                        //<a href="https://ehealth.gov.gr/p-rv/p" data-blogger-escaped-target="_blank">διαδικτυακής πύλης gov.gr</a>
                        if (doc.select("div[itemprop=articleBody]").select("p")
                                .select("iframe[src]").toString().contains("youtube")
                        ) {
                            // Make link clickable
                            _selectedText.value +=
                                "<a href=\"" + doc.select("div[itemprop=articleBody]")
                                    .select("iframe[src]")
                                    .attr("src") + "\" data-blogger-escaped-target=\"_blank\">Βίντεο</a>"
                            Log.i("BINTEO", _selectedText.value)
                        }

                        /*// If contains embedded pdf go to original page
                        if(checkElement(doc.select(".pdfemb-viewer").first())){
                            //Log.e("PDF_EMMBEDED", "PDF")
                            _selectedText.value +=
                                "<a href=\"" + _selectedNews.value?.link + "\" data-blogger-escaped-target=\"_blank\">Σελίδα</a>"
                        }*/

                        // If contains Images
                        if (checkElement(doc.select("div[itemprop=articleBody]")
                                .select("a[href]").first())
                        ) {
                            val imagesLinks = doc.select("div[itemprop=articleBody]")
                                .select("a[href]")
                            //Log.e("LINKS",imagesLinks.toString())
                            for (link in imagesLinks){

                                _toUseArrayList.add(link.attr("href").toString())
                            }

                            _selectedImages.value = _toUseArrayList
                            //Log.e("LINKS",_selectedImages.value.toString())
                        }

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