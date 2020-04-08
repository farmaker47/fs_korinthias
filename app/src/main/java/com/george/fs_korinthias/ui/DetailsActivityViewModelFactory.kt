package com.george.fs_korinthias.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainInfo

class DetailsActivityViewModelFactory(
    private val detailsInfo: MainInfo?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
            return DetailsViewModel(detailsInfo, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}