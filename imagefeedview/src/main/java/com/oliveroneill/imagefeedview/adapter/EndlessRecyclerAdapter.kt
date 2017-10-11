package com.oliveroneill.imagefeedview.adapter

import android.support.v7.widget.RecyclerView

/**
 * Converted from alexvasilkov's GestureViews 'Advanced Demo' in the sample app
 * https://github.com/alexvasilkov/GestureViews/blob/master/sample/src/main/java/com/alexvasilkov/gestures/sample/ui/ex6/adapter/EndlessRecyclerAdapter.java *
 *
 */
abstract class EndlessRecyclerAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            loadNextItemsIfNeeded(recyclerView)
        }
    }
    internal var isLoading: Boolean = false
        private set
    internal var isError: Boolean = false
        private set

    private var callbacks: LoaderCallbacks? = null
    private var loadingOffset = 0

    open fun setCallbacks(callbacks: LoaderCallbacks) {
        this.callbacks = callbacks
        loadNextItems()
    }

    fun setLoadingOffset(loadingOffset: Int) {
        this.loadingOffset = loadingOffset
    }

    private fun loadNextItems() {
        val cb = callbacks
        if (!isLoading && !isError && cb != null &&  cb.canLoadNextItems()) {
            isLoading = true
            onLoadingStateChanged()
            cb.loadNextItems()
        }
    }

    internal fun reloadNextItemsIfError() {
        if (isError) {
            isError = false
            onLoadingStateChanged()
            loadNextItems()
        }
    }

    open fun onNextItemsLoaded() {
        if (isLoading) {
            isLoading = false
            isError = false
            onLoadingStateChanged()
        }
    }

    fun onNextItemsError() {
        if (isLoading) {
            isLoading = false
            isError = true
            onLoadingStateChanged()
        }
    }

    open fun onLoadingStateChanged() {
        // No-default-op
    }

    private fun loadNextItemsIfNeeded(recyclerView: RecyclerView?) {
        if (recyclerView == null) return
        if (!isLoading && !isError) {
            val lastVisibleChild = recyclerView.getChildAt(recyclerView.childCount - 1)
            val lastVisiblePos = recyclerView.getChildAdapterPosition(lastVisibleChild)
            val total = itemCount

            if (lastVisiblePos >= total - loadingOffset) {
                // We need to use runnable, since recycler view does not like when we are notifying
                // about changes during scroll callback.
                recyclerView.post { loadNextItems() }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView == null) return
        recyclerView.addOnScrollListener(scrollListener)
        loadNextItemsIfNeeded(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (recyclerView == null) return
        recyclerView.removeOnScrollListener(scrollListener)
    }

    interface LoaderCallbacks {
        fun canLoadNextItems(): Boolean

        fun loadNextItems()
    }

}
