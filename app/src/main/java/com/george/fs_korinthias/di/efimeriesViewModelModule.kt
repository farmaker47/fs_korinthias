package com.george.fs_korinthias.di

import com.george.fs_korinthias.ui.efimeries.EfimeriesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val efimeriesViewModelModule = module {
    viewModel {
        EfimeriesViewModel()
    }
}