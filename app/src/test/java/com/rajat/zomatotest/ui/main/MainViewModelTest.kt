package com.rajat.zomatotest.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.rajat.zomatotest.mock
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.repository.GithubRepository
import com.rajat.zomatotest.repository.GithubRepositoryTest
import io.reactivex.Flowable
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.hamcrest.CoreMatchers.notNullValue
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class MainViewModelTest {

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository:GithubRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun init(){
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(repository)
    }

    @Test
    fun testNull(){
        assertThat(viewModel.observeTrendingRepository(), notNullValue())
        verify(repository, never()).makeRequestForTrendingRepo(anyBoolean())
    }

    @Test
    fun fetchTrendingGitHubRepository_emptyDbFirstTimeCase(){


    }



}