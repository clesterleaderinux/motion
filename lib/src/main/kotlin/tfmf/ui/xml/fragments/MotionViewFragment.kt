package tfmf.mobile.ui.xml.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tfmf.mobile.ui.Alpha
import tfmf.mobile.ui.MotionTypeKey
import tfmf.mobile.ui.Scale
import tfmf.mobile.ui.TranslationX
import tfmf.mobile.ui.xml.base.MotionViewBase

/**
 * A fragment class for displaying motion views.
 */
open class MotionViewFragment : Fragment() {

    /**
     * A reference to this.layout to apply custom animations.
     */
    open var fragmentLayout: Int = 0

    /**
     * Rootview to use as reference in fragment animations
     */
    lateinit var rootView: View

    /**
     * Fade out values for fragment
     */
    private val alphaMotionValues = hashMapOf(
        MotionTypeKey.Alpha.name to Alpha(aEnter = 1f, aIn = 0f, aExit = 0f),
    )

    /**
     * Translation and scale values to shift fragment from left in transitions
     */
    private val outLeftMotionValues = hashMapOf(
        MotionTypeKey.TranslationX.name to TranslationX(xEnter = 300f, xIn = 0f, xExit = 0f),
        MotionTypeKey.Scale.name to Scale(sEnter = 0.8f, sIn = 1f, sExit = 1f),
        MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 1f),
    )

    /**
     * Translation and scale values to shift fragment from right in transitions
     */
    private val outRightMotionValues = hashMapOf(
        MotionTypeKey.TranslationX.name to TranslationX(xEnter = -300f, xIn = 0f, xExit = 0f),
        MotionTypeKey.Scale.name to Scale(sEnter = 0.8f, sIn = 1f, sExit = 1f),
        MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 1f),
    )

    /**
     * Animation to play as you flip through views in a viewpager
     */
    private val scaleFadeMotionValues = hashMapOf(
        MotionTypeKey.Scale.name to Scale(sEnter = 0.9f, sIn = 1f, sExit = 1f),
        MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 1f),
    )

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(fragmentLayout, container, false)
    }

    /**
     * Translates and scales the root view based on the direction of the slide.
     *
     * @param slideLeft If true, the root view will enter with a slide from the right to the left.
     *                  If false, the root view will exit with a slide from the left to the right.
     */
    fun translateAndScale(slideRight: Boolean) {
        val outLeft = MotionViewBase(motionViewBase = rootView, motionValues = outLeftMotionValues)
        val outRight = MotionViewBase(motionViewBase = rootView, motionValues = outRightMotionValues)
        if (slideRight) {
            outRight.enter()
        } else {
            outLeft.enter()
        }
    }

    /**
     * Animation run as you slide through viewpager views not via tab.select
     */
    fun scaleFadeIn() {
        MotionViewBase(motionViewBase = rootView, motionValues = scaleFadeMotionValues).enter()
    }

    /**
     * Fadeout fragment called from viewpager
     */
    fun fadeOut() {
        MotionViewBase(motionViewBase = rootView, motionValues = alphaMotionValues).enter()
    }
}