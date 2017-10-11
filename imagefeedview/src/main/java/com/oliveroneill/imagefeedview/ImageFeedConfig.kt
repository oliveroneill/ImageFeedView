package com.oliveroneill.imagefeedview

import android.support.v7.widget.Toolbar
import com.oliveroneill.imagefeedview.adapter.PhotoListAdapter
import com.oliveroneill.imagefeedview.adapter.PhotoPagerAdapter

/**
 * Configuration for {@link ImageFeedView}
 *
 * Created by Oliver O'Neill on 3/10/2017.
 */
class ImageFeedConfig<T>(val controller:ImageFeedController<T>) {
    internal var toolbar:Toolbar? = null
    internal var translucent:Boolean? = null
    internal var gridAdapter: PhotoListAdapter<T>? = null
    internal var pagerAdapter: PhotoPagerAdapter<T>? = null

    /**
     * If you're using a toolbar, you should call this method to ensure that the toolbar is
     * properly hidden when using the photo viewer
     */
    fun setToolbar(toolbar : Toolbar) : ImageFeedConfig<T> {
        this.toolbar = toolbar
        return this
    }

    /**
     * When using a translucent status bar, you should call this method to ensure that the feed
     * is correctly position below the status bar
     */
    fun setTranslucentStatusBar(translucent:Boolean) : ImageFeedConfig<T> {
        this.translucent = translucent
        return this
    }

    /**
     * For custom grid views
     */
    fun setGridAdapter(adapter : PhotoListAdapter<T>) : ImageFeedConfig<T> {
        this.gridAdapter = adapter
        return this
    }

    /**
     * For custom photo viewer setup
     */
    fun setPagerAdapter(adapter : PhotoPagerAdapter<T>) : ImageFeedConfig<T> {
        this.pagerAdapter = adapter
        return this
    }
}