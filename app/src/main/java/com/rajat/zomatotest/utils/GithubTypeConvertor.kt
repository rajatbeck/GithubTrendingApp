package com.rajat.zomatotest.utils

import androidx.room.TypeConverter
import com.rajat.zomatotest.models.BuiltBy
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object GithubTypeConvertor {

    private val moshi: Moshi by lazy { Moshi.Builder().build() }

    @TypeConverter
    @JvmStatic
    fun stringToList(value: String): List<BuiltBy> {
        val adapter = moshi.adapter<List<BuiltBy>>(Types.newParameterizedType(List::class.java,BuiltBy::class.java))
        return adapter.fromJson(value)!!
    }

    @TypeConverter
    @JvmStatic
    fun listToString(value: List<BuiltBy>): String {
        return moshi.adapter(List::class.java).toJson(value)
    }

}