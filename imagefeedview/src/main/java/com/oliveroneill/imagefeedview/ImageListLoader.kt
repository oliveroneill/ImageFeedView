package com.oliveroneill.imagefeedview

/**
 * An interface for loading new images. Used by {@link ImageFeedView}. In most cases you should
 * use {@link DefaultImageListLoader}.
 *
 * Created by Oliver O'Neill on 7/10/2017.
 */
interface ImageListLoader<T> {
    /**
     * @param callback new images are returned through the callback asynchronously
     */
    fun getMoreImages(callback: (newImages : List<T>) -> Unit)
}