package com.george.fs_korinthias.di

import com.george.fs_korinthias.ui.detailsNews.DetailsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val detailsActivityViewModelModule = module {

    viewModel {
        DetailsViewModel(getKoin().getProperty("detailsInfo"), get())
    }
}