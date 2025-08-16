package com.microsoft.fluentmotion.ui.xml.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.microsoft.fluentmotion.ui.xml.components.MotionViewCascadingLayout
import com.microsoft.fluentmotion.ui.xml.layouts.MotionViewLinearLayout

/**
 * Demo component that shows how to use MotionViewCascadingLayout to animate a set of child views.
 */
class MotionViewCascadingDemoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : MotionViewLinearLayout(context, attrs) {

    private val cascadingLayout = MotionViewCascadingLayout()
    private val demoViews = mutableListOf<View>()

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        // Create demo child views
        repeat(5) { i ->
            val tv = TextView(context).apply {
                text = "Cascading Item ${'$'}{i + 1}"
                textSize = 18f
                setPadding(32, 32, 32, 32)
                gravity = Gravity.CENTER
            }
            demoViews.add(tv)
            addView(tv)
        }
        // Register the views with the cascading layout
        cascadingLayout.appendCascadingViews(
            key = "demo",
            onEnterAction = null,
            onEndAction = null,
            views = demoViews
        )
    }

    /**
     * Call this to play the cascading enter animation.
     */
    fun playCascadingEnter() {
        cascadingLayout.onEnter()
    }

    /**
     * Call this to play the cascading exit animation.
     */
    fun playCascadingExit() {
        cascadingLayout.onExit()
    }
}
