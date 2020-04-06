package com.rajat.zomatotest.ui.main

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.rajat.zomatotest.R
import com.rajat.zomatotest.ZomatoApp
import com.rajat.zomatotest.utils.ViewModelFactory
import kotlinx.android.synthetic.main.layout_error_view.*
import javax.inject.Inject

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        val TAG = "MainFragment"
    }

    @Inject
    lateinit var factory:ViewModelProvider.Factory

    private val viewModel:MainViewModel by lazy { ViewModelProvider(requireActivity(),factory).get(MainViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    private fun inject(){
        (requireActivity().application as ZomatoApp).fragmentComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeTrendingRepository().observe(viewLifecycleOwner, Observer {
            Log.d(MainFragment.TAG,it.toString())
        })
        viewModel.fetchTrendingGitHubRepository(forceFetch = false)
        btnRetry.setOnClickListener { viewModel.fetchTrendingGitHubRepository(forceFetch = false) }
    }


}
