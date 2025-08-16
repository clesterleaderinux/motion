package com.microsoft.fluentmotion.ui.xml.components

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.microsoft.fluentmotion.ui.Alpha
import com.microsoft.fluentmotion.ui.MotionDuration
import com.microsoft.fluentmotion.ui.MotionTypeKey
import com.microsoft.fluentmotion.ui.Scale
import com.microsoft.fluentmotion.ui.xml.base.MotionViewBase
import kotlin.math.absoluteValue

/**
 * A custom [TabLayout.OnTabSelectedListener] that provides additional motion animation when changing tabs.
 *
 * @property viewPager The associated [ViewPager2] instance which will be animated upon tab selection.
 */
class MotionViewTabSelectionListener(private val viewPager: ViewPager2) : TabLayout.OnTabSelectedListener {
    // Tracks the current selected tab index.
    private var currentTab = 0

    /**
     * Called when a tab enters the selected state.
     *
     * @param tab The tab that was selected.
     */
    override fun onTabSelected(tab: TabLayout.Tab) {
        // Determine if we should animate based on the distance between the current and targeted tab.
        val shouldAnimate = (currentTab - tab.position).absoluteValue > 1

        // If we should animate, proceed with setting up animations.
        if (shouldAnimate) {
            // Animate the ViewPager to move to the selected tab's position.
            viewPager.setCurrentItem(tab.position, true)

            // Set up and start the enter animation for scaling and alpha changes.
            viewPager.let {
                // Define the initial values for scaling and alpha transitions.
                val scaleInAlphaValues = hashMapOf(
                    MotionTypeKey.Alpha.name to Alpha(aEnter = 1f, aIn = 0.3f, aExit = 0.3f),
                    MotionTypeKey.Scale.name to Scale(sEnter = 1f, sIn = 0.9f, sExit = 0.9f),
                )
                // Initialize the animation with the view and these values.
                val scaleInAlpha = MotionViewBase(
                    motionViewBase = it,
                    motionValues = scaleInAlphaValues,
                    duration = MotionDuration.ViewPagerOut,
                )

                // Define the values for the subsequent scaling and alpha transitions.
                val motionValues = hashMapOf(
                    MotionTypeKey.Alpha.name to Alpha(aEnter = 0.3f, aIn = 1f, aExit = 0.3f),
                    MotionTypeKey.Scale.name to Scale(sEnter = 0.9f, sIn = 1f, sExit = 0.9f),
                )
                // Prepare the secondary animation with these new values.
                val scaleUpAlpha = MotionViewBase(motionViewBase = it, motionValues = motionValues)

                // Trigger the first part of the animation immediately.
                scaleInAlpha.enter()

                // Schedule the second part of the animation after the specified delay.
                viewPager.postDelayed(
                    {
                        scaleUpAlpha.enter()
                    },
                    MotionDuration.ViewPagerOut.speedInMillis,
                )
            }
        } else {
            // If no animation is needed, simply set the current item to the target position.
            viewPager.setCurrentItem(tab.position, true)
        }

        // Update the current tab position to the new position.
        currentTab = tab.position
    }

    /**
     * Called when a tab exits the selected state.
     *
     * @param tab The tab that was unselected.
     */
    override fun onTabUnselected(tab: TabLayout.Tab) {
        // No custom behavior defined for unselecting tabs.
    }

    /**
     * Called when a tab that is already selected is chosen again by the user.
     *
     * @param tab The tab that was reselected.
     */
    override fun onTabReselected(tab: TabLayout.Tab) {
        // No custom behavior defined for reselecting tabs.
    }
}
