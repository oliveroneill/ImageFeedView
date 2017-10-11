package com.oliveroneill.imagefeedview.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.oliveroneill.imagefeedview.ImageFeedController
import com.oliveroneill.imagefeedview.R

/**
 * Grid adapter for displaying the feed. Override this for custom grid views
 *
 * Created by Oliver O'Neill on 19/09/2017.
 */
open class PhotoListAdapter<in T>(
        private val photos: ArrayList<T>,
        private val controller: ImageFeedController<T>
) : DefaultEndlessRecyclerAdapter<PhotoListAdapter.ViewHolder>(),
    View.OnClickListener {
    private var listener: OnPhotoListener? = null

    fun setListener(l : OnPhotoListener) {
        this.listener = l
    }

    open fun insert(photos: List<T>) {
        val prevSize = this.photos.size
        this.photos.addAll(photos)
        notifyItemRangeInserted(prevSize, photos.size)
    }

    open fun clear() {
        this.photos.clear()
        notifyDataSetChanged()
    }

    override val count: Int
        get() = photos.size

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val holder = ViewHolder(parent)
        holder.image.setOnClickListener(this)
        return holder
    }

    override fun onBindLoadingView(loadingText: TextView) {
        loadingText.setText(R.string.loading_images)
    }

    override fun onBindHolder(holder: ViewHolder, position: Int) {
        val photo = photos[position]
        holder.image.setTag(R.id.tag_item, photo)
        controller.loadImage(photo, holder.image, null)
    }

    override fun onBindErrorView(errorText: TextView) {
        errorText.setText(R.string.error_on_load_images)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is ViewHolder) {
            controller.recycleImage(holder.image)
        }
    }

    override fun onClick(view: View) {
        val photo = view.getTag(R.id.tag_item)
        val pos = photos.indexOf(photo)
        listener?.onPhotoClick(pos)
    }

    open class ViewHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)) {
        open val image: ImageView = itemView.findViewById(R.id.example_row_iv_image)
    }

    interface OnPhotoListener {
        fun onPhotoClick(position: Int)
    }
}
