package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout.LayoutParams
import demo.tfmf.ui.ComponentUtil
import demo.tfmf.ui.R
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.Scale
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class ScaleFragment : MotionViewFragment() {

    private var onEnter = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_scale, container, false)
        val scale = layout.findViewById<MotionViewLinearLayout>(R.id.scale)
        val view = View(context)
        val scaleXML = layout.findViewById<MotionViewLinearLayout>(R.id.scale_in_xml)
        val enterExit = layout.findViewById<Button>(R.id.enter_exit)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.Scale.name to Scale(sEnter = 1f, sIn = 2f, sExit = 1f),
        )
        val mvb = MotionViewBase(motionViewBase = view, motionValues = motionValues)
        enterExit.setOnClickListener {
            if (onEnter) {
                mvb.enter()
                scaleXML.enter()
            } else {
                mvb.exit()
                scaleXML.exit()
            }
            onEnter = !onEnter
        }
        scale.addView(mvb.motionViewBase)

        return layout
    }
}