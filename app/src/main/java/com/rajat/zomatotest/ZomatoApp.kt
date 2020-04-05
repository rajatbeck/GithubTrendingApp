package com.rajat.zomatotest

import android.app.Application
import com.facebook.stetho.Stetho
import com.rajat.zomatotest.di.AppComponent
import com.rajat.zomatotest.di.DaggerAppComponent
import com.rajat.zomatotest.di.FragmentComponent

class ZomatoApp: Application() {

    lateinit var appComponent: AppComponent

    private var fragmentComponent:FragmentComponent?=null

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
        appComponent.inject(this)
    }

    fun fragmentComponent():FragmentComponent{
        if(fragmentComponent == null){
            fragmentComponent = appComponent.fragmentComponent().create()
        }
        return fragmentComponent as FragmentComponent
    }

    fun releaseFragmentComponent(){
        fragmentComponent = null
    }
}