package tfmf.ui.motion.xml.base

import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import tfmf.ui.motion.MotionCurve
import tfmf.ui.motion.MotionDuration
import tfmf.ui.motion.MotionInterpolator
import tfmf.ui.motion.MotionState
import tfmf.ui.motion.MotionValue
import tfmf.ui.motion.accessibility.IAccessibleMotionView
import tfmf.ui.motion.actions.CancellationError
import tfmf.ui.motion.telemetry.ITelemetryLoggable
import tfmf.ui.motion.telemetry.TelemetryEvent
import tfmf.ui.motion.telemetry.TelemetryLogger
import tfmf.ui.motion.util.MotionUtil
import tfmf.ui.motion.xml.player.MotionPlayer

// A class that represents a base for motion-enabled views, conforms to IMotionView interface.
class MotionViewBase @JvmOverloads constructor(
    motionViewBase: View, // The actual view object that will be animated.
    motionValues: Map<String, MotionValue?>, // The initial key-value pairs for various motion properties.
    chainKey: String? = null, // An optional identifier for associating this view with a motion chain.
    chainIndex: Int = 0, // The index of this view within a motion chain, if applicable.
    chainDelay: Int = 0, // Delay before animations in a chain commence.
    duration: MotionDuration = MotionDuration.DurationMedium01, // Default duration for animations.
    onEndAction: (() -> Unit)? = null, // Optional callback to execute when animations complete.
    onEnterAction: (() -> Unit)? = null, // Optional callback to execute when animations begin.
    curveEnter: MotionCurve = MotionCurve.EasingAcelerate01,
    // accessibility text
    override var onEnterText: String? = null,
    override var onInText: String? = null,
    override var onExitText: String? = null,
    override var onCancelAction: ((CancellationError) -> Unit)? = null,
) : IMotionView, IAccessibleMotionView, ITelemetryLoggable {

    // Properties inherited from IMotionView, initialized with default values or provided parameters.
    override var motionViewBase: View? = motionViewBase
    override var motionPlayer: MotionPlayer = MotionPlayer()
    override var motionValues: Map<String, MotionValue?> =
        HashMap()
    override var playTogether: Boolean = true
    override var motionKey: String? = null
    override var duration: Long = 0L
    override var motionState: Int =
        MotionState.Exiting.index
    override var onEndAction: (() -> Unit)? = onEndAction
    override var onEnterAction: (() -> Unit)? = onEnterAction
    override var curveEnter: Interpolator? =
        MotionInterpolator.EasingAcelerate01.interpolator
    override var curveExit: Interpolator? =
        MotionInterpolator.EasingAcelerate01.interpolator
    override var chainIndex: Int = 0
    override var chainKey: String? = null
    override var chainDelay: Int = 0
    override var startDurationDelay: Long = 0L

    // Initialization block setting up the instance with provided values.
    init {
        this.motionValues = motionValues
        this.chainIndex = chainIndex
        this.onEndAction = onEndAction
        this.onEnterAction = onEnterAction
        this.duration = duration.speedInMillis
        this.chainKey = chainKey
        this.chainDelay = chainDelay
        motionPlayer = MotionUtil.initMotionPlayer(motionView = this)
    }

    /**
     * Begins the enter animations associated with this view.
     */
    override fun enter() {
        try {
            motionPlayer.enter()
        } catch(e:Exception){
            Log.e("MotionViewBase", e.printStackTrace().toString())
        }
    }

    /**
     * Begins the exit animations associated with this view.
     */
    override fun exit() {
        try {
            motionPlayer.exit()
        } catch(e:Exception){
            Log.e("MotionViewBase", e.printStackTrace().toString())
        }
    }

    /**
     * Sets an action to perform when the animation ends and returns the current view.
     * @param performEndAction The action to be performed at the end.
     * @return The current IMotionView instance.
     */
    override fun setOnEndAction(performEndAction: (() -> Unit)?): IMotionView {
        motionViewBase?.announceForAccessibility(onExitText)
        onEndAction = performEndAction
        return this
    }

    /**
     * Sets an action to perform when the animation enters (begins) and returns the current view.
     * @param performEnterAction The action to be performed at the start.
     * @return The current IMotionView instance.
     */
    override fun setOnEnterAction(performEnterAction: (() -> Unit)?): IMotionView {
        motionViewBase?.announceForAccessibility(onEnterText)
        onEnterAction = performEnterAction
        return this
    }

    /**
     * Provides the view that can be animated.
     * @return The view that can be animated.
     */
    override fun animatableLayout(): View? {
        return motionViewBase
    }

    /**
     * Log telemetry for animation changes
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TelemetryLogger.logTelemetryForAction(event, log)
    }

    /**
     * Cancels the current motion view animation.
     *
     * @param cancellationError An enum of type [CancellationError] that specifies the reason for the
     * cancellation. This allows for differentiated handling of various cancellation scenarios within
     * the motion player.
     */
    fun cancel(cancellationError: CancellationError) {
        motionPlayer.cancel(cancellationError = cancellationError)
    }
}
