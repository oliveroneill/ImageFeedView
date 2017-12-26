package com.oliveroneill.imagefeedview.adapter

import com.alexvasilkov.gestures.views.GestureImageView
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
        val position = 2
        adapter.onBindViewHolder(mockView, position)
        verify { mockController.loadImage(list[position], mockView.image, null) }
    }

    @Test
    fun testOnViewRecycled() {
        adapter.onRecycleViewHolder(mockView)
        verify { mockController.recycleImage(mockView.image) }
    }
}