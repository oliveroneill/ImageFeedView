package com.oliveroneill.imagefeedview

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.widget.LinearLayout
import com.alexvasilkov.gestures.animation.ViewPositionAnimator
import com.alexvasilkov.gestures.transition.GestureTransitions
import com.alexvasilkov.gestures.transition.ViewsTransitionAnimator
import com.alexvasilkov.gestures.transition.tracker.SimpleTracker
import com.oliveroneill.imagefeedview.adapter.EndlessRecyclerAdapter
import com.oliveroneill.imagefeedview.adapter.PhotoListAdapter
import com.oliveroneill.imagefeedview.adapter.PhotoPagerAdapter

/**
 * A view component that will show a grid of images and allow you to navigate through them in a
 * pager style when tapped.
 *
 * To use this, you can add:
 *  <com.oliveroneill.imagefeedview.ImageFeedView
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"/>
 * You will then need to cast it to specify your own Image class as generic T. Alternatively,
 * you can create a wrapper class that extends ImageFeedView<YourCustomClass> and then use that
 * in your layout file.
 *
 * To setup the feed, you must call {@link #show} with {@link ImageFeedConfig}, which allows you to
 * specify whether there's a {@link Toolbar} in use and whether the status bar should be
 * translucent.
 * You must also specify the {@link ImageFeedController} for loading and recycling images.
 *
 * Created by Oliver O'Neill on 27/09/2017.
 */
open class ImageFeedView<T> : LinearLayout, ViewPositionAnimator.PositionUpdateListener, PhotoListAdapter.OnPhotoListener {
    // keep track of when to load more images
    private var lastLoadedIndex = -1
    private var collectionSize = 0
    private var hasMore = true
    // configured data controllers
    private lateinit var controller : ImageFeedController<T>
    private lateinit var feedLoader : ImageListLoader<T>

    // views and pager setup
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var grid: RecyclerView
    private lateinit var pager: ViewPager
    private lateinit var pagerBackground: View
    private lateinit var gridAdapter: PhotoListAdapter<T>
    private lateinit var pagerAdapter: PhotoPagerAdapter<T>
    private lateinit var animator: ViewsTransitionAnimator<Int>
    private lateinit var pagerToolbar : Toolbar

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    /**
     * Call this to configure and show the image feed
     * @param config specify a controller and other settings for the feed
     */
    fun show(config:ImageFeedConfig<T>) {
        setController(config.controller)
        setFeedLoader(DefaultImageListLoader(controller))
        setGridAdapter(config.gridAdapter, controller)
        setPagerAdapter(config.pagerAdapter, controller)
        initViews()
        config.toolbar?.let { setToolbar(it) }
        config.translucent?.let { setTranslucentStatusBar(it) }
    }

    /**
     * Generally {@link #show} should be used to setup the loader. This is primarily used for unit
     * testing
     */
    internal fun setFeedLoader(loader:ImageListLoader<T>) {
        this.feedLoader = loader
    }

    /**
     * Generally {@link #show} should be used to set the controller. This is primarily used for unit
     * testing
     */
    internal fun setController(controller : ImageFeedController<T>) {
        this.controller = controller
    }

    internal fun setGridAdapter(adapter : PhotoListAdapter<T>?, controller: ImageFeedController<T>) {
        if (adapter != null)
            this.gridAdapter = adapter
        else
            this.gridAdapter = PhotoListAdapter(ArrayList(), controller)
    }

    internal fun setPagerAdapter(adapter : PhotoPagerAdapter<T>?, controller: ImageFeedController<T>) {
        if (adapter != null)
            this.pagerAdapter = adapter
        else
            this.pagerAdapter = PhotoPagerAdapter(ArrayList(), controller)
    }

    private fun initViews() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.image_feed, this)

        pagerBackground = view.findViewById(R.id.advanced_full_background)
        initGrid(view)
        initPager(view)
        initAnimator()

        setSwipeRefresh(view.findViewById(R.id.swipe_refresh_layout))
    }

    internal fun setSwipeRefresh(swipeRefreshLayout: SwipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            refresh()
        }
    }

    /**
     * Will refresh the image feed and clear all current images
     */
    fun refresh() {
        controller.clear()
        gridAdapter.clear()
        pagerAdapter.clear()
        loadMore()
    }

    private fun initGrid(view:View) {
        grid = view.findViewById(R.id.grid_view)
        // this value will be different for portrait and landscape
        val columns = resources.getInteger(R.integer.images_grid_columns)
        grid.layoutManager = GridLayoutManager(context, columns)
        initGridAdapter(grid, columns)
    }

    internal fun initGridAdapter(grid : RecyclerView, columns : Int) {
        gridAdapter.setListener(this)
        // should start loading 3 columns from the end
        gridAdapter.setLoadingOffset(3 * columns)
        gridAdapter.setCallbacks(object : EndlessRecyclerAdapter.LoaderCallbacks {
            override fun canLoadNextItems(): Boolean {
                return hasMore
            }
            override fun loadNextItems() {
                loadMore()
            }
        })
        grid.adapter = gridAdapter
    }

    private fun initPager(view:View) {
        pager = view.findViewById(R.id.photo_pager)
        pager.visibility = View.VISIBLE

        // This toolbar is used for additional views and a back button
        // At some point this will be configurable
        pagerToolbar = view.findViewById(R.id.pager_toolbar)
        pagerToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
        DecorUtils.setPaddingForStatusBar(pagerToolbar, true)

        initPagerAdapter(pager)
    }

    private fun onBackPressed() {
        if (!animator.isLeaving) {
            animator.exit(true)
        }
    }

    internal fun initPagerAdapter(pager : ViewPager) {
        // Setting up pager views
        pagerAdapter.setPager(pager)
        pager.adapter = pagerAdapter
        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                onPageChange(position)
            }
        })
        pager.currentItem = 0
    }

    /**
     * The animator animates the transition between grid and pager, and vice versa
     */
    private fun initAnimator() {
        val gridTracker = object : SimpleTracker() {
            public override fun getViewAt(pos: Int): View? {
                val holder = grid.findViewHolderForLayoutPosition(pos)
                return if (holder == null) null else (holder as PhotoListAdapter.ViewHolder).image
            }
        }

        val pagerTracker = object : SimpleTracker() {
            public override fun getViewAt(pos: Int): View? {
                return pagerAdapter.getViewHolder(pos)?.image
            }
        }

        animator = GestureTransitions.from<Int>(grid, gridTracker).into(pager, pagerTracker)
        animator.addPositionUpdateListener(this)
    }

    /**
     * This is called for each frame of the animation created by animator
     * It will update visibility and alpha levels accordingly
     */
    override fun onPositionUpdate(position: Float, isLeaving: Boolean) {
        pagerBackground.visibility = if (position == 0f) View.INVISIBLE else View.VISIBLE
        pagerBackground.background.alpha = (255 * position).toInt()

        pagerToolbar.visibility = if (position == 0f) View.INVISIBLE else View.VISIBLE
        pagerToolbar.alpha = position

        if (isLeaving && position == 0f) {
            pagerAdapter.setActivated(false)
        }
    }

    internal fun onPageChange(position: Int) {
        // Check whether we are at the last five images, or at the end of the list
        if (((position > collectionSize - 5) && collectionSize > lastLoadedIndex + 5) ||
                (position == collectionSize - 1)){
            lastLoadedIndex = position
            loadMore()
        }
    }

    internal fun loadMore() {
        feedLoader.getMoreImages {
            insert(it)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    internal fun insert(items:List<T>) {
        hasMore = if (items.isNotEmpty()) {
            gridAdapter.insert(items)
            pagerAdapter.insert(items)
            true
        } else {
            // if there's nothing more to insert then we're done and won't attempt to load anymore
            // images
            false
        }
        gridAdapter.onNextItemsLoaded()
        collectionSize += items.size
    }

    override fun onPhotoClick(position: Int) {
        // set up pager so that back button will only exit the pager view
        pager.isFocusableInTouchMode = true
        pager.requestFocus()
        pager.setOnKeyListener(OnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed()
                return@OnKeyListener true
            }
            false
        })
        // set up toolbar navigation
        pagerToolbar.setNavigationOnClickListener { onBackPressed() }
        pagerAdapter.setActivated(true)
        // start animation
        animator.enter(position, true)
    }

    private fun setToolbar(toolbar : Toolbar) {
        if (toolbar.parent is ViewGroup) {
            // remove the toolbar from its current position
            val parent = toolbar.parent as ViewGroup
            parent.removeView(toolbar)
            // add it after the swipe refresh, this way its in front of the feed but behind the
            // photo viewer
            val feedParent = getChildAt(0) as ViewGroup
            val index = feedParent.indexOfChild(swipeRefreshLayout)
            feedParent.addView(toolbar, index + 1)
        }
    }

    private fun setTranslucentStatusBar(translucent:Boolean) {
        if (translucent) {
            // ensure that the grid is offset below the toolbar
            DecorUtils.setPaddingForStatusBar(grid, true)
        }
    }
}