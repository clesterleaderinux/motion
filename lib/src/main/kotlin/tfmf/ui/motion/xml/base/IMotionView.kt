package tfmf.ui.motion.xml.base

import android.view.View
import android.view.animation.Interpolator
import tfmf.ui.motion.MotionValue
import tfmf.ui.motion.actions.CancellationError
import tfmf.ui.motion.xml.player.MotionPlayer

// to be used with any view that will be needed and wrapped with a MotionPlayer

interface IMotionView {
    // view to apply the animation to
    var motionViewBase: View?

    // player to be used for enter/exit
    var motionPlayer: MotionPlayer

    // motion values for the animation
    var motionValues: Map<String, MotionValue?>

    // play syncronized or sequentially
    var playTogether: Boolean

    // motion identifier
    var motionKey: String?

    // animation duration
    var duration: Long

    // enter or exit
    var motionState: Int

    // animation end action
    var onEndAction: (() -> Unit)?

    // animation enter action
    var onEnterAction: (() -> Unit)?

    // cancel animation
    var onCancelAction: ((CancellationError) -> Unit)?

    // motion curve to apply on enter
    var curveEnter: Interpolator?

    // motion curve to apply on ext
    var curveExit: Interpolator?

    /**
     * Used to order a series of animations with the same chainKey and play in the order of the index.
     */
    var chainIndex: Int

    /**
     * Used to group a collection of animations to play together.
     */
    var chainKey: String?

    /**
     * The time delay between the last chain item played
     */
    var chainDelay: Int

    var startDurationDelay: Long

    /**
     * Triggers the MotionPlayer.enter animation.
     */
    fun enter()

    /**
     * Triggers the MotionPlayer.exit animation.
     */
    fun exit()

    /**
     * Creates an end action to run when all motion for a player is complete.
     * @param performEndAction The action to be performed at the end.
     * @return The IMotionView instance.
     */
    fun setOnEndAction(performEndAction: (() -> Unit)?): IMotionView

    /**
     * Creates a start action to run when the enter function is triggered.
     * @param performEnterAction The action to be performed at the start.
     * @return The IMotionView instance.
     */
    fun setOnEnterAction(performEnterAction: (() -> Unit)?): IMotionView

    /**
     * Returns the view used as the motion view base.
     * @return The view used as the motion view base.
     */
    fun animatableLayout(): View?

}