package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import demo.tfmf.ui.R
import tfmf.ui.motion.MotionDuration
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.TranslationX
import tfmf.ui.motion.TranslationY
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewSharedFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class SharedTransitionExitFragment : MotionViewSharedFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_shared_exit, container, false)
        val imageView = layout.findViewById<MotionViewLinearLayout>(R.id.shared_exit_image)
        imageView.transitionName = "filePhotoTransition"
        val mvb = resizeBoth(imageView)
        mvb.enter()
        return layout
    }

    private fun resizeBoth(view: View): MotionViewBase {
        val motionValues = hashMapOf(
            MotionTypeKey.TranslationY.name to TranslationY(yEnter = 100f, yIn = 0f, yExit = 0f),
            MotionTypeKey.TranslationX.name to TranslationX(xEnter = 0f, xIn = 0f, xExit = 0f),
        )
        return MotionViewBase(
            motionViewBase = view,
            motionValues = motionValues,
            duration = MotionDuration.DurationMedium03,
        )
    }
}