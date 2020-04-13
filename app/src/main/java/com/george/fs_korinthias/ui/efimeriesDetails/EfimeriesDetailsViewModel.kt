package com.george.fs_korinthias.ui.efimeriesDetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.ui.detailsNews.NewsApiStatus
import kotlinx.coroutines.*
import org.jsoup.nodes.Element

class EfimeriesDetailsViewModel (detailsInfo: MainEfimeries?, app: Application) : AndroidViewModel(app) {

    // Only if there are Images in html
    private val _selectedImages = MutableLiveData<ArrayList<String>>()
    // The external LiveData for the SelectedImages
    val selectedImages: LiveData<ArrayList<String>>
        get() = _selectedImages


    private val _selectedEfimeries = MutableLiveData<MainEfimeries>()
    // The external LiveData for the SelectedNews
    val selectedEfimeries: LiveData<MainEfimeries>
        get() = _selectedEfimeries

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
        _selectedEfimeries.value = detailsInfo
        getEfimeries()
    }

    private fun getEfimeries() {

        /*Log.e("IMPORTANT", "IMPORTANT")
        Thread(Runnable {
            //run on UI
        }).start()*/

        //_statusProgress.value = NewsApiStatus.LOADING

        coroutineScope.launch {


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