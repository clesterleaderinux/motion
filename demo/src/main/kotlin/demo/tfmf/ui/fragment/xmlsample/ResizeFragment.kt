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
import tfmf.ui.motion.Resize
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class ResizeFragment : MotionViewFragment() {

    private var onEnter = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_resize, container, false)
        val resize = layout.findViewById<MotionViewLinearLayout>(R.id.resize)
        val enterExit = layout.findViewById<Button>(R.id.enter_exit)
        val resizeXML = layout.findViewById<MotionViewLinearLayout>(R.id.resize_in_xml)

        val mh = resizeHeight()
        val mw = resizeWidth()
        val both = resizeBoth()

        enterExit.setOnClickListener {
            if (onEnter) {
                mh.enter()
                mw.enter()
                both.enter()
                resizeXML.enter()
            } else {
                mh.exit()
                mw.exit()
                both.exit()
                resizeXML.exit()
            }
            onEnter = !onEnter
        }
        resize.addView(mh.motionViewBase)
        resize.addView(mw.motionViewBase)
        resize.addView(both.motionViewBase)

        return layout
    }

    private fun resizeHeight(): MotionViewBase {
        val view = View(context)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                hEnter = 100f,
                hIn = 400f,
                hExit = 200f,
                wEnter = 100f,
                wIn = 100f,
                wExit = 100f,
            ),
        )
        return MotionViewBase(motionViewBase = view, motionValues = motionValues)
    }

    private fun resizeWidth(): MotionViewBase {
        val view = View(context)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                hEnter = 100f,
                hIn = 100f,
                hExit = 100f,
                wEnter = 100f,
                wIn = 400f,
                wExit = 200f,
            ),
        )
        return MotionViewBase(motionViewBase = view, motionValues = motionValues)
    }

    private fun resizeBoth(): MotionViewBase {
        val view = View(context)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                hEnter = 100f,
                hIn = 400f,
                hExit = 200f,
                wEnter = 100f,
                wIn = 400f,
                wExit = 200f,
            ),
        )
        return MotionViewBase(motionViewBase = view, motionValues = motionValues)
    }
}