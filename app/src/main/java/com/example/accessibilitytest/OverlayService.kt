package com.example.accessibilitytest

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val left = intent?.getIntExtra("left", 0) ?: 0
        val top = intent?.getIntExtra("top", 0) ?: 0
        val right = intent?.getIntExtra("right", 0) ?: 0
        val bottom = intent?.getIntExtra("bottom", 0) ?: 0

        showHighlight(left, top, right, bottom)

        return START_NOT_STICKY
    }

    private fun showHighlight(
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    ) {
        val width = right - left
        val height = bottom - top

        if (width <= 0 || height <= 0) return

        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }

        val borderDrawable = GradientDrawable().apply {
            setColor(0x00000000)
            setStroke(6, 0xFFFF0000.toInt())
            cornerRadius = 20f
        }

        val view = View(this).apply {
            background = borderDrawable
        }

        val params = WindowManager.LayoutParams(
            width,
            height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = left
        params.y = top

        windowManager.addView(view, params)
        overlayView = view
    }

    override fun onDestroy() {
        super.onDestroy()

        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}