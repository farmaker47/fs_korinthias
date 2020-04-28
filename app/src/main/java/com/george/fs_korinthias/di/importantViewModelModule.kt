package com.george.fs_korinthias.di

import com.george.fs_korinthias.ui.important.ImportantViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val importantViewModelModule = module {
    viewModel {
        ImportantViewModel()
    }
}