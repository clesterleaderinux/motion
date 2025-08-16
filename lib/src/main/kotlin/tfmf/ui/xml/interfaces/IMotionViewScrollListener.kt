package com.microsoft.fluentmotion.ui.xml.interfaces

/**
 * Interface for listening to motion view scroll events.
 *
 * @property onScrolledUp Function to be invoked when a scroll up event occurs.
 * The parameter indicates whether scrolling up has occurred.
 */
interface IMotionViewScrollListener {

    fun onScrolled()
    fun onScrolledUp(scrolledUp: Boolean)
    fun onSettled()
}