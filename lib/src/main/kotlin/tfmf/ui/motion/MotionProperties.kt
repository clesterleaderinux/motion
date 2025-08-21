package tfmf.ui.motion

import android.view.View
import android.view.animation.Interpolator
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.core.view.animation.PathInterpolatorCompat
import tfmf.ui.motion.actions.CancellationError
import tfmf.ui.motion.compose.MotionLinks
import tfmf.ui.motion.xml.player.MotionPlayer

/**
 * Complete motion chain for UIViews using a MotionPlayer
 * @param chainKey The key for the motion chain.
 * @param chainLinks The list of MotionPlayers in the chain.
 * @param onEndAction The action to perform when the animation ends.
 * @param onEnterAction The action to perform when the animation begins.
 */
data class MotionChain(
    val chainKey: String,
    val chainLinks: MutableList<MotionPlayer>,
    val onEndAction: (() -> Unit)? = null,
    val onEnterAction: (() -> Unit)? = null,
    var onCancelAction: ((CancellationError) -> Unit)? = null
)

/**
 * Link object to be used with Jetpack Compose based components
 * @param duration The duration of the motion.
 * @param chainIndex The index of the chain.
 * @param chainDelay The delay before the chain starts.
 * @param curve The curve of the motion.
 * @param chainId The ID of the chain.
 * @param linkId The ID of the link.
 * @param motionTypes The types of motions.
 * @param zIndex The z-index of the motion.
 * @param composable The composable to be animated.
 * @param onEnterAction The action to perform when the animation begins.
 * @param onExitAction The action to perform when the animation ends.
 * @param onCancelAction The action to perform when the animation is cancelled.
 */
@Immutable
data class MotionLinkComposableProps(
    val duration: MotionDuration,
    val chainIndex: Int? = 0,
    val chainDelay: Long = 0,
    val curve: MotionCurve,
    val chainId: String,
    val linkId: String,
    val motionTypes: HashMap<String?, MotionValue?>,
    val zIndex: Int = 0,
    val onEnterAction: (() -> Unit)?,
    val onExitAction: (() -> Unit)?,
    var onEnterText: String? = null,
    var onInText: String? = null,
    var onExitText: String? = null,
    var onCancelAction: ((CancellationError) -> Unit)? = null,
    val composable: @Composable (() -> Unit)
) {
    @Composable
    fun RenderExit() {
        MotionLinks().ExitingMotionLink(motionLinkComposableProps = this)
    }

    @Composable
    fun RenderEnter() {
        MotionLinks().EnteringMotionLink(motionLinkComposableProps = this)
    }

    @Composable
    fun RenderClickableLink() {
        MotionLinks().ClickableMotionLink(motionLinkComposableProps = this)
    }
}

/**
 * Link object to be used with View based components
 * @param duration The duration of the motion.
 * @param chainDelay The delay before the chain starts.
 * @param curve The curve of the motion.
 * @param chainId The ID of the chain.
 * @param linkId The ID of the link.
 * @param motionTypes The types of motions.
 * @param zIndex The z-index of the motion.
 * @param animateable The view to be animated.
 * @param onCancelAction Action to run to cancel the animation.
 */
@Immutable
data class MotionLinkViewProps(
    val duration: MotionDuration,
    val chainDelay: Long = 0,
    val curve: MotionCurve,
    val chainId: Int,
    val linkId: String,
    val motionTypes: HashMap<String?, MotionValue?>,
    val zIndex: Int = 0,
    val animateable: View,
    val onCancelAction: ((CancellationError) -> Unit)? = null
)

/**
 * Used for demo purposes and may exist in the final product
 * @param drawable The drawable resource ID.
 * @param size The size of the item.
 * @param drawableInstance The instance of the drawable.
 * @param contentDescription The content description of the item.
 */
@Immutable
data class GalleryItem(
    val drawable: Int,
    val size: Dimensions,
    val drawableInstance: Dimensions?,
    val contentDescription: String?
)

/**
 * Delay value to use with the Cascading Layout
 * @param delay The delay value for the cascade.
 */
enum class Stagger(val delay: Long) {
    Zero(MotionDuration.ZeroDuration.speedInMillis),
    Loose(MotionDuration.DurationLong01.speedInMillis),
    Loosest(MotionDuration.DurationLong02.speedInMillis),
    Medium(MotionDuration.DurationMedium01.speedInMillis),
    Normal(MotionDuration.DurationMedium02.speedInMillis),
    Tight(MotionDuration.DurationShort03.speedInMillis),
    Tightest(MotionDuration.DurationShort02.speedInMillis),
    Test(3000L)
}

/**
 * Cancellation type for canceling animatorSets/coroutines
 */
enum class CancellationType {
    MemoryIssue,
    CancelForTest,
    ScreenTransition
}

/**
 * Dimensions for height/width resize animations
 * @param height The height dimension.
 * @param width The width dimension.
 */
data class Dimensions(val height: Int, val width: Int)

// Extendable class to use for array/func that are type specific without needing to pass individual types
// Ex: val motionTypes: [String: MotionValue?] can contain any of these types below
/**
 * Base class for motion values.
 */
sealed interface MotionValue

/**
 * Alpha animation "enter, exit and in" values
 * @param aEnter The alpha value at the start of the animation.
 * @param aIn The alpha value during the animation.
 * @param aExit The alpha value at the end of the animation.
 */
data class Alpha(val aEnter: Float, val aIn: Float, val aExit: Float) : MotionValue

/**
 * Scale animation "enter, exit and in" values
 * @param sEnter The scale value at the start of the animation.
 * @param sIn The scale value during the animation.
 * @param sExit The scale value at the end of the animation.
 */
data class Scale(val sEnter: Float, val sIn: Float, val sExit: Float) : MotionValue

/**
 * Translation x animation "enter, exit and in" values
 * @param xEnter The x translation value at the start of the animation.
 * @param xIn The x translation value during the animation.
 * @param xExit The x translation value at the end of the animation.
 */
data class TranslationX(
    val xEnter: Float, val xIn: Float, val xExit: Float
) : MotionValue

/**
 * Translation y animation "enter, exit and in" values
 * @param yEnter The y translation value at the start of the animation.
 * @param yIn The y translation value during the animation.
 * @param yExit The y translation value at the end of the animation.
 */
data class TranslationY(
    val yEnter: Float, val yIn: Float, val yExit: Float
) : MotionValue

/**
 * Translation targetX animation value
 * @param targetX The target X to move the view to
 */
data class TranslationXTarget(
    val targetX: Float
) : MotionValue

/**
 * Translation targetY animation value
 * @param targetY The target Y to move the view to
 */
data class TranslationYTarget(
    val targetY: Float
) : MotionValue

/**
 * Resize height/animation animation "enter, exit and in" values
 * @param wEnter The width at the start of the animation.
 * @param wIn The width during the animation.
 * @param wExit The width at the end of the animation.
 * @param hEnter The height at the start of the animation.
 * @param hIn The height during the animation.
 * @param hExit The height at the end of the animation.
 */
data class Resize(
    val wEnter: Float,
    val wIn: Float,
    val wExit: Float,
    val hEnter: Float,
    val hIn: Float,
    val hExit: Float
) : MotionValue

/**
 * Elevation/Shadow animation "enter, exit and in" values
 * @param eEnter The elevation at the start of the animation.
 * @param eIn The elevation during the animation.
 * @param eExit The elevation at the end of the animation.
 */
data class Elevation(val eEnter: Float, val eIn: Float, val eExit: Float) : MotionValue

/**
 * Data class representing the elevation of a CardView during different stages of a motion event.
 *
 * @property cvElevationEnter The elevation of the CardView when the motion event enters.
 * @property cvElevationIn The elevation of the CardView during the motion event.
 * @property cvElevationExit The elevation of the CardView when the motion event exits.
 */
data class CardViewElevation(
    val cvElevationEnter: Float,
    val cvElevationIn: Float,
    val cvElevationExit: Float
) : MotionValue

/**
 * Single color or gradient animation "enter, exit and in" values
 * @param gEnter The color or gradient at the start of the animation.
 * @param gIn The color or gradient during the animation.
 * @param gExit The color or gradient at the end of the animation.
 */
data class ColorGradient(
    val gEnter: ArrayList<Color>,
    val gIn: ArrayList<Color>,
    val gExit: ArrayList<Color>
) : MotionValue

/**
 * Scroll x animation "enter, exit and in" values
 * @param sEnter The scroll x value at the start of the animation.
 * @param sIn The scroll x value during the animation.
 * @param sExit The scroll x value at the end of the animation.
 */
data class ScrollX(val sEnter: Float, val sIn: Float, val sExit: Float) : MotionValue

/**
 * Represents the offset values for an indicator during various stages of a motion event.
 * This encapsulates the different offset values used for entering, in-progress, and exiting states.
 *
 * @property ioEnter The offset value when the indicator is entering.
 * @property ioIn The offset value when the indicator is in the active state.
 * @property ioExit The offset value when the indicator is exiting.
 */
data class IndicatorOffset(
    val ioEnter: Float,
    val ioIn: Float,
    val ioExit: Float
) : MotionValue

/**
 * Represents the width values for an indicator during various stages of a motion event.
 * This encapsulates the different width values used for entering, in-progress, and exiting states.
 *
 * @property iwEnter The width value when the indicator is entering.
 * @property iwIn The width value when the indicator is in the active state.
 * @property iwExit The width value when the indicator is exiting.
 */
data class IndicatorWidth(
    val iwEnter: Float,
    val iwIn: Float,
    val iwExit: Float
) : MotionValue

/**
 * Data class representing rotation values for motion.
 *
 * @property dEnter The rotation value at the start of the motion.
 * @property dIn The rotation value during the motion.
 * @property dExit The rotation value at the end of the motion.
 *
 * @constructor Creates a new Rotation instance with specified enter, in, and exit values.
 */
data class Rotation(
    val dEnter: Float,
    val dIn: Float,
    val dExit: Float
) : MotionValue

/**
 * `CornerRadius` is a data class that represents the corner radius values for different states of a motion.
 *
 * @property crEnter The corner radius value when the motion enters.
 * @property crIn The corner radius value when the motion is in progress.
 * @property crExit The corner radius value when the motion exits.
 */
data class CornerRadius(
    val crEnter: Float,
    val crIn: Float,
    val crExit: Float
) : MotionValue

/**
 * Types of motion properties supported and used to build animator sets
 * @param propertyName The name of the property.
 * @param index The index of the property.
 */
enum class MotionTypeKey(val propertyName: String, val index: Int) {
    None("none", 2),
    TranslationX("translationX", 4),
    TranslationXTarget("translationX", 6),
    TranslationY("translationY", 8),
    TranslationYTarget("translationY", 12),
    Alpha("alpha", 16),
    Scale("scale", 32),
    ScaleX("scaleX", 64),
    ScaleY("scaleY", 128),
    Resize("resize", 256),
    ScrollX("scrollX", 512),
    Elevation("elevation", 1024),
    Color("color", 1200),
    CornerRadius("radius", 1201),
    CardViewElevation("cardElevation", 1203),

    // not used in XML - run with draw() method
    IndicatorOffset("indicatorLeft", 1300),

    // not used in XML - run with draw() method
    IndicatorWidth("indicatorWidth", 1400),

    Rotation("rotation", 1500)
}

/**
 * Motion states
 * @param index The index of the state.
 */
enum class MotionState(val index: Int) {
    Entering(0),
    Exiting(1)
}

/**
 * Motion scale factors
 * @param scaleFactor The scale applied to the cascade.
 */
enum class MotionScaleFactor(val scaleFactor: Float) {
    CascadeBase(1f),
    CascadeLight(1.1f),
    CascadeNormal(1.15f),
    CascadeMedium(1.25f),
    CascadeMedium2(1.4f),
    CascadeLarge(1.5f),
    CascadeTest(4.0f)
}

/**
 * Fluent 2 motion durations
 * @param speedInMillis The speed of the motion in milliseconds.
 */
enum class MotionDuration(val speedInMillis: Long) {
    ZeroDuration(0L), // for testing if needed
    DurationShort01(50L),
    DurationShort02(100L),
    DurationShort03(150L),
    DurationMedium01(200L),
    DurationMedium02(250L),
    DurationMedium03(300L),
    DurationLong01(400L),
    DurationLong02(500L),
    ExtendedLong01(750L),
    ExtendedLong02(900L),
    TestableSlow(500L),
    SearchExpand(266L),
    Search166(166L),
    Search116(116L),
    Search183(116L),
    Search80(80L),
    SearchWithAnimationBuffer(280L),
    ViewPagerOut(80L),
    ResetSelectedPivotDuration(60L),
    ShimmerTransitionDelay(3000L),
    AppBarHeaderResetDuration(250L)
}

/**
 * Fluent 2 motion curves
 * @param index The index of the curve.
 * @param easing The cubic bezier easing for the curve.
 */
enum class MotionCurve(val index: Int, val easing: CubicBezierEasing) {
    EasingLinear(0, CubicBezierEasing(0f, 0f, 1f, 1f)),
    EasingEase01(1, CubicBezierEasing(0.33f, 0f, 0.67f, 1f)),
    EasingEase02(2, CubicBezierEasing(0f, 0f, 0.2f, 1f)),
    EasingDecelerate01(3, CubicBezierEasing(0.33f, 0f, 0.1f, 1f)),
    EasingDecelerate02(4, CubicBezierEasing(0f, 0f, 0f, 1f)),
    EasingDecelerate03(5, CubicBezierEasing(0.1f, 0.9f, 0.2f, 1f)),
    EasingAcelerate01(6, CubicBezierEasing(0.8f, 0f, 0.78f, 1f)),
    EasingAcelerate02(7, CubicBezierEasing(1f, 0f, 1f, 1f)),
    EasingAcelerate03(8, CubicBezierEasing(0.9f, 0.1f, 1f, 0.2f))
}

/**
 * Fluent 2 motion curves converted to android interpolators
 * @param index The index of the interpolator.
 * @param interpolator The path interpolator for the curve.
 */
enum class MotionInterpolator(val index: Int, val interpolator: Interpolator) {
    EasingLinear(0, PathInterpolatorCompat.create(0f, 0f, 1f, 1f)),
    EasingEase01(1, PathInterpolatorCompat.create(0.33f, 0f, 0.67f, 1f)),
    EasingEase02(2, PathInterpolatorCompat.create(0f, 0f, 0.2f, 1f)),
    EasingDecelerate01(3, PathInterpolatorCompat.create(0.33f, 0f, 0.1f, 1f)),
    EasingDecelerate02(4, PathInterpolatorCompat.create(0f, 0f, 0f, 1f)),
    EasingDecelerate03(5, PathInterpolatorCompat.create(0.1f, 0.9f, 0.2f, 1f)),
    EasingAcelerate01(6, PathInterpolatorCompat.create(0.8f, 0f, 0.78f, 1f)),
    EasingAcelerate02(7, PathInterpolatorCompat.create(1f, 0f, 1f, 1f)),
    EasingAcelerate03(8, PathInterpolatorCompat.create(0.9f, 0.1f, 1f, 0.2f))
}