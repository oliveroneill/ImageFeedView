package com.oliveroneill.imagefeedview.adapter

import com.oliveroneill.imagefeedview.ImageFeedController
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class PhotoPagerAdapterTest {
    val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here", "mock_value"))
    lateinit var adapter : PhotoPagerAdapter<String>
    lateinit var mockController : ImageFeedController<String>
    lateinit var mockView : PhotoPagerAdapter.ViewHolder
    @Before
    fun setup() {
        mockController = mockk(relaxed = true)
        adapter = PhotoPagerAdapter(list, mockController)
        mockView = mockk(relaxed = true)
    }

    @Test
    fun testOnBindHolder() {
        // Given
        val position = 2
        // When
        adapter.onBindViewHolder(mockView, position)
        // Then
        verify { mockController.loadImage(list[position], mockView.image, null) }
    }

    @Test
    fun testOnViewRecycled() {
        // When
        adapter.onRecycleViewHolder(mockView)
        // Then
        verify { mockController.recycleImage(mockView.image) }
    }
}