package com.example.windowmanagersample

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView

class OverlayView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {


    companion object {
        // creates an instance of OverlayView
        fun create(context: Context) =
            View.inflate(context, R.layout.overlay_view, null) as OverlayView
    }

    private var imageView: ImageView? = null

    // 画像のレイアウト
    private var imageLayoutParams = LayoutParams(0, 0)

    fun zoomIn() {
        imageLayoutParams.width += 100.toDp().toPx()
        imageLayoutParams.height += 100.toDp().toPx()
        imageView!!.layoutParams = imageLayoutParams
    }

    fun zoomOut() {
        imageLayoutParams.width -= 100.toDp().toPx()
        imageLayoutParams.height -= 100.toDp().toPx()
        imageView!!.layoutParams = imageLayoutParams
    }

    fun zoom(progress: Int) {
        imageLayoutParams.width = progress
        imageLayoutParams.height = progress
        imageView!!.layoutParams = imageLayoutParams
    }
}