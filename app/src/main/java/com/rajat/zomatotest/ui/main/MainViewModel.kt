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

    private val repositoryLiveData:MediatorLiveData<Resource<List<Repository>>> = MediatorLiveData();

    init {

    }

    fun observeTrendingRepository():LiveData<Resource<List<Repository>>> = repositoryLiveData

    fun fetchTrendingGitHubRepository(forceFetch: Boolean = false) {
        repositoryLiveData.value = Resource.Loading()
        val disposable = githubRepository.makeRequestForTrendingRepo(forceFetch)
            .observeOn( AndroidSchedulers.mainThread())
            .subscribe({response ->
                repositoryLiveData.value = response
            },{error->
                //some unexpected error occured..
            })
        compositeDisposable.add(disposable)
    }

    fun sort(sortType: SortType){

    }
}
