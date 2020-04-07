package com.rajat.zomatotest.di

import android.app.Application
import com.rajat.zomatotest.BuildConfig
import com.rajat.zomatotest.repository.remote.CachedNetworkInterceptor
import com.rajat.zomatotest.repository.remote.GithubService
import com.rajat.zomatotest.utils.RxErrorHandlingCallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {

    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        return Cache(application.cacheDir, cacheSize.toLong())
    }


    /**
     * Provides a singleton instance OkHttp Builder
     */
    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClientBuilder(cachedNetworkInterceptor: CachedNetworkInterceptor): OkHttpClient.Builder =
        OkHttpClient.Builder().addNetworkInterceptor(cachedNetworkInterceptor)


    @JvmStatic
    @Provides
    @Singleton
    fun provideOkHttpClient(
        okHttpClientBuilder: OkHttpClient.Builder,
        application: Application,
        cache: Cache
    ): OkHttpClient {
        val builder = okHttpClientBuilder
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cache(cache)
        return if (BuildConfig.DEBUG) {
            val httpLogger = HttpLoggingInterceptor()
            httpLogger.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(httpLogger).addInterceptor(ChuckInterceptor(application)).build()
        } else builder.build()

    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @JvmStatic
    @Provides
    @Singleton
    fun provideGithubServiceAPI(retrofit: Retrofit):GithubService{
        return retrofit.create(GithubService::class.java)
    }


}