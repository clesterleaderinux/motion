package tfmf.mobile.ui.xml.layouts

import android.animation.Animator
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.microsoft.fluentmotion.R
import tfmf.mobile.ui.actions.CancellationError
import tfmf.mobile.ui.actions.ICancellable
import tfmf.mobile.ui.telemetry.ITelemetryLoggable
import tfmf.mobile.ui.telemetry.TelemetryEvent
import tfmf.mobile.ui.telemetry.TelemetryLogger

/**
 * A wrapper for a rootView that contains multiple animations in
 * targeted positions or full screen
 * @param context The context in which to create the view
 * @param attrs The attributes to apply to the view
 * @param defStyleAttr The default style to apply to the view
 */
class MotionViewGroupFullScreen @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : MotionViewFrameLayout(context, attrs, defStyleAttr, defStyleRes),
    ITelemetryLoggable,
    ICancellable {

    // The layer where motion views or animations will be added and played.
    var targetViewLayer: MotionViewFrameLayout = MotionViewFrameLayout(context)

    // A transparent view that can be used as needed (e.g., as an overlay).
    var transparentView: View = View(context)

    // A unique key for identifying the group full screen container.
    var motionViewGroupFullScreenContainerKey: String? = null

    // Runnable task to execute after the animation ends.
    var onEndAnimationTask: Runnable? = null

    // X coordinate target position for playing animations.
    var xTarget: Float = 0f

    // Y coordinate target position for playing animations.
    var yTarget: Float = 0f

    // cancel animation
    override var onCancelAction: ((CancellationError) -> Unit)? = null

    init {
        // If attributes are provided, process them to initialize properties.
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.MotionViewGroupFullScreen)
            // Retrieves the key defined in XML.
            motionViewGroupFullScreenContainerKey =
                a.getString(R.styleable.MotionViewGroupFullScreen_motion_full_screen_container_key)
            // Retrieves the target X-coordinate defined in XML.
            xTarget =
                a.getFloat(R.styleable.MotionViewGroupFullScreen_motion_full_screen_container_x_target, 0f)
            // Retrieves the target Y-coordinate defined in XML.
            yTarget =
                a.getFloat(R.styleable.MotionViewGroupFullScreen_motion_full_screen_container_y_target, 0f)
            a.recycle() // Don't forget to recycle TypedArray.
        }
    }

    // Returns default layout parameters for adding views to this container.
    fun getDefaultLayerLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
        )
    }

    /**
     * Plays a given Lottie animation at the specified location within the target view layer.
     * @param lottieDrawable The LottieDrawable to animate
     * @param x The x-coordinate at which to animate
     * @param y The y-coordinate at which to animate
     */
    fun playLottieAnimationAtLocation(
        lottieDrawable: LottieDrawable,
        x: Float,
        y: Float
    ) {
        targetViewLayer.removeAllViews()
        val imageView = ImageView(context).apply {
            this.x = x
            this.y = y
            setImageDrawable(lottieDrawable)
            visibility = View.VISIBLE
        }
        targetViewLayer.bringToFront()
        targetViewLayer.addView(imageView)
        lottieDrawable.playAnimation()
    }

    /**
     * Loads a Lottie animation from resources and plays it at the specified location and size.
     * @param lottieRawRes The raw resource identifier for the Lottie animation
     * @param x The x-coordinate at which to animate
     * @param y The y-coordinate at which to animate
     * @param width The width of the animation
     * @param height The height of the animation
     */
    fun playLottieAnimationDrawableFromRawResAtLocation(
        lottieRawRes: Int,
        x: Float,
        y: Float,
        width: Int,
        height: Int
    ) {
        // Creates a Lottie Drawable and loads the composition asynchronously.
        val lottieDrawable = LottieDrawable().also { drawable ->
            LottieCompositionFactory.fromRawRes(context, lottieRawRes).addListener { composition ->
                drawable.composition = composition
            }
        }
        targetViewLayer.removeAllViews()
        val imageView = ImageView(context).apply {
            this.x = x
            this.y = y
            setImageDrawable(lottieDrawable)
            visibility = View.VISIBLE
            layoutParams = ViewGroup.LayoutParams(width, height)
        }
        // Adds an animator listener to clear views after the animation ends.
        lottieDrawable.addAnimatorListener(
            object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    targetViewLayer.removeAllViews()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationStart(animation: Animator) {}
            },
        )
        targetViewLayer.bringToFront()
        targetViewLayer.addView(imageView)
        lottieDrawable.playAnimation()
    }

    /**
     * Plays a MotionView group animation at the specified location within the target view layer.
     * @param motionGroup The MotionViewFrameLayout to animate
     * @param x The x-coordinate at which to animate
     * @param y The y-coordinate at which to animate
     */
    fun playMotionViewGroupAtLocation(motionGroup: MotionViewFrameLayout, x: Float, y: Float) {
        targetViewLayer.removeAllViews()
        motionGroup.apply {
            this.x = x
            this.y = y
            visibility = View.VISIBLE
        }
        targetViewLayer.bringToFront()
        targetViewLayer.addView(motionGroup)
        motionGroup.enter()
    }

    /**
     * Gets the visible rectangle of the target view and the global offset position.
     * @param finalBoundsInt The Rect in which to store the visible rectangle
     * @param globalOffset The Point in which to store the global offset position
     * @return Whether the global visible rect was computed successfully
     */
    fun getTargetViewVisibleRect(finalBoundsInt: Rect, globalOffset: Point) =
        targetViewLayer.getGlobalVisibleRect(finalBoundsInt, globalOffset)

    /**
     * Log telemetry for animation changes
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TelemetryLogger.logTelemetryForAction(event, log)
    }
}

