package com.rajat.zomatotest.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


//Note - Made all the fields nullable

@Entity(tableName = "repository",primaryKeys = ["name","author"])
@JsonClass(generateAdapter = true)
data class Repository(
    @Json(name="author") @ColumnInfo(name= "author") val author: String,
    @Json(name="avatar") @ColumnInfo(name= "avatar") val avatar: String?,
    @Json(name="builtBy") @ColumnInfo(name= "builtBy") val builtBy: List<BuiltBy>?,
    @Json(name="currentPeriodStars") @ColumnInfo(name= "currentPeriodStars") val currentPeriodStars: Int?,
    @Json(name="description") @ColumnInfo(name= "description") val description: String?,
    @Json(name="forks") @ColumnInfo(name= "forks") val forks: Int?,
    @Json(name="language") @ColumnInfo(name= "language") val language: String?,
    @Json(name="languageColor") @ColumnInfo(name= "languageColor") val languageColor: String?,
    @Json(name="name") @ColumnInfo(name= "name") val name: String,
    @Json(name="stars") @ColumnInfo(name= "stars") val stars: Int?,
    @Json(name="url") @ColumnInfo(name= "url") val url: String?,
    @Transient @ColumnInfo(name= "isExpanded") val isExpanded: Boolean = false
)

@JsonClass(generateAdapter = true)
data class BuiltBy(
    @Json(name="avatar") @ColumnInfo(name="avatar") val avatar: String?,
    @Json(name="href") @ColumnInfo(name="href") val href: String?,
    @Json(name="username") @ColumnInfo(name="username") val username: String?
)

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T? = null) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
