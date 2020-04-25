package com.george.fs_korinthias.di

import com.george.fs_korinthias.MainActivityViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainViewModelModule = module {
    viewModel {
        MainActivityViewModel(get())
    }
}