package com.microsoft.fluentmotion.ui.xml.components

import androidx.annotation.ColorInt
import androidx.annotation.RawRes

/**
 * Data class representing the properties of a [LottieAnimationButton]
 *
 * @property animationId the resource id of the animation
 * @property strokeLayerName the name of the layer associated with the stroke colour in the Lottie animation
 * @property fillLayerName the name of the layer associated with the fill colour in the Lottie animation
 * @property enabledStrokeColor the colour of the stroke when the button is enabled
 * @property disabledStrokeColor the colour of the stroke when the button is disabled
 * @property disabledFillColor the fill colour when the button is disabled
 * @property titleText the title text for the button
 * @property activeTextColor the colour of the text when the button is in its active state (selected)
 * @property inactiveTextColor the colour of the text when the button is in its inactive state (not selected)
 * @property disabledTextColor the colour of the text when the button is disabled
 */
data class LottieAnimationButtonProperties(
    @RawRes val animationId: Int,
    val strokeLayerName: String,
    val fillLayerName: String,
    @ColorInt val enabledStrokeColor: Int,
    @ColorInt val disabledStrokeColor: Int,
    @ColorInt val disabledFillColor: Int,
    val titleText: String,
    @ColorInt val activeTextColor: Int,
    @ColorInt val inactiveTextColor: Int,
    @ColorInt val disabledTextColor: Int
)
