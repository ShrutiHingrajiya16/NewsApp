package com.example.newsapp.data.repository

import com.example.newsapp.data.model.NewsArticle

interface NewsRepository {
    suspend fun getTopHeadlines(): List<NewsArticle>
}
