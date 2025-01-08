package com.example.newsapp.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.newsapp.R
import com.example.newsapp.data.model.NewsArticle
import com.example.newsapp.databinding.FragmentNewsListBinding
import com.example.newsapp.presentation.adapter.NewsAdapter
import com.example.newsapp.presentation.viewmodel.NewsListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsListFragment : Fragment() {

    private var _binding: FragmentNewsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NewsListViewModel by viewModels()
    private var newsList = emptyList<NewsArticle>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObserver()
        setAdapterView()
    }

    private fun setObserver() {

        viewModel.news.observe(viewLifecycleOwner) { articles ->
            newsList = articles
            setAdapterView()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            binding.clNoData.visibility = if (isError) View.VISIBLE else View.GONE
        }
    }

    private fun setAdapterView() {

        val adapter = NewsAdapter(newsList) { newsArticle ->
            val bundle = Bundle()
            bundle.putString("data", newsArticle.url)
            findNavController().navigate(R.id.action_NewsListFragment_to_NewsDetailsFragment, bundle)
        }
        binding.rvNews.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}