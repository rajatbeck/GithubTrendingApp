package com.rajat.zomatotest.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.models.SortType
import com.rajat.zomatotest.repository.GithubRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(private val githubRepository: GithubRepository): ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val repositoryLiveData = githubRepository.getObservableRepositoryListDB()

    private val mediadatorRepoLiveData:MediatorLiveData<Resource<List<Repository>>> = MediatorLiveData()

    private var currentSortType:SortType = SortType.NONE

    init {
        mediadatorRepoLiveData.addSource(repositoryLiveData) {
            mediadatorRepoLiveData.value = sort(
                currentSortType,
                repositoryLiveData.value
            )?.let { list -> Resource.Success(list) }
        }
    }

    fun observeTrendingRepository():LiveData<Resource<List<Repository>>> = mediadatorRepoLiveData

    fun fetchTrendingGitHubRepository(forceFetch: Boolean = false) {
        mediadatorRepoLiveData.value = Resource.Loading()
        val disposable = githubRepository.makeRequestForTrendingRepo(forceFetch)
            .observeOn( AndroidSchedulers.mainThread())
            .subscribe({response ->
                mediadatorRepoLiveData.value = response
            },{error->
                //some unexpected error occured..
            })
        compositeDisposable.add(disposable)
    }

    fun rearrange(sortType: SortType){
        mediadatorRepoLiveData.value = sort(sortType,repositoryLiveData.value)?.
            let {list -> Resource.Success(list) }
    }

    private fun sort(sortType: SortType, list: List<Repository>?): List<Repository>? =
        when (sortType) {
            SortType.SORT_BY_NAME -> list?.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) {it.name})
            SortType.SORT_BY_STAR -> list?.sortedByDescending { it.stars }
            else -> list
        }.also { currentSortType = sortType }

    override fun onCleared() {
        if(!compositeDisposable.isDisposed){
            compositeDisposable.dispose()
        }
        super.onCleared()
    }
}
