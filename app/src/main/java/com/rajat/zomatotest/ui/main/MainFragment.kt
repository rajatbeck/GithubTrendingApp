package com.rajat.zomatotest.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.rajat.zomatotest.R
import com.rajat.zomatotest.ZomatoApp
import com.rajat.zomatotest.models.Repository
import com.rajat.zomatotest.models.Resource
import com.rajat.zomatotest.models.SortType
import kotlinx.android.synthetic.main.layout_error_view.*
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        val TAG = "MainFragment"
    }

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            factory
        ).get(MainViewModel::class.java)
    }

    private val mainAdapter: MainAdapter by lazy { MainAdapter(this::onItemClick) }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    private fun inject() {
        (requireActivity().application as ZomatoApp).fragmentComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.observeTrendingRepository().observe(viewLifecycleOwner, Observer { renderUI(it) })
        viewModel.fetchTrendingGitHubRepository(forceFetch = false)
        btnRetry.setOnClickListener { viewModel.fetchTrendingGitHubRepository(forceFetch = false) }
    }

    private fun initView() {
        with(rvRepositoryList) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = mainAdapter
        }
    }

    private fun renderUI(uiResponse: Resource<List<Repository>>) {
        when (uiResponse) {
            is Resource.Success -> {
                shimmerViewContainer.visibility = View.GONE
                errorView.visibility = View.GONE
                rvRepositoryList.visibility = View.VISIBLE
                mainAdapter.submitList(uiResponse.data)
            }
            is Resource.Error -> {
                shimmerViewContainer.visibility = View.GONE
                errorView.visibility = View.VISIBLE
                rvRepositoryList.visibility = View.GONE
            }
            is Resource.Loading -> {
                shimmerViewContainer.visibility = View.VISIBLE
                errorView.visibility = View.GONE
                rvRepositoryList.visibility = View.GONE
            }
        }
    }

    fun onMenuItemClicked(menuId: Int) {
        if (menuId == R.id.sortName) {
            viewModel.rearrange(SortType.SORT_BY_NAME)
        } else {
            viewModel.rearrange(SortType.SORT_BY_STAR)
        }
    }

    private fun onItemClick(position:Int,repository: Repository){
        viewModel.expandRow(repository)
    }


}
