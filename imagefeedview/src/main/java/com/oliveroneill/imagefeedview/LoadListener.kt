package com.oliveroneill.imagefeedview

/**
 * Listen to load events from {@link ImageFeedController}. See {@link ImageFeedController#loadImage}
 *
 * Created by Oliver O'Neill on 10/10/2017.
 */
interface LoadListener {
    fun onLoaded()
    fun onError(e:Exception?)
}