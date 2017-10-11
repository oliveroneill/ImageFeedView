package com.oliveroneill.imagefeedview.adapter

import android.support.v4.view.ViewPager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alexvasilkov.gestures.Settings
import com.alexvasilkov.gestures.commons.RecyclePagerAdapter
import com.alexvasilkov.gestures.views.GestureImageView
import com.oliveroneill.imagefeedview.ImageFeedController
import com.oliveroneill.imagefeedview.R

/**
 * Adapter for photo viewer. Override this class to specify your own custom views
 *
 * Created by Oliver O'Neill on 16/09/2017.
 */
open class PhotoPagerAdapter<T>(
        private val photos: ArrayList<T>,
        private val controller: ImageFeedController<T>
) : RecyclePagerAdapter<PhotoPagerAdapter.ViewHolder>() {

    private var activated: Boolean = false
    private var viewPager: ViewPager? = null

    fun setPager(viewPager: ViewPager) {
        this.viewPager = viewPager
    }

    open fun insert(photos: List<T>) {
        this.photos.addAll(photos)
        if (activated) notifyDataSetChanged()
    }

    open fun clear() {
        this.photos.clear()
        notifyDataSetChanged()
    }

    /**
     * To prevent ViewPager from holding heavy views (with bitmaps)  while it is not showing
     * we may just pretend there are no items in this adapter ("activate" = false).
     * But once we need to run opening animation we should "activate" this adapter again.<br></br>
     * Adapter is not activated by default.
     */
    fun setActivated(activated: Boolean) {
        if (this.activated != activated) {
            this.activated = activated
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int {
        return if (!activated) 0 else photos.size
    }

    override fun onCreateViewHolder(container: ViewGroup): ViewHolder {
        val view = LayoutInflater.from(container.context).inflate(
                R.layout.photo_layout,
                container, false
        )
        val holder = ViewHolder(
                view,
                view.findViewById(R.id.photo_full_image)
        )

        // configure GestureViews settings
        holder.image.controller.settings
                .setMaxZoom(10f)
                .setDoubleTapZoom(5f)
                .setDoubleTapEnabled(true)
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setRotationEnabled(false)
                .setRestrictRotation(true)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f)
                .setFillViewport(true)
                .setFitMethod(Settings.Fit.INSIDE)
                .gravity = Gravity.CENTER

        viewPager?.let {
            holder.image.controller.enableScrollInViewPager(it)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // TODO: check error condition using listener argument
        controller.loadImage(photos[position], holder.image, null)
    }

    override fun onRecycleViewHolder(holder: ViewHolder) {
        super.onRecycleViewHolder(holder)
        controller.recycleImage(holder.image)
        holder.image.setImageDrawable(null)
    }

    open class ViewHolder(view: View, open val image: GestureImageView) :RecyclePagerAdapter.ViewHolder(view)
}
