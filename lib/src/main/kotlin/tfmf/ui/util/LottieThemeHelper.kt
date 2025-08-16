package tfmf.mobile.ui.util

import android.graphics.Color
import android.graphics.ColorFilter
import androidx.annotation.ColorInt
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.airbnb.lottie.value.LottieValueCallback
import java.util.Locale

// This is a singleton object that helps with color-related operations for Lottie animations.
object LottieThemeColorHelper {

    /**
     * Converts a color integer to a hexadecimal color string.
     * @param intColor The color integer to be converted.
     * @return The hexadecimal color string.
     */
    private fun convertColorIntToHexString(@ColorInt intColor: Int): String {
        // The color integer is bitwise ANDed with 0xFFFFFF to get the last 6 digits that represent the RGB color.
        // Then it's converted to a hexadecimal string and made uppercase.
        return String.format("#%06X", 0xFFFFFF and intColor).uppercase(Locale.getDefault())
    }

    /**
     * Updates the color for a specific key path in a Lottie animation.
     * @param view The Lottie animation view to be updated.
     * @param intColor The color integer to be used.
     * @param keyPath The key path in the Lottie animation to be updated.
     */
    @JvmStatic
    fun updateColorForKeyPath(view: LottieAnimationView?, @ColorInt intColor: Int, keyPath: KeyPath) {
        // A color filter is created from the color integer.
        val filter = SimpleColorFilter(Color.parseColor(convertColorIntToHexString(intColor)))
        // A Lottie value callback is created with the color filter.
        val callback: LottieValueCallback<ColorFilter> = LottieValueCallback(filter)
        // The value callback is added to the Lottie animation view for the specified key path.
        // This will change the color of the specified part of the animation.
        view?.addValueCallback(keyPath, LottieProperty.COLOR_FILTER, callback)
    }
}