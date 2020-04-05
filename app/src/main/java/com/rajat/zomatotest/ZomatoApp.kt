package com.rajat.zomatotest

import android.app.Application
import com.rajat.zomatotest.di.AppComponent
import com.rajat.zomatotest.di.DaggerAppComponent
import dagger.Component

class ZomatoApp: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        initAppComponent()

    }

    fun initAppComponent(){
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}