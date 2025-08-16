package com.microsoft.fluentmotion.ui.xml.interfaces

import androidx.fragment.app.FragmentActivity

/**
 * Interface defining the contract for handling the shimmer animation based on data loading state.
 */
interface IShimmerDataLoaded {

    /**
     * Property indicating whether the data for the fragment has been loaded.
     * When set to true, it should trigger any associated events or actions to indicate that data loading is complete.
     */
    var triggerDataHasLoadedEvent: Boolean

    /**
     * Observer for the shimmer data loading events.
     * Implementations of this observer are notified when it's appropriate to hide the shimmer animation.
     */
    var iShimmerDataLoadedObserver: IShimmerDataLoadedObserver?
}

/**
 * Observer interface for receiving notifications about the shimmer data loading completion.
 */
interface IShimmerDataLoadedObserver {

    /**
     * Called to hide the shimmer animation once the data has been loaded.
     */
    fun hideShimmerOnDataLoad()
}