package com.oliveroneill.imagefeedview.example

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Base64
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.oliveroneill.imagefeedview.DecorUtils
import com.oliveroneill.imagefeedview.ImageFeedConfig
import com.oliveroneill.imagefeedview.ImageFeedController
import com.oliveroneill.imagefeedview.LoadListener
import com.oliveroneill.imagefeedview.example.glide.DataUriImage
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity(), ImageFeedController<ExampleImage> {
    // Setup delays and feed query sizes for simulated image feed
    companion object {
        private val QUERY_SIZE = 20
        private val LENGTH = 50
        private val LOADING_DELAY = 1
        val IMAGE_LOADING_DELAY = 0.1
    }

    private var collectionSize = 0
    private var hue = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val feed = findViewById<ExampleImageFeedView>(R.id.image_feed)
        DecorUtils.setPaddingForStatusBar(toolbar, true)
        val config = ImageFeedConfig(this)
                .setToolbar(toolbar)
                .setTranslucentStatusBar(true)
        feed.show(config)
        setSupportActionBar(toolbar)
    }

    override fun getMoreImages(): List<ExampleImage> {
        Thread.sleep((LOADING_DELAY * 1000).toLong())
        val list = ArrayList<ExampleImage>()
        if (collectionSize >= LENGTH) {
            return list
        }
        for (i in 0 until QUERY_SIZE) {
            hue += 1.0 / LENGTH.toDouble()
            val url = generateDataUrl(Color.HSVToColor(floatArrayOf(hue.toFloat() * 360, 1f, 1f)))
            list.add(ExampleImage(url))
        }
        collectionSize += list.size
        return list
    }

    private fun generateDataUrl(color: Int): String {
        val width = 100
        val height = 100
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(color)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return "data:img/png;base64," + Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun loadImage(item: ExampleImage, imgView: ImageView, listener: LoadListener?) {
        Glide.with(this)
                .load(DataUriImage(item.url))
                .into(imgView)
    }

    override fun recycleImage(imgView:ImageView) {
        Glide.with(this).clear(imgView)
    }

    override fun clear() {}
}
