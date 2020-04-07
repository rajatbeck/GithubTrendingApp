package com.rajat.zomatotest.repository

import androidx.lifecycle.LiveData
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.repository.local.RepositoryDAO
import com.rajat.zomatotest.repository.remote.GithubService
import io.reactivex.Completable
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
        return getRepositoryListFromDb()
            .flatMap {dbList-> fetchDataFromNetwork().map { networkList-> uniqueMergeList(dbList,networkList) }
            }.flatMap {
                if(it is Resource.Success){
                    deleteAndInsertIntoDb(it.data as List<Repository>).andThen(Single.just(it))
                }else{
                   getRepositoryListFromDb()
                }
            }.subscribeOn(Schedulers.io())
    }

    private fun uniqueMergeList(
        dbList: Resource<List<Repository>>,
        networkList: Resource<List<Repository>>
    ): Resource<List<Repository>> {
        return if (networkList is Resource.Success) {
            val updatedList = mutableListOf<Repository>()
            networkList.data?.forEach { networkData ->
                var dataAlreadyExists = false
                dbList.data?.forEach { offlineData ->
                    if ("${offlineData.name}${offlineData.author}" == "${networkData.name}${networkData.author}") {
                        dataAlreadyExists = true
                        val updatedData =
                            networkData.copy(isExpanded = offlineData.isExpanded)
                        updatedList.add(updatedData)
                    }
                }
                if (!dataAlreadyExists) {
                    updatedList.add(networkData)
                }
            }
            Resource.Success(updatedList.toList())
        } else {
            dbList
        }
    }

    fun getObservableRepositoryListDB():LiveData<List<Repository>> = dao.getObservableRepositoryList()

}