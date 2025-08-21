package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import demo.tfmf.ui.R
import demo.tfmf.ui.activity.DemoActivity
import tfmf.ui.motion.Alpha
import tfmf.ui.motion.MotionDuration
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.TranslationX
import tfmf.ui.motion.TranslationY
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewSharedFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class SharedTransitionEnterFragment : MotionViewSharedFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_shared_enter, container, false)
        val imageView = layout.findViewById<MotionViewLinearLayout>(R.id.shared_enter_image)
        imageView.transitionName = "filePhotoTransition"
        val slideOut = layout.findViewById<MotionViewLinearLayout>(R.id.slideout_bottom)

        imageView.setOnClickListener {
            val mvb = translateBoth(imageView)
            mvb.enter()
            (activity as? DemoActivity)?.let { it ->
                it.switchToContinuosMotion(imageView, "filePhotoTransition")
            }
        }
        return layout
    }

    private fun translateBoth(view: View): MotionViewBase {
        val motionValues = hashMapOf(
            MotionTypeKey.TranslationY.name to TranslationY(yEnter = 0f, yIn = -500f, yExit = 0f),
            MotionTypeKey.TranslationX.name to TranslationX(xEnter = 0f, xIn = 0f, xExit = 0f),
        )
        return MotionViewBase(
            motionViewBase = view,
            motionValues = motionValues,
            duration = MotionDuration.DurationMedium01,
            onEndAction = {},
        )
    }

    private fun slideOut(view: View): MotionViewBase {
        val motionValues = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 1f, aIn = 0f, aExit = 0f),
        )
        return MotionViewBase(
            motionViewBase = view,
            motionValues = motionValues,
            duration = MotionDuration.DurationMedium01,
        )
    }
}