package tfmf.ui.motion.xml.interfaces

import tfmf.ui.motion.xml.components.MotionViewTabLayout

interface IMotionViewTabLayoutContainer {

    /**
     * Gets the `MotionViewTabLayout` instance.
     *
     * @return the `MotionViewTabLayout` instance.
     */
    fun getMotionViewTabLayout(): MotionViewTabLayout?
}