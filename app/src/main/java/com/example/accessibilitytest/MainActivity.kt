package com.example.accessibilitytest

import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var overlayView: TextView? = null
    private lateinit var windowManager: WindowManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        } else {
            showOverlay()
        }
    }

    override fun onResume() {
        super.onResume()

        if (Settings.canDrawOverlays(this) && overlayView == null) {
            showOverlay()
        }
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        overlayView = TextView(this).apply {
            text = "Overlay Test"
            textSize = 18f
            setTextColor(0xFFFFFFFF.toInt())
            setBackgroundColor(0xAA000000.toInt())
            setPadding(30, 16, 30, 16)
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params.y = 100

        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()

        overlayView?.let {
            windowManager.removeView(it)
            overlayView = null
        }
    }
}