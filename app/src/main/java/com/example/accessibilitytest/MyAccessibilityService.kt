package com.example.accessibilitytest

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.content.Intent
class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val rootNode = rootInActiveWindow ?: return

        Log.d("AccessibilityTest", "===== 화면 변경 감지 =====")
        Log.d("AccessibilityTest", "Package: ${event?.packageName}")

        printNode(rootNode, 0)

        val targetNode = findNodeByText(rootNode, "고속/시외")

        targetNode?.let { node ->
            val bounds = android.graphics.Rect()
            node.getBoundsInScreen(bounds)

            Log.d("AccessibilityTest", "TARGET_FOUND = 고속/시외")
            Log.d("AccessibilityTest", "TARGET_BOUNDS = $bounds")

            val intent = Intent(this, OverlayService::class.java).apply {
                putExtra("left", bounds.left)
                putExtra("top", bounds.top)
                putExtra("right", bounds.right)
                putExtra("bottom", bounds.bottom)
            }

            startService(intent)
        }
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

    private fun findNodeByText(
        node: AccessibilityNodeInfo?,
        target: String
    ): AccessibilityNodeInfo? {

        if (node == null) return null

        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()

        if (text?.contains(target) == true ||
            desc?.contains(target) == true
        ) {
            return node
        }

        for (i in 0 until node.childCount) {
            val result = findNodeByText(node.getChild(i), target)

            if (result != null) {
                return result
            }
        }

        return null
    }
}