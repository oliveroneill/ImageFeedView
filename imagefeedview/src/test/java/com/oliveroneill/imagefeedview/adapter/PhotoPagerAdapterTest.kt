package com.oliveroneill.imagefeedview.adapter

import com.alexvasilkov.gestures.GestureControllerForPager
import com.alexvasilkov.gestures.Settings
import com.alexvasilkov.gestures.views.GestureImageView
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.oliveroneill.imagefeedview.ImageFeedController
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class PhotoPagerAdapterTest {
    val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here", "mock_value"))
    lateinit var adapter : PhotoPagerAdapter<String>
    lateinit var mockController : ImageFeedController<String>
    lateinit var mockView : PhotoPagerAdapter.ViewHolder
    lateinit var mockImageView : GestureImageView
    @Before
    fun setup() {
        mockController = mock()
        adapter = PhotoPagerAdapter(list, mockController)
        mockView = mock()
        mockImageView = mock()
        val mockGestureController = mock<GestureControllerForPager>()
        val mockGestureSettings = mock<Settings>()
        whenever(mockImageView.controller).thenReturn(mockGestureController)
        whenever(mockImageView.controller.settings).thenReturn(mockGestureSettings)
        whenever(mockView.image).thenReturn(mockImageView)
    }

    @Test
    fun testOnBindHolder() {
        val position = 2
        adapter.onBindViewHolder(mockView, position)
        verify(mockController).loadImage(eq(list[position]), eq(mockImageView), eq(null))
    }

    @Test
    fun testOnViewRecycled() {
        adapter.onRecycleViewHolder(mockView)
        verify(mockController).recycleImage(eq(mockImageView))
    }
}