package com.example.newsapp.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.NewsArticle
import com.example.newsapp.data.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _news = MutableLiveData<List<NewsArticle>>()
    val news: LiveData<List<NewsArticle>> = _news

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    init {
        fetchNews()
    }

    /**
     * function is used for getting top headlines of US
     */
    private fun fetchNews() {
        _isLoading.value = true

        viewModelScope.launch {
            val newsList = newsRepository.getTopHeadlines()
            _news.value = newsList

            try {
                _isLoading.value = false
                val response = newsRepository.getTopHeadlines()
                checkArticleListSize(response)

            } catch (e: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * function is used for checking size of news list
     * and used for changing the format of published date
     */
    private fun checkArticleListSize(newsList: List<NewsArticle>?) {
        _isError.value = true

        newsList?.let {
            if (it.isNotEmpty()) {

                it.map { article ->
                    article.publishedAt = article.publishedAt?.let { it1 -> formatDate(it1) }
                }

                _isError.value = false
                _news.value = it
            }
        }
    }

    /**
     * function is used for changing the date to given format
     */
    private fun formatDate(time: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())

        val date = inputFormat.parse(time)
        return outputFormat.format(date ?: Date())
    }
}