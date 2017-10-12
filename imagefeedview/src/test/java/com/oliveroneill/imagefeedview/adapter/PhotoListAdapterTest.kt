package com.oliveroneill.imagefeedview.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.nhaarman.mockito_kotlin.*
import com.oliveroneill.imagefeedview.ImageFeedController
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.verify
import java.util.*

class PhotoListAdapterTest {
    val list = ArrayList(Arrays.asList("test123", "nvdjfn", "urlgoes here", "mock_value"))
    lateinit var mockController : ImageFeedController<String>
    lateinit var adapter : PhotoListAdapter<String>
    lateinit var mockView : PhotoListAdapter.ViewHolder
    lateinit var mockImageView : ImageView

    @Before
    fun setup() {
        mockController = mock()
        adapter = PhotoListAdapter(list, mockController)
        mockView = mock()
        mockImageView = mock()
        whenever(mockView.image).thenReturn(mockImageView)
    }

    @Test
    fun testOnClick() {
        val expectedClickPos = 2
        val mockListener : PhotoListAdapter.OnPhotoListener = mock()
        adapter.setListener(mockListener)
        val mockView = mock<View> {
            on {getTag(anyInt())} doReturn list[expectedClickPos]
        }
        adapter.onClick(mockView)
        verify(mockListener).onPhotoClick(expectedClickPos)
    }

    @Test
    fun testOnBindHolder() {
        val position = 2
        adapter.onBindViewHolder(mockView, position)
        verify(mockController).loadImage(eq(list[position]), eq(mockImageView), eq(null))
        verify(mockImageView).setTag(anyInt(), eq(list[position]))
    }

    @Test
    fun testOnViewRecycled() {
        adapter.onViewRecycled(mockView)
        verify(mockController).recycleImage(eq(mockImageView))
    }

    @Test
    fun testOnAttachedToRecyclerViewLoadsMore() {
        val loadingOffset = 2
        // Mock adapter so that real RecyclerView methods are not called
        class MockListAdapter : PhotoListAdapter<String>(list, mockController) {
            override fun onLoadingStateChanged() {}
        }
        adapter = MockListAdapter()
        mockView = mock()
        whenever(mockView.image).thenReturn(mock())
        val callbackMock : EndlessRecyclerAdapter.LoaderCallbacks = mock {
            on {canLoadNextItems()} doReturn true
            on {loadNextItems()} doAnswer {
                adapter.onNextItemsLoaded()
                null
            }
        }
        adapter.setCallbacks(callbackMock)
        adapter.setLoadingOffset(loadingOffset)
        val recyclerView: RecyclerView = mock {
            on {getChildAt(anyInt())} doReturn mock<View>()
            on {getChildAdapterPosition(any())} doReturn list.size - loadingOffset + 1
            on {post(any())} doAnswer {
                val runnable = it.arguments[0] as Runnable
                runnable.run()
                null
            }
        }
        adapter.onAttachedToRecyclerView(recyclerView)
        // called once when setting callback and then again on attach
        verify(callbackMock, times(2)).canLoadNextItems()
        verify(callbackMock, times(2)).loadNextItems()
    }

    @Test
    fun testOnAttachedToRecyclerViewLoadsMoreChecksCanLoad() {
        val loadingOffset = 2
        // Mock adapter so that real RecyclerView methods are not called
        class MockListAdapter : PhotoListAdapter<String>(list, mockController) {
            override fun onLoadingStateChanged() {}
        }
        adapter = MockListAdapter()
        mockView = mock()
        whenever(mockView.image).thenReturn(mock())
        // will return false on canLoadNextItems
        val callbackMock : EndlessRecyclerAdapter.LoaderCallbacks = mock {
            on {canLoadNextItems()} doReturn false
            on {loadNextItems()} doAnswer {
                adapter.onNextItemsLoaded()
                null
            }
        }
        adapter.setCallbacks(callbackMock)
        adapter.setLoadingOffset(loadingOffset)
        val recyclerView: RecyclerView = mock {
            on {getChildAt(anyInt())} doReturn mock<View>()
            on {getChildAdapterPosition(any())} doReturn list.size - loadingOffset + 1
            on {post(any())} doAnswer {
                val runnable = it.arguments[0] as Runnable
                runnable.run()
                null
            }
        }
        adapter.onAttachedToRecyclerView(recyclerView)
        // called once when setting callback and then again on attach
        verify(callbackMock, times(2)).canLoadNextItems()
        // never called due to canLoadNextItems being false
        verify(callbackMock, never()).loadNextItems()
    }

    @Test
    fun testOnAttachedToRecyclerViewUnderLoadOffset() {
        val loadingOffset = 2
        // Mock adapter so that real RecyclerView methods are not called
        class MockListAdapter : PhotoListAdapter<String>(list, mockController) {
            override fun onLoadingStateChanged() {}
        }
        adapter = MockListAdapter()
        mockView = mock()
        whenever(mockView.image).thenReturn(mock())
        val callbackMock : EndlessRecyclerAdapter.LoaderCallbacks = mock {
            on {canLoadNextItems()} doReturn true
            on {loadNextItems()} doAnswer {
                adapter.onNextItemsLoaded()
                null
            }
        }
        adapter.setCallbacks(callbackMock)
        adapter.setLoadingOffset(loadingOffset)
        val recyclerView: RecyclerView = mock {
            on {getChildAt(anyInt())} doReturn mock<View>()
            on {getChildAdapterPosition(any())} doReturn list.size - loadingOffset - 1
            on {post(any())} doAnswer {
                val runnable = it.arguments[0] as Runnable
                runnable.run()
                null
            }
        }
        adapter.onAttachedToRecyclerView(recyclerView)
        // called once when setting callback but is not called again since we aren't at the end of
        // the scroll view
        verify(callbackMock).canLoadNextItems()
        verify(callbackMock).loadNextItems()
    }
}