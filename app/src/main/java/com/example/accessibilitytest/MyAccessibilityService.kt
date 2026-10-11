package com.example.accessibilitytest

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return

        Log.d("AccessibilityTest", "===== 화면 변경 감지 =====")
        Log.d("AccessibilityTest", "Package: ${event?.packageName}")

        printNode(rootNode, 0)
    }

    override fun onInterrupt() {
        Log.d("AccessibilityTest", "AccessibilityService interrupted")
    }

    private fun printNode(node: AccessibilityNodeInfo?, depth: Int) {
        if (node == null) return

        val indent = "  ".repeat(depth)

        val bounds = android.graphics.Rect()
        node.getBoundsInScreen(bounds)

        Log.d(
            "AccessibilityTest",
            "$indent" +
                    "Text=${node.text}, " +
                    "Desc=${node.contentDescription}, " +
                    "State=${node.stateDescription}, " +
                    "Hint=${node.hintText}, " +
                    "ViewId=${node.viewIdResourceName}, " +
                    "Class=${node.className}, " +
                    "Clickable=${node.isClickable}, " +
                    "Enabled=${node.isEnabled}, " +
                    "Visible=${node.isVisibleToUser}"
        )

        for (i in 0 until node.childCount) {
            printNode(node.getChild(i), depth + 1)
        }
    }
}