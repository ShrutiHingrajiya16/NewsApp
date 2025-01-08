package com.example.newsapp.data.model

data class NewsArticle(
    val title: String? = "",
    val author: String? = "",
    val description: String? = "",
    val url: String? = "",
    val urlToImage: String? = "",
    var publishedAt: String? = "",
    val content: String? = "",
)