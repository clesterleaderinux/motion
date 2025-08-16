package com.microsoft.fluentmotion.ui.xml.player

import android.animation.Animator
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.Interpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import com.microsoft.fluentmotion.BuildConfig
import com.microsoft.fluentmotion.ui.Alpha
import com.microsoft.fluentmotion.ui.CardViewElevation
import com.microsoft.fluentmotion.ui.CornerRadius
import com.microsoft.fluentmotion.ui.IndicatorOffset
import com.microsoft.fluentmotion.ui.IndicatorWidth
import com.microsoft.fluentmotion.ui.MotionChain
import com.microsoft.fluentmotion.ui.MotionDuration
import com.microsoft.fluentmotion.ui.MotionInterpolator
import com.microsoft.fluentmotion.ui.MotionTypeKey
import com.microsoft.fluentmotion.ui.MotionValue
import com.microsoft.fluentmotion.ui.Resize
import com.microsoft.fluentmotion.ui.Rotation
import com.microsoft.fluentmotion.ui.Scale
import com.microsoft.fluentmotion.ui.ScrollX
import com.microsoft.fluentmotion.ui.TranslationX
import com.microsoft.fluentmotion.ui.TranslationXTarget
import com.microsoft.fluentmotion.ui.TranslationY
import com.microsoft.fluentmotion.ui.TranslationYTarget
import com.microsoft.fluentmotion.ui.actions.CancellationError
import com.microsoft.fluentmotion.ui.telemetry.ITelemetryLoggable
import com.microsoft.fluentmotion.ui.telemetry.TelemetryEvent
import com.microsoft.fluentmotion.ui.util.MotionUtil
import com.microsoft.fluentmotion.ui.xml.base.IMotionView


/**
 * Generate a motion player for a MotionViewGroup
 */
// Base class for motion players, responsible for managing animation sets and motion values.
open class MotionPlayer : ITelemetryLoggable {

    // Collection of animations to be played when the motion enters.
    private val onEnterAnimationSet: MutableList<Animator?> = arrayListOf()

    // Collection of animations to be played when the motion exits.
    private val onExitAnimationSet: MutableList<Animator?> = arrayListOf()

    // AnimatorSet to coordinate the playing of animations.
    private var animatorSet = AnimatorSet()

    // Flag indicating if the enter animation is currently running.
    private var enterAnimationIsRunning = false

    // Flag indicating if the exit animation is currently running.
    private var exitAnimationIsRunning = false

    /**
     * Delay before playing the next animation in the chain.
     */
    var nextIndexDelay = 0

    // Map holding the motion values for various properties.
    var motionValues: Map<String, MotionValue?> = HashMap()

    // The motion view interface implementation reference.
    lateinit var motionView: IMotionView

    // Optional base view for cases where the motion needs a view reference.
    var motionViewBase: View? = null

    // Duration for the animation sequences.
    var duration: Long = MotionDuration.DurationMedium01.speedInMillis.toLong()

    // Interpolator for enter animations.
    var curveOnEnter: Interpolator? = MotionInterpolator.EasingEase01.interpolator

    // Interpolator for exit animations.
    var curveOnExit: Interpolator? = MotionInterpolator.EasingEase01.interpolator

    // Companion object serving as a static container for chaining animations.
    companion object MotionRegister {
        // Currently running chain of animations.
        private var runningChain: MotionChain? = null

        // Register of all chains by their key identifiers.
        private val chainRegister: MutableMap<String, MotionChain> = mutableMapOf()

        // Index pointer to keep track of the current position within a chain.
        private var currentChainIndex = 0

        /**
         * Adds a new motion chain to the register unless it already exists.
         * @param chainKey The key identifier for the motion chain.
         * @param motionChain The motion chain to be added.
         */
        fun addMotionChain(
            chainKey: String,
            motionChain: MotionChain
        ) {
            if (!chainRegister.containsKey(chainKey)) {
                chainRegister[chainKey] = motionChain
            }
        }

        /**
         * Associates a motion player with a specific chain.
         * @param chainKey The key identifier for the motion chain.
         * @param motionPlayer The motion player to be associated with the chain.
         */
        fun addMotionPlayerForChain(
            chainKey: String,
            motionPlayer: MotionPlayer
        ) {
            chainRegister[chainKey]?.chainLinks?.add(motionPlayer)
        }

        /**
         * Removes a motion chain from the register based on its key.
         * @param chainKey The key identifier for the motion chain.
         */
        fun clearChainForKey(chainKey: String) {
            chainRegister.remove(chainKey)
        }

        /**
         * Initiates the 'enter' sequence for a motion chain using its key.
         * @param chainKey The key identifier for the motion chain.
         * @param clearOnFinish Flag indicating if the chain should be cleared after the animation sequence finishes.
         * If false, the chain will remain in the register for future use and caller is responsible for clearing it,
         * otherwise it will cause memory leak as the chain holds reference to the view.
         */
        fun playEnterChainForKey(chainKey: String, clearOnFinish: Boolean = false) {
            runningChain = chainRegister[chainKey]
            onEnterWithDelay(0, clearOnFinish)
        }

        /**
         * Initiates the 'exit' sequence for a motion chain using its key.
         * @param chainKey The key identifier for the motion chain.
         * @param clearOnFinish Flag indicating if the chain should be cleared after the animation sequence finishes.
         * If false, the chain will remain in the register for future use and caller is responsible for clearing it,
         * otherwise it will cause memory leak as the chain holds reference to the view.
         */
        fun playExitChainForKey(chainKey: String, clearOnFinish: Boolean = false) {
            runningChain = chainRegister[chainKey]
            onExitWithDelay(0, clearOnFinish)
        }

        /**
         * Starts the 'enter' animation sequence with the specified delay for each link in the chain.
         * @param index The index of the chain link to start the animation for.
         * @param clearOnFinish Flag indicating if the chain should be cleared after the animation sequence finishes.
         * If false, the chain will remain in the register for future use and caller is responsible for clearing it,
         * otherwise it will cause memory leak as the chain holds reference to the view.
         */
        private fun onEnterWithDelay(index: Int, clearOnFinish: Boolean) {
            if ((runningChain?.chainLinks?.size?.minus(1) ?: -1) >= index) {
                val link = runningChain?.chainLinks?.elementAt(index)
                link?.let {
                    it.enter()
                    it.motionView.animatableLayout()?.postDelayed(
                        {
                            currentChainIndex += 1
                            onEnterWithDelay(currentChainIndex, clearOnFinish)
                        },
                        it.nextIndexDelay.toLong(),
                    )
                }
            } else {
                currentChainIndex = 0
                if (clearOnFinish) {
                    runningChain?.chainKey?.let { clearChainForKey(it) }
                }
                runningChain = null
            }
        }

        /**
         * Starts the 'exit' animation sequence with the specified delay for each link in the chain.
         * @param index The index of the chain link to start the animation for.
         * @param clearOnFinish Flag indicating if the chain should be cleared after the animation sequence finishes.
         * If false, the chain will remain in the register for future use and caller is responsible for clearing it,
         * otherwise it will cause memory leak as the chain holds reference to the view.
         */
        private fun onExitWithDelay(index: Int, clearOnFinish: Boolean) {
            if ((runningChain?.chainLinks?.size?.minus(1) ?: -1) >= index) {
                val link = runningChain?.chainLinks?.elementAt(index)
                link?.let {
                    it.exit()
                    it.motionView.animatableLayout()?.postDelayed(
                        {
                            currentChainIndex += 1
                            onExitWithDelay(currentChainIndex, clearOnFinish)
                        },
                        it.nextIndexDelay.toLong(),
                    )
                }
            } else {
                currentChainIndex = 0
                if (clearOnFinish) {
                    runningChain?.chainKey?.let { clearChainForKey(it) }
                }
                runningChain = null
            }
        }
    }

    // Triggers the 'enter' animations for this motion player instance.
    fun enter(jumpToEnd: Boolean? = false) {
        animatorSet = AnimatorSet()
        // only play the animation if it is configured in the
        // gradle file or global motion is enabled
        if (BuildConfig.DISABLE_ANIMATION_FOR_TESTING || !MotionUtil.animationsEnabled) {
            animatorSet.duration = 0
        } else {
            animatorSet.duration = duration
        }
        buildOnEnterAnimationSet()
        val localAnimatorSet = animatorSet
        animatorSet.apply {
            playTogether(onEnterAnimationSet)
            doOnStart {
                enterAnimationIsRunning = true
                motionView.onEnterAction?.invoke()
            }
            doOnEnd {
                enterAnimationIsRunning = false
                motionView.onEndAction?.invoke()

                // Since the animation has ended, remove it from the running set, otherwise it will cause
                // memory leak since the animatorSet holds reference to the motionView
                MotionUtil.removeRunningAnimatorSet(localAnimatorSet)
            }
        }
        MotionUtil.appendRunningAnimatorSet(animatorSet)
        if (jumpToEnd == true) {
            animatorSet.start()
            animatorSet.end()
        } else {
            animatorSet.start()
        }
    }

    // Triggers the 'exit' animations for this motion player instance.
    fun exit() {
        animatorSet = AnimatorSet()
        // only play the animation if it is configured in the
        // gradle file or global motion is enabled
        if (BuildConfig.DISABLE_ANIMATION_FOR_TESTING || !MotionUtil.animationsEnabled) {
            animatorSet.duration = 0
        } else {
            animatorSet.duration = duration
        }
        buildOnExitAnimationSet()
        val localAnimatorSet = animatorSet
        animatorSet.apply {
            playTogether(onExitAnimationSet)
            doOnStart {
                exitAnimationIsRunning = true
                motionView.onEnterAction?.invoke()
            }
            doOnEnd {
                exitAnimationIsRunning = false
                motionView.onEndAction?.invoke()

                // Since the animation has ended, remove it from the running set, otherwise it will cause
                // memory leak since the animatorSet holds reference to the motionView
                MotionUtil.removeRunningAnimatorSet(localAnimatorSet)
            }
        }
        MotionUtil.appendRunningAnimatorSet(animatorSet)
        animatorSet.start()
    }

    /**
     * Cancels a motion chain based on its key and optionally executes an action at the end.
     *
     * This function iterates through all the links associated with the given chain key and cancels their respective animator sets.
     * If a `chainEndAction` is provided, it will be invoked after all animations have been cancelled.
     *
     * @param chainKey The key identifier for the motion chain.
     * @param cancellationError Log the reason for the cancellation.
     */
    fun cancelChain(chainKey: String?, cancellationError: CancellationError = CancellationError.Default) {
        chainRegister[chainKey]?.chainLinks?.forEach { link ->
            link.animatorSet.cancel()
        }
        chainRegister[chainKey]?.onCancelAction?.invoke(cancellationError)
        val logMessage =
            "Canceled animations for chain '$chainKey' with cancellation type '${cancellationError?.name}'."
        logTelemetryForAction(TelemetryEvent.Cancellation, logMessage)
    }

    /**
     * Cancels animations for a view with a specified cancellation error.
     *
     * @param cancellationError The type of cancellation error. Default is [CancellationError.Default].
     */
    fun cancel(cancellationError: CancellationError? = CancellationError.Default) {
        animatorSet.cancel()
        val logMessage =
            "Canceled animations for view with cancellation type '${cancellationError?.name}'."
        logTelemetryForAction(TelemetryEvent.Cancellation, logMessage)
    }

    /**
     * Logs a telemetry event.
     *
     * @param event The telemetry event to log.
     * @param log The log message associated with the event.
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TODO("Not yet implemented")
    }

    // Builds the set of 'onEnter' animations using properties defined in motionValues.
    private fun buildOnEnterAnimationSet() {
        // Check for and create an alpha animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Alpha)) {
            val alpha = motionValues[MotionTypeKey.Alpha.name] as Alpha
            // Appends an ObjectAnimator for the Alpha property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Alpha,
                start = alpha.aEnter,
                end = alpha.aIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        // Check for and create scale animations if they exist in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Scale)) {
            val scale = motionValues[MotionTypeKey.Scale.name] as Scale
            // Appends an ObjectAnimator for the ScaleX property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.ScaleX,
                start = scale.sEnter,
                end = scale.sIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
            // Appends an ObjectAnimator for the ScaleY property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.ScaleY,
                start = scale.sEnter,
                end = scale.sIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        // Check for and create translation X animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationX)) {
            val translationX = motionValues[MotionTypeKey.TranslationX.name] as TranslationX
            // Appends an ObjectAnimator for the TranslationX property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationX,
                start = translationX.xEnter,
                end = translationX.xIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        // Check for and create translation X animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationX)) {
            val translationX = motionValues[MotionTypeKey.TranslationX.name] as TranslationX
            // Appends an ObjectAnimator for the TranslationX property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationX,
                start = translationX.xEnter,
                end = translationX.xIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        // Direct animations move a view directly to a target position without the need for the current view location
        // Applies to TranslationXTarget and TranslationYTarget
        // Check for and create translation X direction animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationXTarget)) {
            val translationXTarget = motionValues[MotionTypeKey.TranslationXTarget.name] as TranslationXTarget
            // Appends an ObjectAnimator for the TranslationXDirect property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationXTarget,
                targetPosition = translationXTarget.targetX,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        // Check for and create translation Y direction animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationYTarget)) {
            val translationYTarget = motionValues[MotionTypeKey.TranslationYTarget.name] as TranslationYTarget
            // Appends an ObjectAnimator for the TranslationYDirect property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationYTarget,
                targetPosition = translationYTarget.targetY,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        // Check for and create resize animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Resize)) {
            val resize = motionValues[MotionTypeKey.Resize.name] as Resize
            // Append a ValueAnimator for width resize property to the enter animations
            MotionUtil.appendValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Resize,
                start = resize.wEnter.toInt(),
                end = resize.wIn.toInt(),
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
                isHeight = false,
            )
            // Append a ValueAnimator for height resize property to the enter animations
            MotionUtil.appendValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Resize,
                start = resize.hEnter.toInt(),
                end = resize.hIn.toInt(),
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
                isHeight = true,
            )
        }
        // Check for and create scroll X animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.ScrollX)) {
            val scrollX = motionValues[MotionTypeKey.ScrollX.name] as ScrollX
            // Append a custom animator for horizontal scrolling to the enter animations
            MotionUtil.appendScrollObjectAnimator(
                end = scrollX.sIn,
                animationCollection = onEnterAnimationSet,
                motionView = motionView.animatableLayout(),
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.IndicatorOffset)) {
            val offset = motionValues[MotionTypeKey.IndicatorOffset.name] as IndicatorOffset
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.IndicatorOffset,
                start = offset.ioEnter,
                end = offset.ioIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.IndicatorWidth)) {
            val width = motionValues[MotionTypeKey.IndicatorWidth.name] as IndicatorWidth
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.IndicatorWidth,
                start = width.iwEnter,
                end = width.iwIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Rotation)) {
            val degreeRotation = motionValues[MotionTypeKey.Rotation.name] as Rotation
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Rotation,
                start = degreeRotation.dEnter,
                end = degreeRotation.dIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.CornerRadius)) {
            val radius = motionValues[MotionTypeKey.CornerRadius.name] as CornerRadius
            MotionUtil.appendCornerRadiusValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.CornerRadius,
                start = radius.crEnter,
                end = radius.crIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.CardViewElevation)) {
            val cardElevation = motionValues[MotionTypeKey.CardViewElevation.name] as CardViewElevation
            MotionUtil.appendCardViewElevationValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.CardViewElevation,
                start = cardElevation.cvElevationEnter,
                end = cardElevation.cvElevationIn,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
    }

    // Builds the set of 'onExit' animations using properties defined in motionValues.
    private fun buildOnExitAnimationSet() {
        // Check for and create an alpha animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Alpha)) {
            val alpha = motionValues[MotionTypeKey.Alpha.name] as Alpha
            // Appends an ObjectAnimator for the Alpha property to the exit animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Alpha,
                start = alpha.aIn,
                end = alpha.aExit,
                aInterpolator = curveOnExit, // Ensures the proper exit interpolator is used
                animationCollection = onExitAnimationSet,
            )
        }
        // Check for and create scale animations if they exist in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.ScaleX)
            || MotionUtil.containsValueType(motionValues, MotionTypeKey.ScaleY)) {
            val scaleX = motionValues[MotionTypeKey.ScaleX.name] as Scale
            val scaleY = motionValues[MotionTypeKey.ScaleY.name] as Scale
            // Appends an ObjectAnimator for the ScaleX property to the exit animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.ScaleX,
                start = scaleX.sIn,
                end = scaleX.sExit,
                aInterpolator = curveOnExit, // Ensures the proper exit interpolator is used
                animationCollection = onExitAnimationSet,
            )
            // Appends an ObjectAnimator for the ScaleY property to the exit animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.ScaleY,
                start = scaleY.sIn,
                end = scaleY.sExit,
                aInterpolator = curveOnExit, // Ensures the proper exit interpolator is used
                animationCollection = onExitAnimationSet,
            )
        }
        // Check for and create translation X animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationX)) {
            val translationX = motionValues[MotionTypeKey.TranslationX.name] as TranslationX
            // Appends an ObjectAnimator for the TranslationX property to the exit animations
            // Identifies an issue with interpolator usage that should be fixed to use curveOnExit
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationX,
                start = translationX.xIn,
                end = translationX.xExit,
                aInterpolator = curveOnExit, // Corrected interpolator for exiting transition
                animationCollection = onExitAnimationSet,
            )
        }
        // Check for and create translation Y animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationY)) {
            val translationY = motionValues[MotionTypeKey.TranslationY.name] as TranslationY
            // Appends an ObjectAnimator for the TranslationY property to the exit animations
            // Identifies an issue with interpolator usage that should be fixed to use curveOnExit
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationY,
                start = translationY.yIn,
                end = translationY.yExit,
                aInterpolator = curveOnExit, // Corrected interpolator for exiting transition
                animationCollection = onExitAnimationSet,
            )
        }
        // Direct animations move a view directly to a target position without the need for the current view location
        // Applies to TranslationXTarget and TranslationYTarget
        // Check for and create translation X direction animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationXTarget)) {
            val translationXTarget = motionValues[MotionTypeKey.TranslationXTarget.name] as TranslationXTarget
            // Appends an ObjectAnimator for the TranslationXDirect property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationXTarget,
                targetPosition = translationXTarget.targetX,
                aInterpolator = curveOnEnter,
                animationCollection = onExitAnimationSet,
            )
        }
        // Check for and create translation Y direction animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.TranslationYTarget)) {
            val translationYTarget = motionValues[MotionTypeKey.TranslationYTarget.name] as TranslationYTarget
            // Appends an ObjectAnimator for the TranslationYDirect property to the enter animations
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.TranslationYTarget,
                targetPosition = translationYTarget.targetY,
                aInterpolator = curveOnEnter,
                animationCollection = onExitAnimationSet,
            )
        }
        // Check for and create resize animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Resize)) {
            val resize = motionValues[MotionTypeKey.Resize.name] as Resize
            // Append a ValueAnimator for width resize property to the exit animations
            // Identifies an issue with interpolator usage that should be fixed to use curveOnExit
            MotionUtil.appendValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Resize,
                start = resize.wIn.toInt(),
                end = resize.wExit.toInt(),
                aInterpolator = curveOnExit, // Corrected interpolator for exiting transition
                animationCollection = onExitAnimationSet,
                isHeight = false,
            )
            // Append a ValueAnimator for height resize property to the exit animations
            // Identifies an issue with interpolator usage that should be fixed to use curveOnExit
            MotionUtil.appendValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Resize,
                start = resize.hIn.toInt(),
                end = resize.hExit.toInt(),
                aInterpolator = curveOnExit, // Corrected interpolator for exiting transition
                animationCollection = onExitAnimationSet,
                isHeight = true,
            )
        }
        // Check for and create scroll X animation if it exists in motionValues
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.ScrollX)) {
            val scrollX = motionValues[MotionTypeKey.ScrollX.name] as ScrollX
            // Append a custom animator for horizontal scrolling to the exit animations
            // Identifies an issue with incorrect animation collection that should be onExitAnimationSet
            MotionUtil.appendScrollObjectAnimator(
                end = scrollX.sExit,
                animationCollection = onExitAnimationSet, // Corrects the animation collection for exiting transition
                motionView = motionView.animatableLayout(),
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.IndicatorOffset)) {
            val offset = motionValues[MotionTypeKey.IndicatorOffset.name] as IndicatorOffset
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.IndicatorOffset,
                start = offset.ioIn,
                end = offset.ioExit,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.IndicatorWidth)) {
            val width = motionValues[MotionTypeKey.IndicatorWidth.name] as IndicatorWidth
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.IndicatorWidth,
                start = width.iwIn,
                end = width.iwExit,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.Rotation)) {
            val degreeRotation = motionValues[MotionTypeKey.Rotation.name] as Rotation
            MotionUtil.appendObjectAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.Rotation,
                start = degreeRotation.dIn,
                end = degreeRotation.dExit,
                aInterpolator = curveOnEnter,
                animationCollection = onEnterAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.CornerRadius)) {
            val radius = motionValues[MotionTypeKey.CornerRadius.name] as CornerRadius
            MotionUtil.appendCornerRadiusValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.CornerRadius,
                start = radius.crIn,
                end = radius.crExit,
                aInterpolator = curveOnEnter,
                animationCollection = onExitAnimationSet,
            )
        }
        if (MotionUtil.containsValueType(motionValues, MotionTypeKey.CardViewElevation)) {
            val cardElevation = motionValues[MotionTypeKey.CardViewElevation.name] as CardViewElevation
            MotionUtil.appendCardViewElevationValueAnimator(
                motionView = motionView.animatableLayout(),
                property = MotionTypeKey.CardViewElevation,
                start = cardElevation.cvElevationIn,
                end = cardElevation.cvElevationExit,
                aInterpolator = curveOnEnter,
                animationCollection = onExitAnimationSet,
            )
        }
    }
}
