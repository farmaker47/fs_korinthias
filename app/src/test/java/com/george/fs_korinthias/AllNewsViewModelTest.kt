package com.george.fs_korinthias

import android.app.Application
import com.george.fs_korinthias.ui.allNews.AllNewsVewModel
import junit.framework.Assert
import junit.framework.Assert.assertNotNull
import org.junit.Test
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import org.mockito.Mock
import org.mockito.Mockito

class AllNewsViewModelTest: KoinTest {

    @Test
    fun testMainActivityViewModel() {
        // declare module and sub-modules
        val allNewsViewModelModule = module (override = true){
            viewModel {
                AllNewsVewModel()
            }
        }

        startKoin {
            modules(
                allNewsViewModelModule
            )
        }

        val service: AllNewsVewModel = get()

        assertNotNull(service)
    }
}