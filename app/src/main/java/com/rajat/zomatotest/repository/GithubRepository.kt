package com.rajat.zomatotest.repository

import androidx.lifecycle.LiveData
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.repository.local.RepositoryDAO
import com.rajat.zomatotest.repository.remote.GithubService
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

const val NETWORK_FAILURE = "network_failure"
const val INSERTION_ERROR = "insertion_error"
const val EMPTY_TABLE = "empty_table"

class GithubRepository @Inject constructor(private val dao: RepositoryDAO,private val api:GithubService) {

    private fun fetchDataFromNetwork(): Single<Resource<List<Repository>>>{
        return api.getTrendingRepoInGit()
            .onErrorReturn { listOf<Repository>() }
            .flatMap {
                if(it.isNotEmpty()) {
                    Single.just(Resource.Success(it))
                }else {
                    Single.just(Resource.Error(NETWORK_FAILURE, it))
                }
            }
    }

    fun insertRepositoryIntoDb(repositoryList: List<Repository>): Single<Resource<Long>> {
        return dao.insertRepositoryList(repositoryList)
            .onErrorReturn { listOf() }.map {
                if (it.isNotEmpty()) {
                    Resource.Success(1L)
                } else {
                    Resource.Error(INSERTION_ERROR, -1L)
                }
            }
    }

    private fun deleteAndInsertIntoDb(repositoryList: List<Repository>):Completable{
        return Completable.fromAction {dao.deleteBeforeInsert(repositoryList)}
    }

    private fun getRepositoryListFromDb():Single<Resource<List<Repository>>>{
       return dao.getRepositoryListOnce()
            .onErrorReturn { listOf() }
            .map { list->
                 if(list.isNotEmpty()){
                    Resource.Success(list)
                }else{
                    Resource.Error(EMPTY_TABLE,list)
                }
            }
    }

    fun makeRequestForTrendingRepo(force:Boolean = false):Single<Resource<List<Repository>>>{
        return fetchDataFromNetwork()
            .flatMap {
                if(it is Resource.Success){
                    deleteAndInsertIntoDb(it.data as List<Repository>)
                        .andThen(Single.just(it))
                }else{
                   getRepositoryListFromDb()
                }
            }.subscribeOn(Schedulers.io())
    }

    fun getObservableRepositoryListDB():LiveData<List<Repository>> = dao.getObservableRepositoryList()

}