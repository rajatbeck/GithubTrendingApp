package com.rajat.zomatotest.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rajat.zomatotest.ui.main.MainViewModel
import com.rajat.zomatotest.utils.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindsViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun bindsMainViewModel(mainViewModel: MainViewModel): ViewModel

}