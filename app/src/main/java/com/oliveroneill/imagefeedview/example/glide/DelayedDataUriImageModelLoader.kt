package com.oliveroneill.imagefeedview.example.glide

import android.util.Base64
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.ByteArrayInputStream
import java.io.InputStream

class DelayedDataUriImageModelLoader(val delayInSeconds:Double) : ModelLoader<DataUriImage, InputStream> {
    override fun buildLoadData(model: DataUriImage?, width: Int, height: Int, options: Options?): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData<InputStream>(
                GlideUrl(model!!.url),
                object : DataFetcher<InputStream> {
                    override fun loadData(priority: Priority?, callback: DataFetcher.DataCallback<in InputStream>?) {
                        bg {
                            Thread.sleep((delayInSeconds * 1000).toLong())
                            val dataUri = model.url
                            val base64 = dataUri.substring("data:img/png;base64,".length)
                            callback!!.onDataReady(ByteArrayInputStream(Base64.decode(base64, Base64.DEFAULT)))
                        }
                    }

                    override fun cleanup() {
                    }

                    override fun getDataClass(): Class<InputStream> {
                        return InputStream::class.java
                    }

                    override fun getDataSource(): DataSource {
                        return DataSource.LOCAL
                    }

                    override fun cancel() {
                    }
                }
        )
    }

    override fun handles(model: DataUriImage?): Boolean {
        if (model == null) {
            return false
        }
        return true
    }

    class Factory(private val delayInSeconds: Double) : ModelLoaderFactory<DataUriImage, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory?): ModelLoader<DataUriImage, InputStream> {
            return DelayedDataUriImageModelLoader(delayInSeconds)
        }

        override fun teardown() {}
    }
}