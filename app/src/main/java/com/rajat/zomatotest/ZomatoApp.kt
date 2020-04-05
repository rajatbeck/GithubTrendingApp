package com.rajat.zomatotest

import android.app.Application
import com.facebook.stetho.Stetho
import com.rajat.zomatotest.di.AppComponent
import com.rajat.zomatotest.di.DaggerAppComponent

class ZomatoApp: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG){
            Stetho.initializeWithDefaults(this);
        }
        initAppComponent()

    }

    private fun initAppComponent(){
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }
}