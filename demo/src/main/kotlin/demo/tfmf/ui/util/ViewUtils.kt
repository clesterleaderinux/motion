package demo.tfmf.ui.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import androidx.annotation.Dimension
import androidx.annotation.Dimension.Companion.DP
import androidx.annotation.StyleableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.microsoft.fluentmotion.R

object ViewUtils {

    /**
     * Converts dp units to pixel units based on device density.
     * @param context The Context the view is running in, through which it can access the current theme,
     * resources, etc.
     * @param dp The value in dp to be converted.
     * @return The converted value in pixels.
     */
    fun dpToPx(context: Context, @Dimension(unit = DP) dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.resources.displayMetrics)
    }

    /**
     * Converts dp units from Float for more precision to pixel units based on device density.
     * @param context The Context the view is running in, through which it can access the current theme,
     * resources, etc.
     * @param dp The value in dp to be converted.
     * @return The converted value in pixels.
     */
    fun dpToPx(context: Context, @Dimension(unit = DP) dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
    }

    /**
     * Converts pixels to dp.
     *
     * @param context The context to access the resources and display metrics.
     * @param px The value in pixels to be converted to dp.
     * @return The converted value in dp.
     */
    fun pxToDp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
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
     * Calculates the maximum animated width for a view by considering the container width,
     * a percentage of the maximum width, and any additional space to be removed.
     *
     * @param containerWidth The total width of the container.
     * @param percentMax The percentage of the container width to be used as the maximum width.
     * Default is 0.9 (90%).
     * @param removedSpace Vararg parameter representing the space to be removed from the maximum width.
     * @return The calculated maximum animated width greater > || = 0f
     */
    fun getMaxAnimatedWidthForView(
        containerWidth: Float,
        percentMax: Float = 0.9f,
        vararg removedSpace: Int
    ): Float {
        return ((containerWidth * percentMax) - removedSpace.sum()).coerceAtLeast(0f)
    }

    /**
     * Retrieves a drawable resource using AppCompatResources for compatibility with older Android versions.
     *
     * @param context The context to use for retrieving the drawable.
     * @param attrs The TypedArray containing the attributes.
     * @param index The index of the drawable attribute in the TypedArray.
     * @return The drawable if found, or null if the resource ID is not valid.
     */
    fun getAppCompatDrawableForResource(
        context: Context,
        attrs: TypedArray,
        @StyleableRes index: Int
    ): Drawable? {
        val drawableResId = attrs.getResourceId(index, 0)
        val drawable = if (drawableResId != 0) {
            AppCompatResources.getDrawable(context, drawableResId)
        } else {
            null
        }
        return drawable
    }

    /**
     * See [ViewUtils.isScreenSizeSufficientForImage() in ODSPCore]
     * Checks if the current screen size is large enough to show an image in the main content view.
     *
     * @param context The calling context
     * @param minimumHeightRequired The minimum height required to show an image
     * @return True if the device is a tablet or if it is in portrait mode or if the shortest screen dimension
     * is greater than the minimum required screen height for showing images
     */
    fun isScreenSizeSufficientForImage(context: Context, minimumHeightRequired: Int): Boolean {
        val resources: Resources = context.resources
        val isTablet: Boolean = resources.getBoolean(R.bool.is_tablet_size)
        val isPortraitMode: Boolean = resources.configuration.orientation ==
            Configuration.ORIENTATION_PORTRAIT

        // Get the shortest screen dimension in pixels
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val shortestScreenDimensionInPixels = minOf(displayMetrics.widthPixels, displayMetrics.heightPixels)

        return isTablet || isPortraitMode || shortestScreenDimensionInPixels >= minimumHeightRequired
    }

    /**
     * Creates a transparent drawable with rounded corners.
     *
     * This function creates a `GradientDrawable` with a transparent color, semi transparent
     * and rounded corners. The corner radius is specified by the `radius` parameter.
     *
     * @param context The context to use for resource access.
     * @param radius The radius of the corners in pixels.
     * @return A `Drawable` with a transparent color and rounded corners.
     */
    fun createCircularDrawable(context: Context, color: Int, radius: Float): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(ContextCompat.getColor(context, color))
            cornerRadius = radius
        }
    }
}