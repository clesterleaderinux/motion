package com.microsoft.fluentmotion.ui.xml.interfaces

import android.view.View

/**
 * This interface represents a fragment that supports cascading animations.
 * Implement this interface if your fragment needs to play cascading animations on enter and exit.
 */
interface IMotionViewCascadableFragment {

    /**
     * returns the body views for the transition cascade animation
     */
    fun getCascadeBody(): List<View>

    /**
     * boolean to tell you if you entered by a Files/Photos change
     * or regular pivot button is pressed
     */
    var switchedByAppMode: Boolean
}