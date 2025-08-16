package tfmf.mobile.ui.compose

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import fluent.compose.demo.fluentmotion.ui.Alpha
import fluent.compose.demo.fluentmotion.ui.Elevation
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.demo.fluentmotion.ui.Scale
import fluent.compose.demo.fluentmotion.ui.TranslationX
import fluent.compose.demo.fluentmotion.ui.TranslationY
import tfmf.mobile.ui.actions.CancellationError
import tfmf.mobile.ui.actions.ICancellable
import tfmf.mobile.ui.telemetry.ITelemetryLoggable
import tfmf.mobile.ui.telemetry.TelemetryEvent
import tfmf.mobile.ui.telemetry.TelemetryLogger
import tfmf.mobile.ui.util.MotionUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * This is the `MotionLinks` class. This class implements `ICancellable` and `ITelemetryLoggable` interfaces.
 *
 * @property ICancellable This property represents the cancellable nature of the class.
 * @property ITelemetryLoggable This property represents the loggable nature of the class for telemetry purposes.
 */
class MotionLinks : ICancellable, ITelemetryLoggable {

    // cancel action to run on animation coroutine cancellation
    override var onCancelAction: ((CancellationError) -> Unit)? = null

    // animation coroutine chain id
    private lateinit var chainIdForCoroutineScope: String

    /**
     * Cancels the animation for a view with a specified cancellation error.
     *
     * @param cancellationError The type of cancellation error. Default is [CancellationError.Default].
     */
    fun cancelAnimation(cancellationError: CancellationError = CancellationError.Default) {
        MotionUtil.cancelAnimationCoroutine(
            chainIdForCoroutineScope = chainIdForCoroutineScope,
            cancellationError,
        )
        onCancelAction?.invoke(cancellationError)
    }

    /**
     * Logs a telemetry event.
     *
     * @param event The telemetry event to log.
     * @param log The log message associated with the event.
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        // needs to be hooked up to the Telemetry module once use cases are defined
    }

    /**
     * Defines the entering Motion Link for a composable.
     * @param motionLinkComposableProps The properties of the motion link.
     */
    @Composable
    fun EnteringMotionLink(
        motionLinkComposableProps: MotionLinkComposableProps
    ) {
        // set the cancel action for the link
        onCancelAction = motionLinkComposableProps.onCancelAction
        chainIdForCoroutineScope = motionLinkComposableProps.chainId
        // Extract the different types of motion from the properties
        // These properties define how the composable should animate when it enters the composition
        val translationXType =
            motionLinkComposableProps.motionTypes[MotionTypeKey.TranslationX.name] as TranslationX?
        val translationYType =
            motionLinkComposableProps.motionTypes[MotionTypeKey.TranslationY.name] as TranslationY?
        val alphaType = motionLinkComposableProps.motionTypes[MotionTypeKey.Alpha.name] as Alpha?
        val scaleType = motionLinkComposableProps.motionTypes[MotionTypeKey.Scale.name] as Scale?
        val resizeType = motionLinkComposableProps.motionTypes[MotionTypeKey.Resize.name] as Resize?

        // Initialize the state for the x and y translation, alpha, and scale
        // These state variables will be updated by the animations and used to apply the animated properties to the composable
        var x by remember { mutableFloatStateOf(translationXType?.xEnter ?: 0f) }
        var y by remember { mutableFloatStateOf(translationYType?.yEnter ?: 0f) }
        var alpha by remember { mutableFloatStateOf(alphaType?.aEnter ?: 1f) }
        var scale by remember { mutableFloatStateOf(scaleType?.sEnter ?: 1f) }
        var width by remember { mutableFloatStateOf(resizeType?.wEnter ?: 1f) }
        var height by remember { mutableFloatStateOf(resizeType?.hEnter ?: 1f) }
        var duration = motionLinkComposableProps.duration.speedInMillis.toInt()
        // only play the animation if it is configured in the
        // gradle file or global motion is enabled
//        if (BuildConfig.DISABLE_ANIMATION_FOR_TESTING || !MotionUtil.animationsEnabled) {
//            duration = 0
//        }
        val coroutineScope = rememberCoroutineScope()
        MotionUtil.appendRunningAnimationCoroutine(motionLinkComposableProps.chainId, coroutineScope)
        val animationSpec =
            TweenSpec<Float>(durationMillis = duration, easing = motionLinkComposableProps.curve.easing)

        // Launch animations when the composable enters the composition
        LaunchedEffect(Unit) {
            // Delay the start of the animations by the chain delay
            try {
                motionLinkComposableProps.onEnterAction?.invoke()
                TelemetryLogger.logTelemetryForAction(TelemetryEvent.Enter, "log")
                delay(motionLinkComposableProps.chainDelay)
                // Animate the x and y translation if a translation type is provided
                translationXType?.let {
                    launch {
                        animate(
                            initialValue = it.xEnter,
                            targetValue = it.xIn,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            x = value
                        }
                    }
                }
                translationYType?.let {
                    launch {
                        animate(
                            initialValue = it.yEnter,
                            targetValue = it.yIn,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            y = value
                        }
                    }
                }
                // Animate the alpha if an alpha type is provided
                alphaType?.let {
                    launch {
                        animate(
                            initialValue = it.aEnter,
                            targetValue = it.aIn,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            alpha = value
                        }
                    }
                }
                // Animate the scale if a scale type is provided
                scaleType?.let {
                    launch {
                        animate(
                            initialValue = it.sEnter,
                            targetValue = it.sIn,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            scale = value
                        }
                    }
                }
                // Animate the resize if a resize set type is provided
                resizeType?.let {
                    launch {
                        animate(
                            initialValue = it.wEnter,
                            targetValue = it.wIn,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            width = value
                        }
                    }
                    launch {
                        animate(
                            initialValue = it.hEnter,
                            targetValue = it.hIn,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            height = value
                        }
                    }
                }
            } finally {
                // Upon animation completion or cancellation, clean up the coroutines
                MotionUtil.removeRunningAnimationCoroutine(chainIdForCoroutineScope = motionLinkComposableProps.chainId)
                // Optionally execute the onExit action after the animations complete
                motionLinkComposableProps.onExitAction?.invoke()
            }
        }

        // Apply the animated properties to the Box composable
        // The Box composable will animate based on the state variables that are updated by the animations
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .alpha(alpha)
                .scale(scale)
                .offset(x.dp, y.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                },
        ) {
            // announcement for accessibility
            motionLinkComposableProps?.onEnterText?.let { Text(text = it) }
            // Call the composable function provided in the properties
            // This is where you can put the content of the composable that you want to animate
            motionLinkComposableProps.composable()
        }
    }

    /**
     * Defines the exiting motion link for a composable.
     * @param motionLinkComposableProps The properties of the motion link.
     */
    @Composable
    fun ExitingMotionLink(motionLinkComposableProps: MotionLinkComposableProps) {
        // set the cancel action for the link
        onCancelAction = motionLinkComposableProps.onCancelAction
        chainIdForCoroutineScope = motionLinkComposableProps.chainId
        // Extract the different types of motion from the properties using the MotionProperty enum
        val translationXType =
            motionLinkComposableProps.motionTypes[MotionTypeKey.TranslationX.name] as TranslationX?
        val translationYType =
            motionLinkComposableProps.motionTypes[MotionTypeKey.TranslationY.name] as TranslationY?
        val alphaType = motionLinkComposableProps.motionTypes[MotionTypeKey.Alpha.name] as Alpha?
        val scaleType = motionLinkComposableProps.motionTypes[MotionTypeKey.Scale.name] as Scale?
        val resizeType = motionLinkComposableProps.motionTypes[MotionTypeKey.Resize.name] as Resize?
        val elevationType = motionLinkComposableProps.motionTypes[MotionTypeKey.Elevation.name] as Elevation?

        // Initialize the state for the x and y translation, alpha, scale, size, and elevation
        var x by remember { mutableFloatStateOf(translationXType?.xIn ?: 0f) }
        var y by remember { mutableFloatStateOf(translationYType?.yIn ?: 0f) }
        var alpha by remember { mutableFloatStateOf(alphaType?.aIn ?: 1f) }
        var scale by remember { mutableFloatStateOf(scaleType?.sIn ?: 1f) }
        var width by remember { mutableFloatStateOf(resizeType?.wIn ?: 1f) }
        var height by remember { mutableFloatStateOf(resizeType?.hIn ?: 1f) }
        var elevation by remember { mutableFloatStateOf(elevationType?.eIn ?: 0f) }

        // Animation duration may be altered for testing purposes
        var duration = motionLinkComposableProps.duration.speedInMillis.toInt()
        // only play the animation if it is configured in the
        // gradle file or global motion is enabled
//        if (BuildConfig.DISABLE_ANIMATION_FOR_TESTING || !MotionUtil.animationsEnabled) {
//            duration = 0
//        }

        // Prepare a coroutine scope for launching animations
        val coroutineScope = rememberCoroutineScope()

        // Keep track of animation coroutines running within this coroutine scope
        MotionUtil.appendRunningAnimationCoroutine(
            chainIdForCoroutineScope = motionLinkComposableProps.chainId,
            coroutineScope,
        )

        // Define the specification for tween animation
        val animationSpec =
            TweenSpec<Float>(durationMillis = duration, easing = motionLinkComposableProps.curve.easing)

        // Launch animations when the composable exits the composition
        LaunchedEffect(Unit) {
            try {
                // Optionally execute the onEnter action before starting the animations
                motionLinkComposableProps.onEnterAction?.invoke()
                TelemetryLogger.logTelemetryForAction(TelemetryEvent.Exit, "log")
                // Delay the start of the animations by the specified chain delay
                delay(motionLinkComposableProps.chainDelay)

                // Animate the translation, alpha, scale, and elevation based on their respective types
                translationXType?.let {
                    launch {
                        animate(
                            initialValue = it.xIn,
                            targetValue = it.xExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            x = value // Update the x translation state
                        }
                    }
                }
                translationYType?.let {
                    launch {
                        animate(
                            initialValue = it.yIn,
                            targetValue = it.yExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            y = value // Update the y translation state
                        }
                    }
                }
                alphaType?.let {
                    launch {
                        animate(
                            initialValue = it.aIn,
                            targetValue = it.aExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            alpha = value // Update the alpha state
                        }
                    }
                }
                scaleType?.let {
                    launch {
                        animate(
                            initialValue = it.sIn,
                            targetValue = it.sExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            scale = value // Update the scale state
                        }
                    }
                }
                elevationType?.let {
                    launch {
                        animate(
                            initialValue = it.eIn,
                            targetValue = it.eExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            elevation = value // Update the elevation state
                        }
                    }
                }
                // Animate resizing if a resize type is provided
                resizeType?.let {
                    launch {
                        animate(
                            initialValue = it.wIn,
                            targetValue = it.wExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            width = value // Update the width state
                        }
                    }
                    launch {
                        animate(
                            initialValue = it.hIn,
                            targetValue = it.hExit,
                            animationSpec = animationSpec,
                        ) { value, _ ->
                            height = value // Update the height state
                        }
                    }
                }
            } finally {
                // Upon animation completion or cancellation, clean up the coroutines
                MotionUtil.removeRunningAnimationCoroutine(chainIdForCoroutineScope = motionLinkComposableProps.chainId)
                // Optionally execute the onExit action after the animations complete
                motionLinkComposableProps.onExitAction?.invoke()
            }
        }

        // Apply the animated properties to the Box composable
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp) // Apply the dynamic size
                .alpha(alpha)                              // Apply the dynamic alpha
                .scale(scale)                             // Apply the dynamic scale
                .offset(x.dp, y.dp)
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                },                      // Apply the dynamic offset
        ) {
            // announcement for accessibility
            motionLinkComposableProps?.onEnterText?.let { Text(text = it) }
            // The content of the Box is determined by the 'composable' lambda passed in through props
            motionLinkComposableProps.composable()
        }
    }


    /**
     * Defines the enter/exit motion link data for a composable.
     * @param motionLinkComposableProps The properties of the motion link.
     */
    // Generate an animated composable triggered onclick
    @Composable
    fun ClickableMotionLink(motionLinkComposableProps: MotionLinkComposableProps) {
        // Extract the different types of motion from the properties
        val translationXType =
            motionLinkComposableProps.motionTypes[MotionTypeKey.TranslationX.name] as TranslationX?
        val translationYType =
            motionLinkComposableProps.motionTypes[MotionTypeKey.TranslationY.name] as TranslationY?
        val alphaType = motionLinkComposableProps.motionTypes[MotionTypeKey.Alpha.name] as Alpha?
        val scaleType = motionLinkComposableProps.motionTypes[MotionTypeKey.Scale.name] as Scale?
        val resizeType = motionLinkComposableProps.motionTypes[MotionTypeKey.Resize.name] as Resize?
        val elevationType = motionLinkComposableProps.motionTypes[MotionTypeKey.Elevation.name] as Elevation?

        // Initialize the state for the x and y translation, alpha, scale, and color
        var x by remember { mutableFloatStateOf(translationXType?.xEnter ?: 0f) }
        var y by remember { mutableFloatStateOf(translationYType?.yEnter ?: 0f) }
        var alpha by remember { mutableFloatStateOf(alphaType?.aEnter ?: 1f) }
        var scale by remember { mutableFloatStateOf(scaleType?.sEnter ?: 1f) }
        val color by remember { mutableStateOf(MotionUtil.generateRandomColor()) }
        var width by remember { mutableFloatStateOf(resizeType?.wEnter ?: 1f) }
        var height by remember { mutableFloatStateOf(resizeType?.hEnter ?: 1f) }
        var elevation by remember { mutableFloatStateOf(elevationType?.eEnter ?: 0f) }
        var duration = motionLinkComposableProps.duration.speedInMillis.toInt()
        // gradle file or global motion is enabled
//        if (BuildConfig.DISABLE_ANIMATION_FOR_TESTING || !MotionUtil.animationsEnabled) {
//            duration = 0
//        }

        // Prepare a coroutine scope for launching animations
        val coroutineScope = rememberCoroutineScope()

        // Keep track of animation coroutines running within this coroutine scope
        MotionUtil.appendRunningAnimationCoroutine(
            chainIdForCoroutineScope = motionLinkComposableProps.chainId,
            coroutineScope,
        )

        // Define the specification for tween animation
        val animationSpec =
            TweenSpec<Float>(durationMillis = duration, easing = motionLinkComposableProps.curve.easing)
        var enter by remember { mutableStateOf(true) }
        // Create a Box composable with the animated properties and a clickable modifier
        Box(
            modifier = Modifier
                .size(width = width.dp, height = height.dp)
                .alpha(alpha)
                .scale(scale)
                .offset(x.dp, y.dp)
                .background(color)
                .clickable {
                    // Launch animations when the Box is clicked
                    try {
                        if (enter) {
                            // Animate the x and y translation if a translation type is provided
                            translationXType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.xEnter,
                                        targetValue = it.xIn,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        x = value
                                    }
                                }
                            }
                            translationYType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.yEnter,
                                        targetValue = it.yIn,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        y = value
                                    }
                                }
                            }
                            // Animate the alpha if an alpha type is provided
                            alphaType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.aEnter,
                                        targetValue = it.aIn,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        alpha = value
                                    }
                                }
                            }
                            // Animate the scale if a scale type is provided
                            scaleType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.sEnter,
                                        targetValue = it.sIn,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        scale = value
                                    }
                                }
                            }
                            elevationType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.eIn,
                                        targetValue = it.eExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        elevation = value
                                    }
                                }
                            }
                            resizeType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.wEnter,
                                        targetValue = it.wIn,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        width = value
                                    }
                                }
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.hEnter,
                                        targetValue = it.hIn,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        height = value
                                    }
                                }
                            }
                        } else {
                            translationXType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.xIn,
                                        targetValue = it.xExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        x = value
                                    }
                                }
                            }
                            translationYType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.yIn,
                                        targetValue = it.yExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        y = value
                                    }
                                }
                            }
                            // Animate the alpha if an alpha type is provided
                            alphaType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.aIn,
                                        targetValue = it.aExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        alpha = value
                                    }
                                }
                            }
                            // Animate the scale if a scale type is provided
                            scaleType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.sIn,
                                        targetValue = it.sExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        scale = value
                                    }
                                }
                            }
                            elevationType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.eIn,
                                        targetValue = it.eExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        elevation = value
                                    }
                                }
                            }
                            resizeType?.let {
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.wIn,
                                        targetValue = it.wExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        width = value
                                    }
                                }
                                coroutineScope.launch {
                                    animate(
                                        initialValue = it.hIn,
                                        targetValue = it.hExit,
                                        animationSpec = animationSpec,
                                    ) { value, _ ->
                                        height = value
                                    }

                                }
                            }
                        }
                    } finally {
                        // Upon animation completion or cancellation, clean up the coroutines
                        MotionUtil.removeRunningAnimationCoroutine(chainIdForCoroutineScope = motionLinkComposableProps.chainId)
                        // Optionally execute the onExit action after the animations complete
                        motionLinkComposableProps.onExitAction?.invoke()
                    }
                    enter = !enter
                },
        ) {
            // announcement for accessibility
            motionLinkComposableProps?.onEnterText?.let { Text(text = it) }
            // The content of the Box is determined by the 'composable' lambda passed in through props
            motionLinkComposableProps.composable()
        }
    }
}