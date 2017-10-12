package com.oliveroneill.imagefeedview.example.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.oliveroneill.imagefeedview.example.MainActivity
import java.io.InputStream

@GlideModule
class DataUriGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context?, glide: Glide?, registry: Registry?) {
        registry!!.prepend(DataUriImage::class.java,
                InputStream::class.java,
                DelayedDataUriImageModelLoader.Factory(MainActivity.IMAGE_LOADING_DELAY)
        )
        super.registerComponents(context, glide, registry)
    }
}