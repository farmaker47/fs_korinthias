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
import org.koin.test.inject
import org.mockito.Mock
import org.mockito.Mockito.mock

class DependencyGraphTest: KoinTest {

    @Test
    fun testMainActivityViewModel() {
        // declare module and sub-modules
        val applicationModuleMainViewModel = module (override = true) {
            single { mock(Application::class.java) }
        }
        val mainViewModelModule = module{
            viewModel {
                MainActivityViewModel(get())
            }
        }

        startKoin {
            modules(
                applicationModuleMainViewModel,
                mainViewModelModule
            )
        }

        val service: MainActivityViewModel by inject()

        assertNotNull(service)
    }
}