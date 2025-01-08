package com.example.newsapp.data.repository

import com.example.newsapp.BuildConfig
import com.example.newsapp.data.api.NewsApiService
import com.example.newsapp.data.model.NewsArticle
import javax.inject.Inject


class NewsRepositoryImpl @Inject constructor(
    private val apiService: NewsApiService
) : NewsRepository {
    override suspend fun getTopHeadlines(): List<NewsArticle> {

        val response = apiService.getTopHeadlines("us", BuildConfig.PRIVATE_KEY)

        return if (response.isSuccessful) {
            response.body()?.articles ?: emptyList()
        } else {
            emptyList()
        }
    }
}