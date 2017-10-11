package com.oliveroneill.imagefeedview

import android.content.Context
import android.os.Build
import android.view.View

/**
 * Helper class for padding requirements
 *
 * Created by Oliver O'Neill on 22/09/2017.
 */
object DecorUtils {
    /**
     * Shift view down so that it does not overlap with status bar
     */
    fun setPaddingForStatusBar(view: View, isFixedSize: Boolean) {
        if (isCanHaveTransparentDecor) {
            val height = getStatusBarHeight(view.context)

            view.setPadding(view.paddingLeft, view.paddingTop + height,
                    view.paddingRight, view.paddingBottom)

            if (isFixedSize) {
                view.layoutParams.height += height
            }
        }
    }

    private val isCanHaveTransparentDecor: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    private fun getStatusBarHeight(context: Context): Int {
        return getDimenSize(context, "status_bar_height")
    }

    private fun getDimenSize(context: Context, key: String): Int {
        val resourceId = context.resources.getIdentifier(key, "dimen", "android")
        return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
    }
}