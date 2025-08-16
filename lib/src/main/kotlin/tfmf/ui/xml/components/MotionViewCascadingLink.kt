package tfmf.mobile.ui.xml.components

import android.view.View
import android.view.animation.Interpolator
import tfmf.mobile.ui.MotionDuration
import tfmf.mobile.ui.MotionInterpolator
import tfmf.mobile.ui.MotionState
import tfmf.mobile.ui.MotionValue
import tfmf.mobile.ui.Stagger
import tfmf.mobile.ui.accessibility.IAccessibleMotionView
import tfmf.mobile.ui.actions.CancellationError
import tfmf.mobile.ui.actions.ICancellable
import tfmf.mobile.ui.telemetry.ITelemetryLoggable
import tfmf.mobile.ui.telemetry.TelemetryEvent
import tfmf.mobile.ui.util.MotionUtil
import tfmf.mobile.ui.xml.base.IMotionView
import tfmf.mobile.ui.xml.player.MotionPlayer

// Represents a single element in the cascading animation chain for motion views.
class MotionViewCascadingLink @JvmOverloads constructor(
    // Base view that will participate in the motion animation.
    motionViewBase: View,
    // Map of motion properties (like alpha, scale) to be applied during the animation.
    motionValues: Map<String, MotionValue?>,
    // Initial state of the motion view, defaulting to entering state.
    motionState: MotionState = MotionState.Entering,
    // Stagger settings which determine delay between animations in a chain.
    stagger: Stagger = Stagger.Normal,
    // Index within the animation chain, affecting start delay.
    chainIndex: Int = 0,
    // Optional callback to be invoked at the end of the animation.
    onEndAction: (() -> Unit)? = null,
    // accessibility text
    override var onEnterText: String? = null,
    override var onInText: String? = null,
    override var onExitText: String? = null,
    override var onCancelAction: ((CancellationError) -> Unit)? = null
) : IMotionView,
    IAccessibleMotionView,
    ITelemetryLoggable,
    ICancellable {

    // Holds a reference to the base view for motion.
    override var motionViewBase: View? = motionViewBase

    // The MotionPlayer instance responsible for executing the animations.
    override var motionPlayer: MotionPlayer = MotionPlayer()

    // A mutable map to store and manage motion values specific to this link.
    override var motionValues: Map<String, MotionValue?> = HashMap()

    // Flag indicating whether the animations should play together.
    override var playTogether: Boolean = true

    // Identifier for a specific motion within a sequence.
    override var motionKey: String? = null

    // Duration of the animation in milliseconds.
    override var duration: Long = MotionDuration.DurationMedium01.speedInMillis

    // The current state of the motion view.
    override var motionState: Int = MotionState.Exiting.index

    // Callback function to execute when the animation ends.
    override var onEndAction = onEndAction

    // Callback function to execute when the animation starts.
    override var onEnterAction: (() -> Unit)? = null

    // Interpolator for the entering animation.
    override var curveEnter: Interpolator? = MotionInterpolator.EasingEase01.interpolator

    // Interpolator for the exiting animation.
    override var curveExit: Interpolator? = MotionInterpolator.EasingEase01.interpolator

    // Index of the link within the motion chain.
    override var chainIndex: Int = 0

    // Key identifying the chain this link is part of.
    override var chainKey: String? = null

    // Delay before the animation starts, as determined by the chain's stagger.
    override var chainDelay: Int = 0

    // Additional delay before the animation begins, separate from the stagger delay.
    override var startDurationDelay: Long = 0L

    init {
        // Initializes class members with constructor parameters.
        this.motionValues = motionValues
        // Sets up the delay according to the stagger configuration.
        this.chainDelay = stagger.delay.toInt()
        this.chainIndex = chainIndex
        this.motionState = motionState.index
        this.onEndAction = onEndAction
        // Prepares the MotionPlayer using utility functions.
        motionPlayer = MotionUtil.initMotionPlayer(motionView = this)
    }

    /**
     * Begins the enter animations associated with this view.
     */
    override fun enter() {
        motionPlayer.enter()
    }

    /**
     * Begins the exit animations associated with this view.
     */
    override fun exit() {
        motionPlayer.exit()
    }

    /**
     * Sets an action to perform when the animation ends and returns the current view.
     * @param performEndAction The action to be performed at the end.
     * @return The current IMotionView instance.
     */
    override fun setOnEndAction(performEndAction: (() -> Unit)?): IMotionView {
        onEndAction = performEndAction
        return this
    }

    /**
     * Sets an action to perform when the animation enters (begins) and returns the current view.
     * @param performEnterAction The action to be performed at the start.
     * @return The current IMotionView instance.
     */
    override fun setOnEnterAction(performEnterAction: (() -> Unit)?): IMotionView {
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
        TODO("Not yet implemented")
    }
}
