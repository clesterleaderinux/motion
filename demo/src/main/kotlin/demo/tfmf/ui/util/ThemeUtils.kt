package demo.tfmf.ui.util

import android.content.Context
import android.content.res.Resources.Theme
import android.util.TypedValue
import androidx.annotation.ColorInt

object ThemeUtils {
    /**
     * Gets the resource id from the attribute id based on the current theme set
     *
     * @param attributeId The attribute id
     * @return The resource id
     */
    fun getResourceIdFromAttribute(theme: Theme, attributeId: Int): Int {
        val resultTypedValue = TypedValue()
        theme.resolveAttribute(attributeId, resultTypedValue, true)
        return resultTypedValue.resourceId
    }

    /**
     * Gets the color value form the attribute id based on the them of the context
     *
     * @param context The context with the theme
     * @param attributeId The attribute id
     *
     * @return The color value for the given attribute id
     */
    @ColorInt
    fun getColorFromAttribute(context: Context, attributeId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attributeId, typedValue, true)
        return typedValue.data
    }

    /**
     * Gets the float value from the attribute id based on the theme of the context
     *
     * @param context The context with the theme
     * @param attributeId The attribute id
     *
     * @return The float value for the given attribute id
     */
    fun getFloatFromAttribute(context: Context, attributeId: Int): Float {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attributeId, typedValue, true)
        return typedValue.float
    }
}

