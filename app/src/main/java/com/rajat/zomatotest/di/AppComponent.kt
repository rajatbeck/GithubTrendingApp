package com.rajat.zomatotest.di

import android.app.Application
import com.rajat.zomatotest.MainActivity
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application):Builder

        fun build(): AppComponent
    }

    fun inject(mainActivity: MainActivity)


}