package com.george.fs_korinthias.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.george.fs_korinthias.MainInfo
import kotlinx.coroutines.selects.select

class DetailsViewModel(detailsInfo: MainInfo?, app: Application) : AndroidViewModel(app) {

    private val _selectedNews = MutableLiveData<MainInfo>()
    // The external LiveData for the SelectedNews
    val selectedNews: LiveData<MainInfo>
        get() = _selectedNews

    // Initialize the _selectedNews MutableLiveData
    init {
        _selectedNews.value = detailsInfo
        Log.e("NEW", selectedNews.value?.image)
    }


}