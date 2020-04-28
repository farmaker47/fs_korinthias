package com.george.fs_korinthias.di

import com.george.fs_korinthias.ui.allNews.AllNewsVewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val allNewsViewModelModule= module {
    viewModel {
        AllNewsVewModel()
    }
}