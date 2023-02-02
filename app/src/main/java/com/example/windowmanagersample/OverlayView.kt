package com.example.windowmanagersample

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.view.*
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
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        0, 80,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )


    private var tmpView = View(context)


    private var imageView: ImageView? = null

    // 画像のレイアウト
    private var imageLayoutParams = LayoutParams(0, 0)

    private var currentX = 0f
    private var currentY = 0f

    var backGroundView : FrameLayout? = null


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

//        layoutParams.gravity = Gravity.START

        // 画像を指定
        imageView!!.setImageResource(R.drawable.cat)

        imageLayoutParams = LayoutParams(300.toDp().toPx(), 300.toDp().toPx())
//        imageLayoutParams.gravity = Gravity.CENTER_HORIZONTAL

//        imageLayoutParams.setMargins(
//            100.toDp().toPx(),
//            200.toDp().toPx(),
//            100.toDp().toPx(),
//            100.toDp().toPx())

        this.setOnLongClickListener { view ->

//            backGroundView = FrameLayout(context)
//            val bgParams = WindowManager.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                PixelFormat.TRANSLUCENT)
            // 背景を設定
//            windowManager.addView(backGroundView, bgParams)

            MyLog.e("setOnLongClickListener")
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
//            this.setBackgroundColor(0)
//
//            val x = imageView!!.pivotX
//            val y = imageView!!.pivotY
            windowManager.updateViewLayout(this, layoutParams)
//
//            imageView!!.pivotX = x
//            imageView!!.pivotY = y
//            imageView!!.invalidate()

//            imageLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
//            imageLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
//            this.updateViewLayout(imageView, imageLayoutParams)

//            tmpView = View(context, null, windowManager.currentWindowMetrics.bounds.width(), windowManager.currentWindowMetrics.bounds.height())
//            tmpView.setBackgroundColor(0xFFFFFF)


//            this.addView(tmpView)

//            tmpView.wi = windowManager.currentWindowMetrics.bounds.width()
//            tmpView.height = windowManager.currentWindowMetrics.bounds.height()

            this.startDragAndDrop(null, View.DragShadowBuilder(imageView), imageView, 0)




        }

        this.setOnDragListener { v, event ->
            when(event. action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    MyLog.e("ACTION_DRAG_STARTED x = ${v.x}, y = ${v.y}")


                    true
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    MyLog.e("ACTION_DRAG_ENTERED x = ${v.x}, y = ${v.y}")


                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    MyLog.e("ACTION_DRAG_LOCATION x = ${event.x}, y = ${event.y}")
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    MyLog.e("ACTION_DRAG_EXITED x = ${v.x}, y = ${v.y}")

                    true
                }
                DragEvent.ACTION_DROP -> {
                    layoutParams.x = event.x.toInt() - 150
                    layoutParams.y = event.y.toInt() - 150
                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
                    windowManager.updateViewLayout(this, layoutParams)
                    MyLog.e("ACTION_DROP x = ${event.x}, y = ${event.y}")
                    MyLog.e("ACTION_DROP x = ${v.x}, y = ${v.y}")

                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    MyLog.e("ACTION_DRAG_ENDED x = ${v.x}, y = ${v.y}")
                    MyLog.e("ACTION_DRAG_ENDED x = ${event.x}, y = ${event.y}")

//                    layoutParams.x = event.x.toInt()
//                    layoutParams.y = event.y.toInt()
//                    layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
//                    layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
//                    windowManager.updateViewLayout(this, layoutParams)
//                    windowManager.removeView(backGroundView!!)
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