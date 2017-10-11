package com.oliveroneill.imagefeedview

import android.widget.ImageView

/**
 * An interface used for rendering images within views and retrieving new images for the feed
 *
 * Created by Oliver O'Neill on 27/09/2017.
 */
interface ImageFeedController<T> {
    /**
     * Called when item needs to be loaded into the specified image view
     * @param item the image object
     * @param imgView the image should be rendered in this view
     * @param listener load events or errors should be sent through here
     */
    fun loadImage(item:T, imgView: ImageView, listener:LoadListener?)

    /**
     * When the image is no longer needed you can clean up the view
     */
    fun recycleImage(imgView: ImageView)

    /**
     * Returns more images for the feed synchronously. The feed will handle the asynchronous
     * component
     */
    fun getMoreImages() : List<T>

    /**
     * Clear all images in the feed. New requests for {@link #getMoreImages} will expect images
     * from the start of the feed again. This function is called on a refresh
     */
    fun clear()
}