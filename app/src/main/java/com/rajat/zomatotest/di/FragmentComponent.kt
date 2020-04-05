package com.rajat.zomatotest.di

import com.rajat.zomatotest.ui.main.MainFragment
import dagger.Subcomponent
import javax.inject.Singleton


@Subcomponent
interface FragmentComponent{

    @Subcomponent.Factory
    interface Factory {
        fun create(): FragmentComponent
    }

    fun inject(mainFragment: MainFragment)
}