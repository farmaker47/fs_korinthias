package com.george.fs_korinthias.di

import com.george.fs_korinthias.ui.efimeriesDetails.EfimeriesDetailsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val efimeriesDetailsViewModelModule = module {
    viewModel {
        EfimeriesDetailsViewModel(getKoin().getProperty("efimeriesInfo"), get())
    }
}