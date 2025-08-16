package com.microsoft.fluentmotion.ui.xml.interfaces

import com.microsoft.fluentmotion.ui.xml.components.MotionViewTabLayout

interface IMotionViewTabLayoutContainer {

    /**
     * Gets the `MotionViewTabLayout` instance.
     *
     * @return the `MotionViewTabLayout` instance.
     */
    fun getMotionViewTabLayout(): MotionViewTabLayout?
}