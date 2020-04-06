package com.rajat.zomatotest.repository.remote

import com.rajat.zomatotest.models.Repository
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface GithubService{

    @Headers("Content-Type: application/json")
    @GET("repositories")
    fun getTrendingRepoInGit(): Single<List<Repository>>

}