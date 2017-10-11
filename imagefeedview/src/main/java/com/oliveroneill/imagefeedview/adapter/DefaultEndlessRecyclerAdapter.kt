package com.oliveroneill.imagefeedview.adapter

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.oliveroneill.imagefeedview.R

/**
 * Converted from alexvasilkov's GestureViews 'Advanced Demo' in the sample app
 * https://github.com/alexvasilkov/GestureViews/blob/master/sample/src/main/java/com/alexvasilkov/gestures/sample/ui/ex6/adapter/DefaultEndlessRecyclerAdapter.java
 *
 */
abstract class DefaultEndlessRecyclerAdapter<VH : RecyclerView.ViewHolder> : EndlessRecyclerAdapter<RecyclerView.ViewHolder>() {
    companion object {
        private val EXTRA_LOADING_TYPE = Integer.MAX_VALUE
        private val EXTRA_ERROR_TYPE = Integer.MAX_VALUE - 1
    }
    abstract val count: Int

    private val spanSizes = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(pos: Int): Int {
            val spanLookup = originalSpanLookup
            return if (pos == count && (isLoading || isError))
                spanCount
            else spanLookup?.getSpanSize(pos) ?: 1
        }
    }

    private var originalSpanLookup: GridLayoutManager.SpanSizeLookup? = null
    private var spanCount: Int = 0

    private var oldIsLoading: Boolean = false
    private var oldIsError: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            EXTRA_LOADING_TYPE -> LoadingViewHolder(parent)
            EXTRA_ERROR_TYPE -> ErrorViewHolder(parent, this)
            else -> onCreateHolder(parent, viewType)
        }
    }

    protected abstract fun onCreateHolder(parent: ViewGroup, viewType: Int): VH

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int,
                         payloads: List<Any>) {
        onBindViewHolder(holder, position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> onBindLoadingView(holder.loading)
            is ErrorViewHolder -> onBindErrorView(holder.error)
            else -> onBindHolder(holder as VH, position)
        }
    }

    protected abstract fun onBindLoadingView(loadingText: TextView)

    protected abstract fun onBindErrorView(errorText: TextView)

    protected abstract fun onBindHolder(holder: VH, position: Int)

    override fun getItemViewType(position: Int): Int {
        if (position == count) {
            if (isLoading) {
                return EXTRA_LOADING_TYPE
            } else if (isError) {
                return EXTRA_ERROR_TYPE
            }
        }

        val type = getViewType(position)

        if (type == EXTRA_LOADING_TYPE) {
            throw IllegalArgumentException(
                    "Cannot use $EXTRA_LOADING_TYPE as view type")
        }
        if (type == EXTRA_ERROR_TYPE) {
            throw IllegalArgumentException("Cannot use $EXTRA_ERROR_TYPE as view type")
        }

        return type
    }

    private fun getViewType(position: Int): Int {
        return 0
    }

    override fun onLoadingStateChanged() {
        super.onLoadingStateChanged()

        if (oldIsLoading) {
            if (isError) {
                notifyItemChanged(count) // Switching to error view
            } else if (!isLoading) {
                notifyItemRemoved(count) // Loading view is removed
            }
        } else if (oldIsError) {
            if (isLoading) {
                notifyItemChanged(count) // Switching to loading view
            } else if (!isError) {
                notifyItemRemoved(count) // Error view is removed
            }
        } else {
            if (isLoading || isError) {
                notifyItemInserted(count) // Showing loading or error view
            }
        }

        oldIsError = isError
        oldIsLoading = isLoading
    }

    override fun getItemCount(): Int {
        return count + if (isLoading || isError) 1 else 0
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView == null) return
        if (recyclerView.layoutManager is GridLayoutManager) {
            val gridManager = recyclerView.layoutManager as GridLayoutManager
            spanCount = gridManager.spanCount
            originalSpanLookup = gridManager.spanSizeLookup
            gridManager.spanSizeLookup = spanSizes
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (recyclerView == null) return
        if (recyclerView.layoutManager is GridLayoutManager) {
            val gridManager = recyclerView.layoutManager as GridLayoutManager
            gridManager.spanSizeLookup = originalSpanLookup
            originalSpanLookup = null
            spanCount = 1
        }
    }

    private class LoadingViewHolder internal constructor(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_extra_loading, parent, false)) {
        internal val loading: TextView = itemView.findViewById(R.id.extra_loading_text)
    }

    private class ErrorViewHolder internal constructor(parent: ViewGroup, adapter: EndlessRecyclerAdapter<*>) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_extra_error, parent, false)) {
        internal val error: TextView = itemView.findViewById(R.id.extra_error)
        init {
            error.setOnClickListener { adapter.reloadNextItemsIfError() }
        }
    }
}