package demo.tfmf.ui.fragment.xmlsample

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import demo.tfmf.ui.R
import tfmf.ui.motion.Alpha
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class AlphaFragment : MotionViewFragment() {

    private var onEnter = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_alpha, container, false)
        val alpha = layout.findViewById<MotionViewLinearLayout>(R.id.alpha)
        val view = View(context)
        val alphaXML = layout.findViewById<MotionViewLinearLayout>(R.id.alpha_in_xml)
        val enterExit = layout.findViewById<AppCompatButton>(R.id.enter_exit)
        view.layoutParams = LinearLayout.LayoutParams(400, 400)
        view.setBackgroundColor(Color.BLUE)
        val motionValues = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0.1f, aIn = 1f, aExit = 0.1f),
        )
        val mvb = MotionViewBase(motionViewBase = view, motionValues = motionValues)
        enterExit.setOnClickListener {
            if (onEnter) {
                mvb.enter()
                alphaXML.enter()
            } else {
                mvb.exit()
                alphaXML.exit()
            }
            onEnter = !onEnter
        }
        alpha.addView(mvb.motionViewBase)

        return layout
    }
}