package com.oliveroneill.imagefeedview.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.oliveroneill.imagefeedview.ImageFeedController
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.util.*

class PhotoListAdapterTest {
    val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here", "mock_value"))
    lateinit var mockController : ImageFeedController<String>
    lateinit var adapter : PhotoListAdapter<String>
    lateinit var mockView : PhotoListAdapter.ViewHolder

    @Before
    fun setup() {
        mockController = mockk(relaxed = true)
        adapter = PhotoListAdapter(list, mockController)
        mockView = mockk(relaxed = true)
    }

    @Test
    fun testOnClick() {
        val expectedClickPos = 2
        val mockListener : PhotoListAdapter.OnPhotoListener = mockk(relaxed = true)
        adapter.setListener(mockListener)
        val mockView = mockk<View>()
        every {
            mockView.getTag(any())
        } returns list[expectedClickPos]
        adapter.onClick(mockView)
        verify { mockListener.onPhotoClick(expectedClickPos) }
    }

    @Test
    fun testOnBindHolder() {
        val position = 2
        adapter.onBindViewHolder(mockView, position)
        verify { mockController.loadImage(list[position], mockView.image, null) }
        verify { mockView.image.setTag(any(), list[position]) }
    }

    @Test
    fun testOnViewRecycled() {
        adapter.onViewRecycled(mockView)
        verify { mockController.recycleImage(mockView.image) }
    }

    @Test
    fun testOnAttachedToRecyclerViewLoadsMore() {
        val loadingOffset = 2
        // Mock adapter so that real RecyclerView methods are not called
        class MockListAdapter : PhotoListAdapter<String>(list, mockController) {
            override fun onLoadingStateChanged() {}
        }
        adapter = MockListAdapter()
        mockView = mockk(relaxed = true)
        val callbackMock : EndlessRecyclerAdapter.LoaderCallbacks = mockk()
        every { callbackMock.canLoadNextItems() } returns true
        every { callbackMock.loadNextItems() } answers { adapter.onNextItemsLoaded() }

        adapter.setCallbacks(callbackMock)
        adapter.setLoadingOffset(loadingOffset)

        val recyclerView: RecyclerView = mockk(relaxed = true)
        every { recyclerView.getChildAdapterPosition(any()) } returns list.size - loadingOffset + 1
        every { recyclerView.post(any()) } answers {
            firstArg<Runnable>().run()
            false
        }
        adapter.onAttachedToRecyclerView(recyclerView)
        // called once when setting callback and then again on attach
        verify(exactly = 2) { callbackMock.canLoadNextItems() }
        verify(exactly = 2) { callbackMock.loadNextItems() }
    }

    @Test
    fun testOnAttachedToRecyclerViewLoadsMoreChecksCanLoad() {
        val loadingOffset = 2
        // Mock adapter so that real RecyclerView methods are not called
        class MockListAdapter : PhotoListAdapter<String>(list, mockController) {
            override fun onLoadingStateChanged() {}
        }
        adapter = MockListAdapter()
        mockView = mockk(relaxed = true)
        // will return false on canLoadNextItems
        val callbackMock : EndlessRecyclerAdapter.LoaderCallbacks = mockk()
        every { callbackMock.canLoadNextItems() } returns false
        every { callbackMock.loadNextItems() } answers { adapter.onNextItemsLoaded() }
        adapter.setCallbacks(callbackMock)
        adapter.setLoadingOffset(loadingOffset)
        val recyclerView: RecyclerView = mockk(relaxed = true)
        every { recyclerView.getChildAdapterPosition(any()) } returns list.size - loadingOffset + 1
        every { recyclerView.post(any()) } answers {
            firstArg<Runnable>().run()
            false
        }

        adapter.onAttachedToRecyclerView(recyclerView)
        // called once when setting callback and then again on attach
        verify(exactly = 2) { callbackMock.canLoadNextItems() }
        // never called due to canLoadNextItems being false
        verify(exactly = 0) { callbackMock.loadNextItems() }
    }

    @Test
    fun testOnAttachedToRecyclerViewUnderLoadOffset() {
        val loadingOffset = 2
        // Mock adapter so that real RecyclerView methods are not called
        class MockListAdapter : PhotoListAdapter<String>(list, mockController) {
            override fun onLoadingStateChanged() {}
        }
        adapter = MockListAdapter()
        mockView = mockk(relaxed = true)
        val callbackMock : EndlessRecyclerAdapter.LoaderCallbacks = mockk()
        every { callbackMock.canLoadNextItems() } returns true
        every { callbackMock.loadNextItems() } answers { adapter.onNextItemsLoaded() }
        adapter.setCallbacks(callbackMock)
        adapter.setLoadingOffset(loadingOffset)
        val recyclerView: RecyclerView = mockk(relaxed = true)
        every { recyclerView.getChildAdapterPosition(any()) } returns list.size - loadingOffset - 1
        every { recyclerView.post(any()) } answers {
            firstArg<Runnable>().run()
            false
        }
        adapter.onAttachedToRecyclerView(recyclerView)
        // called once when setting callback but is not called again since we aren't at the end of
        // the scroll view
        verify { callbackMock.canLoadNextItems() }
        verify { callbackMock.loadNextItems() }
    }
}