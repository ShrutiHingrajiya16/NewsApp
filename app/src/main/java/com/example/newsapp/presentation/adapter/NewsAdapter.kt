package com.example.newsapp.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.data.model.NewsArticle
import com.example.newsapp.databinding.ItemNewsBinding

class NewsAdapter(
    private val newsList: List<NewsArticle>,
    private val onItemClick: (NewsArticle) -> Unit
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        holder.bind(newsItem, onItemClick)
    }

    override fun getItemCount(): Int = newsList.size

    class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            newsItem: NewsArticle,
            onItemClick: (NewsArticle) -> Unit
        ) {
            binding.tvTitleArticle.text = newsItem.title
            binding.tvTimeArticle.text = newsItem.publishedAt

            if (newsItem.author.isNullOrEmpty()) {
                binding.tvAuthorArticle.visibility = View.GONE
            } else {
                binding.tvAuthorArticle.visibility = View.VISIBLE
                binding.tvAuthorArticle.text =
                    binding.root.context.getString(R.string.by, newsItem.author)
            }
            Glide.with(binding.root)
                .load(newsItem.urlToImage)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.ivArticle)

            binding.clArticle.setOnClickListener {
                onItemClick(newsItem)
            }
        }
    }
}
