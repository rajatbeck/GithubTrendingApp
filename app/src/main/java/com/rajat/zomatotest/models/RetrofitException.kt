package com.rajat.zomatotest.models

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException

class RetrofitException private constructor(
    message: String?,
    /**
     * The request URL which produced the error.
     */
    val url: String?,
    /**
     * Response object containing status code, headers, body, etc.
     */
    val response: Response<*>?,
    /**
     * The event kind which triggered this error.
     */
    val kind: Kind,
    exception: Throwable,
    val retrofit:Retrofit
) : RuntimeException(message, exception) {

    override fun toString(): String {
        return super.toString() + " : " + kind + " : " + url + " : "
    }

    @Throws(IOException::class)
    fun <T> getErrorBodyAs(type: Class<T>?): T? {
        if (response?.errorBody() == null) {
            return null
        }
        val converter: Converter<ResponseBody?, T> = retrofit.responseBodyConverter(type, arrayOfNulls<Annotation>(0))
        return converter.convert(response.errorBody())
    }


    /**
     * Identifies the event kind which triggered a [RetrofitException].
     */
    enum class Kind {
        /**
         * An [IOException] occurred while communicating to the server.
         */
        NETWORK,
        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    companion object {
        fun httpError(url: String, response: Response<*>?, httpException: HttpException,retrofit: Retrofit): RetrofitException {
            val message = response?.code().toString() + " " + response?.message()
            return RetrofitException(message, url, response, Kind.HTTP, httpException,retrofit)
        }

        fun networkError(exception: IOException,retrofit: Retrofit): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.NETWORK, exception,retrofit)
        }

        fun unexpectedError(exception: Throwable,retrofit: Retrofit): RetrofitException {
            return RetrofitException(exception.message, null, null, Kind.UNEXPECTED, exception,retrofit)
        }

        fun asRetrofitException(throwable: Throwable,retrofit: Retrofit): RetrofitException {
            if (throwable is RetrofitException) {
                return throwable
            }
            // We had non-200 http error
            if (throwable is HttpException) {
                //val response = throwable.response()
                return httpError("", throwable.response(), throwable,retrofit)
            }
            // A network error happened
            return if (throwable is IOException) {
                networkError(throwable,retrofit)
            } else unexpectedError(throwable,retrofit)
            // We don't know what happened. We need to simply convert to an unknown error
        }


    }
}