package com.george.fs_korinthias.ui.efimeries

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.george.fs_korinthias.InfoOnoma
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.ui.important.WeatherApiStatus
import kotlinx.coroutines.*
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException
import java.util.HashMap

class EfimeriesViewModel : ViewModel() {

    companion object {
        const val BASE_URL_EFIMERIES = "http://www.fsk.gr/wordpress/?page_id=12685"
    }

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private var _titlePerioxes = MutableLiveData<ArrayList<MainEfimeries>>()

    // The external LiveData interface to the property is immutable, so only this class can modify
    val titlePerioxes: LiveData<ArrayList<MainEfimeries>>
        get() = _titlePerioxes

    val _toUseArrayList = ArrayList<MainEfimeries>()

    // The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<WeatherApiStatus>()

    // The external immutable LiveData for the request status
    val status: LiveData<WeatherApiStatus>
        get() = _status

    init {
        getEfimeries()
    }

    private fun getEfimeries() {

        /*Log.e("IMPORTANT", "IMPORTANT")
        Thread(Runnable {
            //run on UI
        }).start()*/

        coroutineScope.launch {
            val cookies = HashMap<String, String>()
            try {

                // status loading
                _status.postValue(WeatherApiStatus.LOADING)

                val importantResponse = Jsoup.connect(BASE_URL_EFIMERIES)
                    .method(Connection.Method.GET)
                    .userAgent("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .execute()
                cookies.putAll(importantResponse.cookies())
                val doc = importantResponse.parse()
                Log.e("EFIMERIES", doc.toString())
                //check if element exists
                if (checkElement(
                        doc.select(".vc_tta-panels").select(".vc_tta-title-text").first()
                    )
                ) {
                    val perioxes = doc.select(".vc_tta-panels").select(".vc_tta-panel-heading")
                    for ((i, onoma) in perioxes.withIndex()) {

                        val information = doc.select(".vc_tta-panels").select(".vc_tta-panel-body")
                            .select(".wpb_wrapper").select("table")[i].select("tr")
                        /*Log.e(
                            "INFORMATION",
                            information.select("td").get(601).text().toString()
                        )*/
                        Log.e(
                            "INFORMATION",
                            information.size.toString()
                        )

                        val toUseArrayListInfo = ArrayList<InfoOnoma>()
                        for ((index, info) in information.withIndex()) {

                            val infoObject = InfoOnoma(
                                info.select("td").text(),
                                "george",
                                "soloupis"
                            )
                            toUseArrayListInfo.add(infoObject)
                        }

                        /*val chunkedList = arrayListString.chunked(3)
                        Log.e("CHUNKED",chunkedList.toString())
                        for ((index,k) in chunkedList.withIndex()){
                            val infoObject = InfoOnoma(
                                chunkedList[index][0],
                                chunkedList[index][1],
                                chunkedList[index][0]                            )
                            toUseArrayListInfo.add(infoObject)
                        }*/


                        val generalObject = MainEfimeries(
                            onoma.text(),
                            toUseArrayListInfo
                        )
                        _toUseArrayList.add(generalObject)


                    }


                }

                //_status.postValue(WeatherApiStatus.DONE)

                withContext(Dispatchers.Main) {
                    // call to UI thread
                    _titlePerioxes.value = _toUseArrayList
                    _status.value = WeatherApiStatus.DONE
                    Log.e("PERIOXES", titlePerioxes.value.toString())
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