package com.oliveroneill.imagefeedview

import com.oliveroneill.imagefeedview.adapter.PhotoListAdapter
import com.oliveroneill.imagefeedview.adapter.PhotoPagerAdapter
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class ImageFeedViewTest {
    val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here",
            "mock_value", "1", "2", "3", "4"))
    lateinit var mockGrid : PhotoListAdapter<String>
    lateinit var mockPager : PhotoPagerAdapter<String>
    lateinit var feed : ImageFeedView<String>

    @Before
    fun setup() {
        mockGrid = mockk(relaxed = true)
        mockPager = mockk(relaxed = true)
        feed = ImageFeedView(mockk(relaxed = true))
        feed.setGridAdapter(mockGrid, mockk(relaxed = true))
        feed.setPagerAdapter(mockPager, mockk(relaxed = true))
        feed.initGridAdapter(mockk(relaxed = true), 3)
        feed.initPagerAdapter(mockk(relaxed = true))
        feed.setSwipeRefresh(mockk(relaxed = true))
    }

    @Test
    fun testOnPageChangeDoesntTriggerLoadMoreTooEarly() {
        // Given
        feed.insert(list)
        // these will not be used
        val newImages = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(newImages)
            }
        })
        // When switching pages without being close to the edge
        feed.onPageChange(list.size - 6)
        // Then only the initial load should be done
        verify(exactly = 1) { mockGrid.insert(list) }
        verify(exactly = 1) { mockPager.insert(list) }
        verify(exactly = 1) { mockGrid.onNextItemsLoaded() }
    }

    @Test
    fun testOnPageChangeTriggersLoadMoreWithLessThanFiveImages() {
        // Given
        val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here"))
        feed.insert(list)
        verify { mockGrid.insert(list) }
        verify { mockPager.insert(list) }
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        // When
        // this one should do nothing
        feed.onPageChange(list.size - 2)
        // this will trigger another load
        feed.onPageChange(list.size - 1)
        // Then
        verify(exactly = 1) { mockGrid.insert(expected) }
        verify(exactly = 1) { mockPager.insert(expected) }
        // called on initial load and then again for new images
        verify(exactly = 2) { mockGrid.onNextItemsLoaded() }
    }

    @Test
    fun testOnPageChangeTriggersLoadMore() {
        // Given
        feed.insert(list)
        verify { mockGrid.insert(list) }
        verify { mockPager.insert(list) }
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        // When
        feed.onPageChange(list.size - 4)
        // Then
        verify { mockGrid.insert(expected) }
        verify { mockPager.insert(expected) }
        // called on initial load and then again for new images
        verify(exactly = 2) { mockGrid.onNextItemsLoaded() }
    }

    @Test
    fun testLoadMore() {
        // Given
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        // When
        feed.loadMore()
        // Then
        verify { mockGrid.insert(expected) }
        verify { mockPager.insert(expected) }
        verify { mockGrid.onNextItemsLoaded() }
    }

    @Test
    fun testRefresh() {
        // Given
        val expected = ArrayList(Arrays.asList("new", "n32", "new3"))
        val mockController : ImageFeedController<String> = mockk(relaxed = true)
        feed.setFeedLoader(object : ImageListLoader<String> {
            override fun getMoreImages(callback: (newImages: List<String>) -> Unit) {
                callback(expected)
            }
        })
        feed.setController(mockController)
        // When
        feed.refresh()
        // Then
        verify { mockController.clear() }
        verify { mockGrid.clear() }
        verify { mockGrid.clear() }
        verify { mockPager.clear() }
        verify { mockGrid.insert(expected) }
        verify { mockPager.insert(expected) }
        verify { mockGrid.onNextItemsLoaded() }
    }

    @Test
    fun testInsert() {
        // When
        feed.insert(list)
        // Then
        verify { mockGrid.insert(list) }
        verify { mockPager.insert(list) }
        verify { mockGrid.onNextItemsLoaded() }
    }
}