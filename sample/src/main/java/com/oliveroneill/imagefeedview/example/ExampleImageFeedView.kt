package com.oliveroneill.imagefeedview.example

import android.content.Context
import android.util.AttributeSet
import com.oliveroneill.imagefeedview.ImageFeedView

class ExampleImageFeedView : ImageFeedView<ExampleImage> {
    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
}