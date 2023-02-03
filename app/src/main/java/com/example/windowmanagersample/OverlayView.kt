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

    private val windowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // settings for overlay view
    private val layoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        0, 80,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
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

        layoutParams.gravity = Gravity.TOP or Gravity.START
        // ImageViewのインスタンスを作成
        imageView = ImageView(context)

        // 画像を指定
        imageView!!.setImageResource(R.drawable.cat)

        imageLayoutParams = LayoutParams(300.toDp().toPx(), 300.toDp().toPx())

        this.setOnLongClickListener { view ->
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            windowManager.updateViewLayout(this, layoutParams)

            this.startDragAndDrop(null, View.DragShadowBuilder(imageView), imageView, 0)
        }

        this.setOnDragListener { v, event ->
            when(event. action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    MyLog.e("ACTION_DRAG_LOCATION x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    true
                }
                DragEvent.ACTION_DROP -> {
                    layoutParams.x = event.x.toInt() - 150
                    layoutParams.y = event.y.toInt() - 150
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    windowManager.updateViewLayout(this, layoutParams)
                    MyLog.e("ACTION_DROP x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    MyLog.e("ACTION_DRAG_ENDED x = ${v.x}, y = ${v.y}")
                    MyLog.e("ACTION_DRAG_ENDED x = ${event.x}, y = ${event.y}")
                    true
                }
                else -> {
                    MyLog.e("ACTION_DRAG_ENTERED")

                    false
                }
            }
        }

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