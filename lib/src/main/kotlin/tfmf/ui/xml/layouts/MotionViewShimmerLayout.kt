package com.microsoft.fluentmotion.ui.xml.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import com.microsoft.fluentmotion.ui.Alpha
import com.microsoft.fluentmotion.ui.MotionDuration
import com.microsoft.fluentmotion.ui.MotionTypeKey
import com.microsoft.fluentmotion.ui.MotionValue
import com.microsoft.fluentmotion.ui.Scale
import com.microsoft.fluentmotion.ui.actions.CancellationError
import com.microsoft.fluentmotion.ui.xml.base.MotionViewBase

/**
 * A custom `LinearLayout` that supports shimmer effects and motion transitions.
 * This layout is designed to provide visual feedback through shimmer and motion effects
 * during data loading or content transitions. It extends the functionality of a standard
 * `LinearLayout` by integrating motion properties and transitions.
 *
 * @property context The Context the view is running in, through which it can access the current theme, resources, etc.
 * @property attrs The attributes of the XML tag that is inflating the view.
 * @property defStyleAttr An attribute in the current theme that contains a reference to a style resource to apply to this view.
 * @property defStyleRes A resource identifier of a style resource that supplies default values for the view, used only if defStyleAttr is 0 or can not be found in the theme.
 */
class MotionViewShimmerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * A map holding the motion properties for various states of the view. Each entry in the map
     * corresponds to a specific motion type (e.g., alpha, scale) and its associated values for
     * different states (enter, in, exit). This allows for customizable and dynamic motion effects
     * based on the view's state.
     */
    private val motionValues: Map<String, MotionValue?> = hashMapOf(
        MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0f),
        MotionTypeKey.Scale.name to Scale(sEnter = 0.9f, sIn = 1f, sExit = 0.8f)
    )

    private val mvb = MotionViewBase(
        motionViewBase = this,
        motionValues = motionValues,
        duration = MotionDuration.DurationMedium01,
    )

    /**
     * Triggers the 'enter' motion transition, applying the predefined motion effects to the view.
     * This method should be called when the view is entering the screen or becoming visible to
     * the user, to enhance the visual experience with a smooth transition.
     */
    fun enter() {
        mvb.enter()
    }

    /**
     * Initiates the 'exit' motion transition, applying the predefined motion effects to the view.
     * This method should be used when the view is leaving the screen or becoming invisible to
     * provide a smooth and visually appealing transition out.
     */
    fun exit() {
        mvb.exit()
    }

    /**
     * Cancels any ongoing motion transitions and resets the view's motion state. This is particularly
     * useful when the view's visibility changes unexpectedly, and any ongoing transitions need to be
     * halted to prevent visual glitches or inconsistencies.
     */
    fun cancel(cancellationError: CancellationError) {
        mvb.cancel(cancellationError)
    }
}