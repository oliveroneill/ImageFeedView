package com.oliveroneill.imagefeedview

import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.oliveroneill.imagefeedview.adapter.PhotoListAdapter
import com.oliveroneill.imagefeedview.adapter.PhotoPagerAdapter
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Oliver O'Neill on 6/10/2017.
 */
class ImageFeedViewTest {
    val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here",
            "mock_value", "1", "2", "3", "4"))
    lateinit var mockGrid : PhotoListAdapter<String>
    lateinit var mockPager : PhotoPagerAdapter<String>
    lateinit var feed : ImageFeedView<String>

    @Before
    fun setup() {
        mockGrid = mock()
        mockPager = mock()
        feed = ImageFeedView(mock())
        feed.setGridAdapter(mockGrid, mock())
        feed.setPagerAdapter(mockPager, mock())
        feed.initGridAdapter(mock(), 3)
        feed.initPagerAdapter(mock())
        feed.setSwipeRefresh(mock())
    }

    @Test
    fun testOnPageChangeDoesntTriggerLoadMoreTooEarly() {
        feed.insert(list)
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        feed.onPageChange(list.size - 6)
        verify(mockGrid, times(1)).insert(eq(list))
        verify(mockPager, times(1)).insert(eq(list))
        verify(mockGrid, times(1)).onNextItemsLoaded()
    }

    @Test
    fun testOnPageChangeTriggersLoadMoreWithLessThanFiveImages() {
        val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here"))
        feed.insert(list)
        verify(mockGrid).insert(eq(list))
        verify(mockPager).insert(eq(list))
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        // this one should do nothing
        feed.onPageChange(list.size - 2)
        // this will trigger another load
        feed.onPageChange(list.size - 1)
        verify(mockGrid).insert(eq(expected))
        verify(mockPager).insert(eq(expected))
        verify(mockGrid, times(2)).onNextItemsLoaded()
    }

    @Test
    fun testOnPageChangeTriggersLoadMore() {
        feed.insert(list)
        verify(mockGrid).insert(eq(list))
        verify(mockPager).insert(eq(list))
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        feed.onPageChange(list.size - 4)
        verify(mockGrid).insert(eq(expected))
        verify(mockPager).insert(eq(expected))
        verify(mockGrid, times(2)).onNextItemsLoaded()
    }

    @Test
    fun testLoadMore() {
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        feed.loadMore()
        verify(mockGrid).insert(eq(expected))
        verify(mockPager).insert(eq(expected))
        verify(mockGrid).onNextItemsLoaded()
    }

    @Test
    fun testRefresh() {
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        val mockController : ImageFeedController<String> = mock()
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        feed.setController(mockController)
        feed.refresh()
        verify(mockController).clear()
        verify(mockGrid).clear()
        verify(mockPager).clear()
        verify(mockGrid).insert(eq(expected))
        verify(mockPager).insert(eq(expected))
        verify(mockGrid).onNextItemsLoaded()
    }

    @Test
    fun testInsert() {
        feed.insert(list)
        verify(mockGrid).insert(eq(list))
        verify(mockPager).insert(eq(list))
        verify(mockGrid).onNextItemsLoaded()
    }
}