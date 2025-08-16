package com.microsoft.fluentmotion.ui.util

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.SweepGradient
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.view.animation.Interpolator
import androidx.annotation.Dimension
import androidx.annotation.MainThread
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import com.microsoft.fluentmotion.R
import com.microsoft.fluentmotion.ui.MotionInterpolator
import com.microsoft.fluentmotion.ui.MotionLinkComposableProps
import com.microsoft.fluentmotion.ui.MotionScaleFactor
import com.microsoft.fluentmotion.ui.MotionTypeKey
import com.microsoft.fluentmotion.ui.MotionValue
import com.microsoft.fluentmotion.ui.actions.CancellationError
import com.microsoft.fluentmotion.ui.telemetry.ITelemetryLoggable
import com.microsoft.fluentmotion.ui.telemetry.TelemetryEvent
import com.microsoft.fluentmotion.ui.xml.base.IMotionView
import com.microsoft.fluentmotion.ui.xml.layouts.MotionViewCardViewLayout
import com.microsoft.fluentmotion.ui.xml.player.MotionPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.random.Random


@Suppress("unused")
// Utility class providing various methods for motion and animation management.
object MotionUtil : ITelemetryLoggable {
    /**
     * A map to manage motion chains identified by a unique string key.
     */
    var motionChains = HashMap<String, ArrayList<MotionLinkComposableProps>>()

    /**
     * A set to keep track of running animation coroutines, allowing cancellation.
     */
    var runningAnimationCoroutines = mutableMapOf<String, CoroutineScope?>()

    /**
     * Disable the animations when this is set to false.
     */
    var animationsEnabled = true

    /**
     * Running animator sets
     */
    var runningAnimatorSets = mutableListOf<AnimatorSet>()

    /**
     * Generates a random color by constructing a Color object from random RGB values.
     * @return Color object with random RGB values.
     */
    fun generateRandomColor(): Color {
        val random = Random(System.currentTimeMillis())

        val red = random.nextInt(256)
        val green = random.nextInt(256)
        val blue = random.nextInt(256)

        return Color(red, green, blue)
    }

    /**
     * Clears running animator sets
     */
    @MainThread
    fun clearRunningAnimatorSets() {
        // Make a copy of the list to avoid `cancel` removing from the list while iterating. This happens
        // because `cancel` calls `onEnd` listener which calls `removeRunningAnimatorSet
        val animatorSets = runningAnimatorSets
        runningAnimatorSets = mutableListOf()

        for (animatorSet in animatorSets) {
            animatorSet.cancel()
        }
    }

    /**
     * Add running animator sets
     * @param animatorSet currently active animator set to add to the list
     */
    @MainThread
    fun appendRunningAnimatorSet(animatorSet: AnimatorSet) {
        runningAnimatorSets.add(animatorSet)
    }

    /**
     * Remove animator set that has finished running
     * @param animatorSet The animator set that finished running and should be removed from the list
     */
    @MainThread
    fun removeRunningAnimatorSet(animatorSet: AnimatorSet) {
        runningAnimatorSets.remove(animatorSet)
    }

    /**
     * Adds a motion link to a motion chain identified by a chainId.
     * @param motionLinkComposableProps The motion link to be added.
     * @param chainId The ID of the chain to which the link will be added.
     */
    fun addMotionChainLink(motionLinkComposableProps: MotionLinkComposableProps, chainId: String) {
        // Get the existing array list or create a new one if it doesn't exist.
        val arrayList = motionChains[chainId] ?: ArrayList<MotionLinkComposableProps>().also {
            motionChains[chainId] = it
        }
        arrayList.add(motionLinkComposableProps)
    }

    /**
     * Clears all motion links from a specified chain.
     * @param chainId The ID of the chain to be cleared.
     */
    fun clearChain(chainId: String) {
        motionChains[chainId]?.clear()
    }

    /**
     * Checks whether a flag is contained within a set of flags (bitwise OR operation).
     * @param flagSet The set of flags.
     * @param flag The flag to check.
     * @return True if the flag is contained within the set, false otherwise.
     */
    fun containsFlag(flagSet: Int, flag: Int) = (flagSet or flag) == flagSet

    /**
     * Determines if a map of motion values contains a specific motion property.
     * @param motionValues The map of motion values.
     * @param motionProperty The motion property to check.
     * @return True if the map contains the motion property, false otherwise.
     */
    fun containsValueType(
        motionValues: Map<String, MotionValue?>,
        motionTypeKey: MotionTypeKey
    ): Boolean {
        return motionValues.containsKey(motionTypeKey.name)
    }

    /**
     * Appends a CoroutineScope to the set of running animations after it cancels an existing routine for the key.
     * @param chainIdForCoroutineScope The unique identifier for the CoroutineScope.
     * @param coroutineScope The CoroutineScope to be added.
     */
    fun appendRunningAnimationCoroutine(chainIdForCoroutineScope: String, coroutineScope: CoroutineScope?) {
        runningAnimationCoroutines[chainIdForCoroutineScope] = coroutineScope
    }

    /**
     * Cancels and removes all running animation coroutines.
     *
     * @param cancellationError The type of cancellation error. Default is [CancellationError.Default].
     */
    fun cancelAllRunningAnimationCoroutines(cancellationError: CancellationError? = CancellationError.Default) {
        for ((chainIdForCoroutineScope, _) in runningAnimationCoroutines) {
            runningAnimationCoroutines[chainIdForCoroutineScope]?.cancel()
        }
        val logMessage =
            "Canceled all coroutine animations with cancellation type '${cancellationError?.name}'."
        logTelemetryForAction(TelemetryEvent.Cancellation, logMessage)
    }

    /**
     * Cancels the animation coroutine for a specific chain ID.
     *
     * @param chainIdForCoroutineScope The chain ID for the coroutine scope.
     * @param cancellationError The type of cancellation error. Default is [CancellationError.Default].
     */
    fun cancelAnimationCoroutine(
        chainIdForCoroutineScope: String?,
        cancellationError: CancellationError? = CancellationError.Default
    ) {
        runningAnimationCoroutines[chainIdForCoroutineScope]?.cancel()
        val logMessage =
            "Canceled animations for coroutine '$chainIdForCoroutineScope' with cancellation type '${cancellationError?.name}'."
        logTelemetryForAction(TelemetryEvent.Cancellation, logMessage)
    }

    /**
     * Removes a specific CoroutineScope from the set of running animations.
     * @param chainIdForCoroutineScope The unique identifier for the CoroutineScope to be removed.
     */
    fun removeRunningAnimationCoroutine(chainIdForCoroutineScope: String) {
        runningAnimationCoroutines[chainIdForCoroutineScope]?.cancel()
        runningAnimationCoroutines.remove(chainIdForCoroutineScope)
    }

    /**
     * Translates a bezier animation value into an Interpolator object.
     * @param index The index of the bezier value.
     * @return The corresponding Interpolator object.
     */
    fun convertBezierToCurve(index: Int): Interpolator {
        return MotionInterpolator.values()[index].interpolator
    }

    /**
     * Appends an ObjectAnimator to a collection of animators for a property change.
     * @param property The property to be animated.
     * @param start The starting value of the animation.
     * @param end The ending value of the animation.
     * @param animationCollection The collection of animators.
     * @param aInterpolator The interpolator to be used for the animation.
     * @param motionView The view to be animated.
     */
    fun appendObjectAnimator(
        property: MotionTypeKey,
        start: Float,
        end: Float,
        animationCollection: MutableList<Animator?>,
        aInterpolator: Interpolator?,
        motionView: View?
    ) {
        animationCollection.add(
            ObjectAnimator.ofFloat(
                motionView,
                property.propertyName,
                start,
                end,
            ).apply { interpolator = aInterpolator },
        )
    }

    /**
     * Appends an ObjectAnimator to the provided animation collection.
     *
     * @param property The property to be animated.
     * @param targetPosition The target position for the animation.
     * @param animationCollection The collection to which the animator will be added.
     * @param aInterpolator The interpolator to be used for the animation.
     * @param motionView The view to be animated.
     */
    fun appendObjectAnimator(
        property: MotionTypeKey,
        targetPosition: Float,
        animationCollection: MutableList<Animator?>,
        aInterpolator: Interpolator?,
        motionView: View?
    ) {
        animationCollection.add(
            ObjectAnimator.ofFloat(motionView, property.propertyName, targetPosition).apply {
                interpolator = aInterpolator
            },
        )
    }

    /**
     * Special case appending an ObjectAnimator for scrolling animations.
     * @param end The ending value of the animation.
     * @param animationCollection The collection of animators.
     * @param motionView The view to be animated.
     */
    fun appendScrollObjectAnimator(
        end: Float,
        animationCollection: MutableList<Animator?>,
        motionView: View?
    ) {
        val animator = ObjectAnimator.ofInt(motionView, MotionTypeKey.ScrollX.propertyName, end.toInt())
        animationCollection.add(animator)
    }

    /**
     * Appends a ValueAnimator for size change animations.
     * @param property The property to be animated.
     * @param start The starting value of the animation.
     * @param end The ending value of the animation.
     * @param animationCollection The collection of animators.
     * @param aInterpolator The interpolator to be used for the animation.
     * @param motionView The view to be animated.
     * @param isHeight A boolean indicating whether the height is to be animated.
     */
    fun appendValueAnimator(
        property: MotionTypeKey,
        start: Int,
        end: Int,
        animationCollection: MutableList<Animator?>,
        aInterpolator: Interpolator?,
        motionView: View?,
        isHeight: Boolean? = false
    ) {
        if (property == MotionTypeKey.Resize) {
            appendContentResizeValueAnimator(
                property = property,
                motionView = motionView,
                animationCollection = animationCollection,
                aInterpolator = aInterpolator,
                start = start,
                end = end,
                isHeight = isHeight == true,
            )
        }
    }

    /**
     * Appends a corner radius value animator to the provided animation collection.
     *
     * @param property The motion type key.
     * @param motionView The view to be animated.
     * @param animationCollection The collection of animators to which the new animator will be added.
     * @param aInterpolator The interpolator to be used for the animation.
     * @param start The starting value of the corner radius.
     * @param end The ending value of the corner radius.
     */
    fun appendCornerRadiusValueAnimator(
        property: MotionTypeKey,
        motionView: View?,
        animationCollection: MutableList<Animator?>,
        aInterpolator: Interpolator?,
        start: Float,
        end: Float,
    ) {
        if (motionView is MotionViewCardViewLayout) {
            val radiusAnimator = ValueAnimator.ofFloat(start, end)
            radiusAnimator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                animatedValue.also { motionView.radius = it }
            }
            radiusAnimator.interpolator = aInterpolator
            animationCollection.add(radiusAnimator)
        } else {
            throw IllegalArgumentException("MotionView must be an instance of MotionViewCardViewLayout")
        }
    }

    /**
     * Appends a card view elevation value animator to the provided animation collection.
     *
     * @param property The motion type key.
     * @param motionView The view to be animated.
     * @param animationCollection The collection of animators to which the new animator will be added.
     * @param aInterpolator The interpolator to be used for the animation.
     * @param start The starting value of the card view elevation.
     * @param end The ending value of the card view elevation.
     */
    fun appendCardViewElevationValueAnimator(
        property: MotionTypeKey,
        motionView: View?,
        animationCollection: MutableList<Animator?>,
        aInterpolator: Interpolator?,
        start: Float,
        end: Float,
    ) {
        if (motionView is MotionViewCardViewLayout) {
            val cardElevationAnimator = ValueAnimator.ofFloat(start, end)
            cardElevationAnimator.addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Float
                animatedValue.also { motionView.cardElevation = it }
            }
            cardElevationAnimator.interpolator = aInterpolator
            animationCollection.add(cardElevationAnimator)
        } else {
            throw IllegalArgumentException("MotionView must be an instance of MotionViewCardViewLayout")
        }
    }

    /**
     * Private helper method to handle content resizing animations.
     * @param property The property to be animated.
     * @param motionView The view to be animated.
     * @param animationCollection The collection of animators.
     * @param aInterpolator The interpolator to be used for the animation.
     * @param start The starting value of the animation.
     * @param end The ending value of the animation.
     * @param isHeight A boolean indicating whether the height is to be animated.
     */
    fun appendContentResizeValueAnimator(
        property: MotionTypeKey,
        motionView: View?,
        animationCollection: MutableList<Animator?>,
        aInterpolator: Interpolator?,
        start: Int,
        end: Int,
        isHeight: Boolean
    ) {
        if (motionView != null) {
            if (isHeight) {
                val setParam = motionView.layoutParams
                setParam.height = start
                motionView.layoutParams = setParam
                val params = motionView.layoutParams
                val hAnimator = ValueAnimator.ofInt(params.height, end)
                hAnimator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    params.height = animatedValue
                    motionView.layoutParams = params
                }
                hAnimator.interpolator = aInterpolator
                animationCollection.add(hAnimator)
            } else {
                val setParam = motionView.layoutParams
                setParam.width = start
                motionView.layoutParams = setParam
                val params = motionView.layoutParams
                val wAnimator = ValueAnimator.ofInt(params.width, end)
                wAnimator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    params.width = animatedValue
                    motionView.layoutParams = params
                }
                wAnimator.interpolator = aInterpolator
                animationCollection.add(wAnimator)
            }
        }
    }

    /**
     * Updates provided Lottie JSON string with new bitmap references from bitmapList pairs.
     * @param lottieJson The original Lottie JSON string.
     * @param bitmapList The list of pairs to replace in the Lottie JSON string.
     * @return The updated Lottie JSON string.
     */
    fun updateLottieImageData(lottieJson: String, bitmapList: List<Pair<String, String>>): String {
        // For each pair, replace first occurrence within lottieJson with second item of the pair.
        var altered = lottieJson
        bitmapList.forEach { pair ->
            altered = altered.replaceFirst(pair.first, pair.second, false)
        }
        return altered
    }

    /**
     * Encodes a bitmap image to Base64 string with specified compression quality.
     * @param bitmap The bitmap to be encoded.
     * @param compressionQuality The compression quality for the encoding.
     * @return The Base64 encoded string.
     */
    fun getEncodedBase64StringFromBitmap(bitmap: Bitmap?, compressionQuality: Int): String {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, compressionQuality, stream)
        val byteFormat: ByteArray = stream.toByteArray()

        return "data:image/png;base64," + Base64.encodeToString(byteFormat, Base64.NO_WRAP)
    }

    /**
     * Retrieves a bitmap image from the app's assets folder and resizes it to given dimensions.
     * @param context The context to access the assets.
     * @param filePath The path of the file in the assets.
     * @param width The desired width of the bitmap.
     * @param height The desired height of the bitmap.
     * @return The resized bitmap, or null if an error occurred.
     */
    fun getBitmapFromAsset(context: Context, filePath: String, width: Int, height: Int): Bitmap? {
        val assetManager: AssetManager = context.assets
        try {
            val istr = assetManager.open(filePath)
            return Bitmap.createScaledBitmap(BitmapFactory.decodeStream(istr), width, height, false)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Converts dp units to pixel units based on device density.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param dp The value in dp to be converted.
     * @return The converted value in pixels.
     */
    fun dpToPx(context: Context, @Dimension(unit = Dimension.DP) dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)
    }

    /**
     * Converts dp units from Float for more precision to pixel units based on device density.
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param dp The value in dp to be converted.
     * @return The converted value in pixels.
     */
    fun dpToPx(context: Context, @Dimension(unit = Dimension.DP) dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    /**
     * Converts pixel units to dp units based on device density.
     * @param px The value in pixels to be converted.
     * @return The converted value in dp.
     */
    fun pxToDp(context: Context, @Dimension(unit = Dimension.PX) px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * Generates a random color as an Int by combining random RGB values.
     * @return The generated random color.
     */
    fun getRandomColor(): Int {
        val red = Random.nextInt(256)
        val green = Random.nextInt(256)
        val blue = Random.nextInt(256)
        return android.graphics.Color.rgb(red, green, blue)
    }

    /**
     * Initializes a motion player for managing animations on a given view component (IMotionView).
     * @param motionView The view component to be managed by the motion player.
     * @return The initialized motion player.
     */
    fun initMotionPlayer(motionView: IMotionView): MotionPlayer {
        // Configure and return a new MotionPlayer instance with properties from the motionView.
        val i = motionView.animatableLayout()
        val mp = MotionPlayer().also {
            it.motionViewBase = motionView.motionViewBase
            it.motionView = motionView
            it.motionValues = motionView.motionValues
            it.duration = motionView.duration
            it.curveOnEnter = motionView.curveEnter
            it.curveOnExit = motionView.curveExit
            it.nextIndexDelay = motionView.chainDelay
        }
        // If a chainKey exists, add the MotionPlayer to the chain.
        motionView.chainKey?.let { MotionPlayer.addMotionPlayerForChain(it, mp) }
        return mp
    }

    /**
     * Blurs a bitmap by scaling it down, applying a blur effect, and then scaling it back up.
     *
     * This function first scales down the bitmap by a factor of 10. Then, it draws the scaled down
     * bitmap to a new bitmap with a `Paint` that has a `BlurMaskFilter`. Finally, it scales up the
     * result to the original size.
     *
     * @param bitmap The original bitmap to be blurred.
     * @param blurRadius The radius of the blur effect. A higher value will result in a stronger blur effect.
     * @return The blurred bitmap.
     */
    fun blurBitmap(bitmap: Bitmap, blurRadius: Float): Bitmap {
        // Scale down the bitmap
        val scaleRatio = 0.1f
        val width = (bitmap.width * scaleRatio).toInt()
        val height = (bitmap.height * scaleRatio).toInt()
        val smallBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)

        // Create a new bitmap and a canvas to draw on
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)

        // Create a paint with a blur mask filter
        val paint = Paint().apply {
            isAntiAlias = true
            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
        }

        // Draw the scaled down bitmap with the blur paint
        canvas.drawBitmap(smallBitmap, 0f, 0f, paint)

        smallBitmap.recycle()

        // Scale up the result
        return Bitmap.createScaledBitmap(outputBitmap, bitmap.width, bitmap.height, true)
    }


    /**
     * Creates a new bitmap with rounded corners from an original bitmap.
     *
     * This function creates a new bitmap and a canvas to draw on it. It then draws a rounded rectangle
     * onto the canvas using a paint object. The original bitmap is then drawn onto the canvas using
     * a PorterDuff xfermode, which results in only the parts of the bitmap that intersect with the
     * rounded rectangle being drawn. The new bitmap with rounded corners is then returned.
     *
     * @param bitmap The original bitmap to be rounded.
     * @param cornerRadius The radius for the corners of the bitmap in pixels.
     * @return The new bitmap with rounded corners.
     */
    fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val paint = Paint().apply {
            isAntiAlias = true
        }

        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = cornerRadius.toFloat()

        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    /**
     * Creates a BitmapDrawable with a SweepGradient effect based on the given colors.
     *
     * The function takes an array of color resource IDs, applies a sweep gradient effect,
     * and then returns the resulting image as a BitmapDrawable. Additional processing such
     * as blurring and rounding corners is applied to create the final visual effect.
     *
     * @param resources The Resources object for resolving the color resources.
     * @param colors An IntArray containing the color resource IDs that will define the sweep gradient.
     * @return A BitmapDrawable containing the generated bitmap with the applied sweep gradient.
     */
    fun createSweepGradientBitmap(context: Context, resources: Resources, sweepGradientColors: TypedArray): BitmapDrawable {
        val expandedWidth = dpToPx(context, 320)
        val expandedHeight = dpToPx(context,56)

        val resolvedColors = IntArray(sweepGradientColors.length())
        for (i in 0 until sweepGradientColors.length()) {
            resolvedColors[i] = sweepGradientColors.getColor(i, 0)
        }

        val positions = FloatArray(sweepGradientColors.length())
        for (i in 0 until sweepGradientColors.length()) {
            positions[i] = i.toFloat() / (sweepGradientColors.length() - 1)
        }
        val rotationAngle = -180f

        val centerX = expandedWidth / 2
        val centerY = expandedHeight / 2

        // Create SweepGradient
        val gradient = SweepGradient(
            centerX, // centerX
            centerY, // centerY
            resolvedColors, // color array
            positions, // position array
        )

        // Initialize Paint with gradient and properties
        val paint = Paint().apply {
            setAlpha(50)
            shader = gradient
            isAntiAlias = true
        }

        // Define pivot for rotation
        val pivotX = expandedWidth / 2f
        val pivotY = expandedHeight / 2f

        // Create bitmap and canvas to draw on
        val bitmap =
            Bitmap.createBitmap(expandedWidth.toInt(), expandedHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Perform rotation before drawing the rectangle
        canvas.rotate(rotationAngle, pivotX, pivotY)
        canvas.drawRect(0f, 0f, expandedWidth, expandedHeight, paint)

        // Apply additional effects like rounding corners and blurring
        val rounded = getRoundedCornerBitmap(bitmap, 150)
        val mutableBitmap = rounded.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        bitmap.recycle()

        return blurBitmap(mutableBitmap, 24f).toDrawable(resources)
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
     * Checks if the device is a tablet.
     *
     * @param context The context of the caller.
     * @return True if the device is a tablet, false otherwise.
     */
    fun isTablet(context: Context): Boolean {
        return context.resources.getBoolean(R.bool.is_tablet_size)
    }

    /**
     * Computes the x position of a view to
     * be used with the Tab sliding indicator.
     *
     * @param view The target view.
     * @return The x position in the master view.
     */
    fun xPositionOfView(view: View): Int {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location[0]
    }

    /**
     * Sets the cascade parameters for the given views.
     * This includes resetting the alpha and scale properties of each view.
     *
     * @param views The views for which to reset the cascade parameters.
     */
    @JvmStatic
    fun setTheBaseCascadeStateForViews(views: List<View>) {
        for (view in views) {
            view.apply {
                alpha = 0f
                scaleX = MotionScaleFactor.CascadeMedium.scaleFactor
                scaleY = MotionScaleFactor.CascadeMedium.scaleFactor
                invalidate()
            }
        }
    }
}
