package com.microsoft.fluentmotion.ui.xml.base

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.model.KeyPath
import com.microsoft.fluentmotion.R
import com.microsoft.fluentmotion.ui.accessibility.IAccessibleMotionView
import com.microsoft.fluentmotion.ui.actions.CancellationError
import com.microsoft.fluentmotion.ui.actions.ICancellable
import com.microsoft.fluentmotion.ui.telemetry.ITelemetryLoggable
import com.microsoft.fluentmotion.ui.telemetry.TelemetryEvent
import com.microsoft.fluentmotion.ui.util.LottieThemeColorHelper

/**
 * A custom view that extends LottieAnimationView to support theming by changing colors dynamically.
 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 * @param defStyleAttr An attribute in the current theme that contains a reference to a style resource that supplies default values for the view. Set to 0 to not look for defaults.
 */
class ThemeableAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LottieAnimationView(context, attrs, defStyleAttr),
    IAccessibleMotionView,
    ITelemetryLoggable,
    ICancellable {

    // accessibility text
    override var onEnterText: String? = null
    override var onInText: String? = null
    override var onExitText: String? = null

    // cancel
    override var onCancelAction: ((CancellationError) -> Unit)? = null

    init {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ThemeableAnimationView, 0, 0)
            onEnterText = a.getString(
                R.styleable.ThemeableAnimationView_on_enter_text,
            )
            onInText = a.getString(
                R.styleable.ThemeableAnimationView_on_in_text,
            )
            onExitText = a.getString(
                R.styleable.ThemeableAnimationView_on_exit_text,
            )
            a.recycle()
        }
    }

    /**
     * Apply a color theme to the animation.
     * @param leftDotColor Color integer for the left dot element in the animation.
     * @param middleDotColor Color integer for the middle dot element in the animation.
     * @param rightDotColor Color integer for the right dot element in the animation.
     * @param greyDotColor Color integer for all grey dot elements in the animation.
     */
    fun applyTheme(
        @ColorInt leftDotColor: Int,
        @ColorInt middleDotColor: Int,
        @ColorInt rightDotColor: Int,
        @ColorInt greyDotColor: Int
    ) {
        // Update the color for the 'left' dot in the animation using a key path.
        LottieThemeColorHelper.updateColorForKeyPath(
            this,
            leftDotColor,
            KeyPath("**", "left", "fill_color_left"),
        )

        // Update the color for the 'middle' dot in the animation using a key path.
        LottieThemeColorHelper.updateColorForKeyPath(
            this,
            middleDotColor,
            KeyPath("**", "middle", "fill_color_middle"),
        )

        // Update the color for the 'right' dot in the animation using a key path.
        LottieThemeColorHelper.updateColorForKeyPath(
            this,
            rightDotColor,
            KeyPath("**", "right", "fill_color_right"),
        )

        // Update the color for the 'grey' dots on the left side in the animation using a key path.
        LottieThemeColorHelper.updateColorForKeyPath(
            this,
            greyDotColor,
            KeyPath("**", "fill_grey_left"),
        )

        // Update the color for the 'grey' dots in the middle in the animation using a key path.
        LottieThemeColorHelper.updateColorForKeyPath(
            this,
            greyDotColor,
            KeyPath("**", "fill_grey_middle"),
        )

        // Update the color for the 'grey' dots on the right side in the animation using a key path.
        LottieThemeColorHelper.updateColorForKeyPath(
            this,
            greyDotColor,
            KeyPath("**", "fill_grey_right"),
        )
    }

    /**
     * Log telemetry for animation changes
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TODO("Not yet implemented")
    }
}
