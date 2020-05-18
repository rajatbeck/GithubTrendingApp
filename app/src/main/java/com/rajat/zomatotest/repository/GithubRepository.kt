package com.rajat.zomatotest.repository

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.repository.local.RepositoryDAO
import com.rajat.zomatotest.repository.remote.GithubService
import com.rajat.zomatotest.utils.OpenForTesting
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

const val NETWORK_FAILURE = "network_failure"
const val INSERTION_ERROR = "insertion_error"
const val EMPTY_TABLE = "empty_table"

@VisibleForTesting
class GithubRepository @Inject constructor(
    private val dao: RepositoryDAO,
    private val api: GithubService
) {

    private fun fetchDataFromNetwork(force: Boolean = false): Single<Resource<List<Repository>>> {
        return chooseCachedAndNonCachedAPI(force)
            .onErrorReturn { listOf<Repository>() }
            .flatMap {
                if (it.isNotEmpty()) {
                    Single.just(Resource.Success(it))
                } else {
                    Single.just(Resource.Error(NETWORK_FAILURE, it))
                }
            }
    }

    private fun chooseCachedAndNonCachedAPI(force: Boolean = false): Single<List<Repository>> =
        if (force) api.getTrendingRepoInGitNonCached() else api.getTrendingRepoInGit()

    //TODO: return a completable over here
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

    fun deleteAndInsertIntoDb(repositoryList: List<Repository>): Completable {
        return Completable.fromAction { dao.deleteBeforeInsert(repositoryList) }
    }

    fun getRepositoryListFromDb(): Single<Resource<List<Repository>>> {
        return dao.getRepositoryListOnce()
            .onErrorReturn { listOf() }
            .map { list ->
                if (list.isNotEmpty()) {
                    Resource.Success(list)
                } else {
                    Resource.Error(EMPTY_TABLE, list)
                }
            }
    }

    fun makeRequestForTrendingRepo(force: Boolean = false): Flowable<Resource<List<Repository>>> {
        return getRepositoryListFromDb().toFlowable()
            .switchMap { dbResponse ->
                if (dbResponse is Resource.Error && dbResponse.message == EMPTY_TABLE) {
                    fetchDataFromNetwork(force).toFlowable()
                        .flatMap {
                            if (it is Resource.Success) {
                                deleteAndInsertIntoDb(it.data as List<Repository>)
                                    .andThen(Flowable.just(it))
                            } else {
                                Flowable.just(it)
                            }
                        }.startWith(Flowable.just(Resource.Loading()))
                } else {
                    fetchDataFromNetwork(force).toFlowable()
                        .flatMap { networkResponse -> uniqueMergeList(dbResponse, networkResponse) }
                        .flatMap { response ->
                            deleteAndInsertIntoDb(response.data as List<Repository>)
                                .andThen(Flowable.just(response))
                        }
                }
            }.subscribeOn(Schedulers.io())
    }

    fun uniqueMergeList(
        dbResponse: Resource<List<Repository>>,
        networkResponse: Resource<List<Repository>>
    ): Flowable<Resource<List<Repository>>> {
        return Flowable.create<Resource<List<Repository>>>({ emitter ->
            if (networkResponse is Resource.Success) {
                val updatedList = mutableListOf<Repository>()
                networkResponse.data?.forEach { networkData ->
                    var dataAlreadyExists = false
                    dbResponse.data?.forEach { offlineData ->
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
                emitter.onNext(Resource.Success(updatedList.toList()))
                emitter.onComplete()
            } else {
                emitter.onNext(dbResponse)
                emitter.onComplete()
            }
        }, BackpressureStrategy.BUFFER)

    }

    fun getObservableRepositoryListDB(): LiveData<List<Repository>> =
        dao.getObservableRepositoryList()

}