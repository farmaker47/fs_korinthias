package com.george.fs_korinthias.ui.important

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.HashMap

class ImportantViewModel : ViewModel() {

    companion object {
        const val BASE_URL_IMPORTANT = "http://www.fsk.gr/wordpress/?cat=204&paged=1"
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    // Internally, we use a MutableLiveData, because we will be updating the List of WeatherJsonObject
    // with new values
    /*private var _text = String()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val text: String
        get() = _text*/


    private var _imageList: ArrayList<String> = ArrayList()
    val imageList: ArrayList<String>
        get() = _imageList

    private var _linkList: ArrayList<String> = ArrayList()
    val linkList: ArrayList<String>
        get() = _linkList

    private var _titleList = MutableLiveData<ArrayList<MainInfo>>()
    // The external LiveData interface to the property is immutable, so only this class can modify
    val titleList: LiveData<ArrayList<MainInfo>>
        get() = _titleList

    /*private val _titleList = ArrayList<String>()
    // The external LiveData interface to the property is immutable, so only this class can modify
    val titleList: ArrayList<String>
        get() = _titleList*/

    val _toUseArrayList = ArrayList<MainInfo>()

    init {
        //_titleList.add("Xiaomi Redmi Note 6 Pro Android 9, API 28")
        //_titleList.add("Xiaomi Redmi Note 6 Pro Android 9, API 28")
        getImportantNews()
    }

    fun getImportantNews() {

        /*Log.e("IMPORTANT", "IMPORTANT")
        Thread(Runnable {
            //run on UI
        }).start()*/

        coroutineScope.launch {
            val cookies = HashMap<String, String>()
            try {
                val importantResponse = Jsoup.connect(BASE_URL_IMPORTANT)
                    .method(Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .execute()
                cookies.putAll(importantResponse.cookies())
                val doc = importantResponse.parse()
                //check if element exists
                if (checkElement(doc.select("div[class=image]").first())) {
                    val image = doc.select(".image")
                    //_titleList.value=image.select("img[alt]")
                    for (element in image) {
                        /*val altElement = element.select("img[alt]").attr("alt")
                        _toUseArrayList.add(altElement)
                        //_titleList.value?.add(altElement)
                        Log.e("IMAGEATTR", altElement.toString())

                        val srcElement = element.select("img[src]").attr("src")
                        _imageList.add(srcElement)
                        Log.e("IMAGEATTR", srcElement.toString())

                        val hrefElement = element.select("a[href]").attr("href")
                        _linkList.add(hrefElement)
                        Log.e("IMAGEATTR", hrefElement.toString())*/

                        val generalElement = MainInfo(element.select("img[alt]").attr("alt"),
                            element.select("img[src]").attr("src"),element.select("a[href]").attr("href"))
                        _toUseArrayList.add(generalElement)

                    }



                }

                //Log.e("LIST", _toUseArrayList.toString())
                //_titleList.value?.add(_toUseArrayList[0])
                //_titleList.value?.add("George")
                //Log.e("LIST_TITLE", _titleList.value.toString())
                _titleList.postValue(_toUseArrayList)


            } catch (e: IOException) {
                Log.e("EXCEPTION", e.toString())
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