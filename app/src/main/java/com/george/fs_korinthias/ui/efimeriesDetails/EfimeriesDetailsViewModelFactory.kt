package com.george.fs_korinthias.ui.efimeriesDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.george.fs_korinthias.MainEfimeries
import com.george.fs_korinthias.MainInfo
import com.george.fs_korinthias.ui.detailsNews.DetailsViewModel

class EfimeriesDetailsViewModelFactory (
    private val detailsInfo: MainEfimeries?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EfimeriesDetailsViewModel::class.java)) {
            return EfimeriesDetailsViewModel(
                detailsInfo,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}