package com.example.windowmanagersample

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi

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

        // 画像の大きさ
        val imageWidth = 300.toDp().toPx()
        val imageHeight = 300.toDp().toPx()

        val layoutParams = FrameLayout.LayoutParams(imageWidth, imageHeight)
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        layoutParams.setMargins(
            100.toDp().toPx(),
            200.toDp().toPx(),
            100.toDp().toPx(),
            100.toDp().toPx())

        imageView!!.layoutParams = layoutParams
    }
}