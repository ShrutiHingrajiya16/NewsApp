package com.example.newsapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.newsapp.data.model.NewsArticle
import com.example.newsapp.data.repository.NewsRepository
import com.example.newsapp.presentation.viewmodel.NewsListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsListViewModelTest {

    private lateinit var viewModel: NewsListViewModel

    private val newsRepository: NewsRepository = mockk(relaxed = true)

    // Observer to observe LiveData
    private val newsObserver = mockk<Observer<List<NewsArticle>>>(relaxed = true)
    private val loadingObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val errorObserver = mockk<Observer<Boolean>>(relaxed = true)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Use a test dispatcher to control coroutines in the ViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Initialize the ViewModel
        Dispatchers.setMain(testDispatcher)
        viewModel = NewsListViewModel(newsRepository)

        // Set up observers for LiveData
        viewModel.news.observeForever(newsObserver)
        viewModel.isLoading.observeForever(loadingObserver)

        viewModel.isError.observeForever(errorObserver)
    }


    @Test
    fun `test fetchNews success`(){
        val mockArticles = listOf(NewsArticle(title = "Article 1", publishedAt = "2025-01-01T10:00:00Z"))
        coEvery { newsRepository.getTopHeadlines() } returns mockArticles

        testDispatcher.scheduler.advanceUntilIdle()

        val method = viewModel::class.java.getDeclaredMethod("fetchNews")
        method.isAccessible = true // Make the private method accessible

        coVerify { newsRepository.getTopHeadlines() }

        assertEquals(viewModel.news.value,mockArticles)
        assertTrue(viewModel.isError.value == false)
        assertTrue(viewModel.isLoading.value == false)
        assertTrue(viewModel.news.value?.isNotEmpty() == true)

    }

    @Test
    fun `test fetchNews failure`()
    {
        val mockArticles = emptyList<NewsArticle>()

        coEvery { newsRepository.getTopHeadlines() } returns mockArticles

        testDispatcher.scheduler.advanceUntilIdle()

        val method = viewModel::class.java.getDeclaredMethod("fetchNews")
        method.isAccessible = true

        coVerify { newsRepository.getTopHeadlines() }

        assertTrue(viewModel.news.value?.isEmpty() == true)
        assertTrue(viewModel.isError.value == true)
        assertTrue(viewModel.isLoading.value == false)
    }

    @Test
    fun `test formatDate success`() {

        val method = viewModel::class.java.getDeclaredMethod("formatDate", String::class.java)
        method.isAccessible = true

        val inputDate = "2025-01-01T10:00:00Z"
        val expectedOutput = "01/01/2025 10:00 am"

        val result = method.invoke(viewModel, inputDate) as String
        assertEquals(expectedOutput, result)
    }


    @Test
    fun `test formatDate failure`() {

        val method = viewModel::class.java.getDeclaredMethod("formatDate", String::class.java)
        method.isAccessible = true

        val inputDate = "2025-01-01T10:00:00Z"
        val expectedOutput = "01/01/2025 10:00 AM"

        val result = method.invoke(viewModel, inputDate) as String
        assertFalse(expectedOutput == result)

    }

    @Test
    fun `test checkArticleListSize with valid articles`() {
        val mockArticles = listOf(NewsArticle(title = "Article 1", publishedAt = "2025-01-01T10:00:00Z"))
        val expectedList = listOf(NewsArticle(title = "Article 1", publishedAt = "01/01/2025 10:00 am"))

        val method = viewModel::class.java.getDeclaredMethod("checkArticleListSize", List::class.java)
        method.isAccessible = true

        method.invoke(viewModel, mockArticles)

        assertEquals(1, viewModel.news.value?.size)
        assertEquals(expectedList, viewModel.news.value)
        assertTrue(viewModel.isError.value == false)
    }

    @Test
    fun `test checkArticleListSize with empty articles`() {
        val mockArticles = emptyList<NewsArticle>()

        val method = viewModel::class.java.getDeclaredMethod("checkArticleListSize", List::class.java)
        method.isAccessible = true

        method.invoke(viewModel, mockArticles)

        assertEquals(null, viewModel.news.value)
        assertTrue(viewModel.isError.value == true)
    }
}