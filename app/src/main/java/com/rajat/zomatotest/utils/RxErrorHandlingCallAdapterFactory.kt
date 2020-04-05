package com.rajat.zomatotest.utils

import com.rajat.zomatotest.models.RetrofitException.Companion.asRetrofitException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.lang.reflect.Type


class RxErrorHandlingCallAdapterFactory private constructor() : CallAdapter.Factory() {
    private val original = RxJava2CallAdapterFactory.create()

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        return RxCallAdapterWrapper(retrofit,original.get(returnType, annotations, retrofit) ?: return null)
    }

    private class RxCallAdapterWrapper<R>(private val retrofit: Retrofit,private val wrapped: CallAdapter<R, *>) : CallAdapter<R, Any> {

        override fun responseType(): Type {
            return wrapped.responseType()
        }

        override fun adapt(call: Call<R>): Any {
            return when (val result = wrapped.adapt(call)) {
                is Single<*> -> result.onErrorResumeNext(Function { throwable -> Single.error(asRetrofitException(throwable,retrofit)) })
                is Observable<*> -> result.onErrorResumeNext(Function { throwable -> Observable.error(asRetrofitException(throwable,retrofit)) })
                is Completable -> result.onErrorResumeNext (Function{ throwable -> Completable.error(asRetrofitException(throwable,retrofit)) } )
                else -> result
            }

        }
    }

    companion object {
        fun create(): CallAdapter.Factory {
            return RxErrorHandlingCallAdapterFactory()
        }
    }
}