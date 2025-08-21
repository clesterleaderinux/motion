package tfmf.ui.motion.xml.interfaces

import android.view.View

/**
 * Interface for listening to slider element click events.
 */
interface SliderElementListener {
    /**
     * Called when a slider element is clicked.
     * @param sliderId The ID of the clicked slider.
     * @param sliderWidth The width of the clicked slider.
     * @param smoothScrollTo The position to smoothly scroll to.
     * @param sliderAnchorPosition The anchor position of the slider.
     * @param withSmoothScroll A boolean indicating whether to use smooth scrolling.
     */
    fun onSliderElementClicked(
        sliderId: Int,
        sliderWidth: Int,
        smoothScrollTo: Float,
        sliderAnchorPosition: Float,
        withSmoothScroll: Boolean
    )
}

/**
 * Interface for listening to slider fragment switch events.
 */
interface SliderFragmentSwitchListener {
    /**
     * Called when the view pager and slider position need to be switched.
     * @param sliderId The ID of the slider.
     */
    fun switchViewPagerAndSliderPosition(
        sliderId: Int
    )
}

/**
 * Interface for views used in the slider/tabs.
 *
 * This interface defines the necessary methods that a view should implement
 * to be used as a slider in the tabs.
 */
interface ISliderView {
    /**
     * Gets the view.
     *
     * @return The view.
     */
    fun getView(): View

    /**
     * Gets the slider ID.
     *
     * @return The slider ID.
     */
    fun getSliderId(): Int

    /**
     * Calculates the anchor position of the slider.
     *
     * @return The anchor position of the slider.
     */
    fun sliderAnchorPosition(): Int

    /**
     * Performs an action.
     */
    fun performAction()

    /**
     * Gets the width of the slider.
     *
     * @return The width of the slider.
     */
    fun sliderWidth(): Int

    /**
     * Gets the minimum tab width to fit tab content, in pixels.
     */
    fun getTabMinWidth(): Int

    /**
     * Gets the padding of the slider.
     *
     * @return The padding of the slider.
     */
    fun sliderPadding(): Int

    /**
     * Sets the slider as active.
     */
    fun setActive()

    /**
     * Sets the slider as inactive.
     */
    fun setInactive()

    /**
     * Sets the slider as enabled.
     *
     * @param isActive A boolean indicating whether the slider is active.
     */
    fun setEnabled(isActive: Boolean)

    /**
     * Sets the slider as disabled.
     */
    fun setDisabled()
}