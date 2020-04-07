package com.rajat.zomatotest.repository.remote

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CachedNetworkInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val updatedRequest = if (request.headers["Cache-Control"].equals("no-cache")) {
            request.newBuilder()
                .removeHeader("Cache-Control")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build()
        } else {
            request.newBuilder().build()
        }
        val response = chain.proceed(updatedRequest)
        val cacheControl = CacheControl.Builder()
            .maxAge(2, TimeUnit.HOURS)
            .build()
        return if (response.code in 200..300) {
            response.newBuilder()
                .header("Cache-Control", cacheControl.toString())
                .build()
        } else {
            response.newBuilder().build()
        }
    }

}