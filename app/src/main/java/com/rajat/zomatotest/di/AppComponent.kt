package com.rajat.zomatotest.di

import android.app.Application
import com.rajat.zomatotest.MainActivity
import com.rajat.zomatotest.ZomatoApp
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    ViewModelModule::class,
    FragmentSubComponentModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application):Builder

        fun build(): AppComponent
    }

    fun fragmentComponent():FragmentComponent.Factory

    fun inject(zomatoApp: ZomatoApp)

}