package com.oliveroneill.imagefeedview

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

/**
 * Used to asynchronously load new image metadata for the feed
 *
 * Created by Oliver O'Neill on 7/10/2017.
 */
class DefaultImageListLoader<T>(val controller:ImageFeedController<T>) : ImageListLoader<T> {
    override fun getMoreImages(callback: (newImages : List<T>) -> Unit) {
        async(UI) {
            val data: Deferred<List<T>> = bg {
                controller.getMoreImages()
            }
            val newImages = data.await()
            callback(newImages)
        }
    }
}