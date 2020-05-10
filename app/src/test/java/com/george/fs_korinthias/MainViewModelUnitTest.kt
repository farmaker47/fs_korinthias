package com.george.fs_korinthias

import android.app.Application
import com.george.fs_korinthias.di.*
import junit.framework.Assert.assertNotNull
import org.junit.Test
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mock

class DependencyGraphTest: KoinTest {
    @Mock
    private lateinit var context: Application

    @Test
    fun testMainActivityViewModel() {
        // declare module and sub-modules
        val mainViewModelModule = module {
            viewModel {
                MainActivityViewModel(androidApplication())
            }
        }

        startKoin {
            //androidContext(applicationContext)
            //context=Application()
            //androidContext(context)
            modules(
                mainViewModelModule
            )
        }

        val service: MainActivityViewModel = get()

        assertNotNull(service)
    }
}