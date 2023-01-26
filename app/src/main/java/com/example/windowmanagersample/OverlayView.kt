package com.example.windowmanagersample

import android.content.Context
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.WindowManager
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

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // settings for overlay view
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        PixelFormat.TRANSLUCENT
    )

    private var imageView: ImageView? = null

    // 画像のレイアウト
    private var imageLayoutParams = LayoutParams(0, 0)


    // starts displaying this view as overlay
    @Synchronized
    fun show() {
        if (!this.isShown) {

            if (imageView == null) {
                createImageView()
                this.addView(imageView)
            }
            windowManager.addView(this, layoutParams)
        }
    }

    @Synchronized
    fun hide() {
        if (this.isShown) {
            windowManager.removeView(this)
        }
    }

    private fun createImageView() {
        // ImageViewのインスタンスを作成
        imageView = ImageView(context)

        // 画像を指定
        imageView!!.setImageResource(R.drawable.cat)

        imageLayoutParams = LayoutParams(300.toDp().toPx(), 300.toDp().toPx())
        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL
        imageLayoutParams.setMargins(
            100.toDp().toPx(),
            200.toDp().toPx(),
            100.toDp().toPx(),
            100.toDp().toPx())

        imageView!!.layoutParams = imageLayoutParams
    }

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