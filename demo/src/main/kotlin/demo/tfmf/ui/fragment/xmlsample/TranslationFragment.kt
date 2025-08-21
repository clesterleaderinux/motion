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
import tfmf.ui.motion.TranslationX
import tfmf.ui.motion.TranslationY
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class TranslationFragment : MotionViewFragment() {

    private var onEnter = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_translation, container, false)
        val translation = layout.findViewById<MotionViewLinearLayout>(R.id.translation)
        val enterExit = layout.findViewById<Button>(R.id.enter_exit)
        val translationXML = layout.findViewById<MotionViewLinearLayout>(R.id.translation_in_xml)

        val mx = moveXView()
        val my = moveYView()
        val both = moveBothViews()

        enterExit.setOnClickListener {
            if (onEnter) {
                mx.enter()
                my.enter()
                both.enter()
                translationXML.enter()
            } else {
                mx.exit()
                my.exit()
                both.exit()
                translationXML.exit()
            }
            onEnter = !onEnter
        }
        translation.addView(mx.motionViewBase)
        translation.addView(my.motionViewBase)
        translation.addView(both.motionViewBase)

        return layout
    }

    private fun moveXView(): MotionViewBase {
        val view = View(context)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.TranslationX.name to TranslationX(xEnter = -100f, xIn = 200f, xExit = -100f),
        )
        return MotionViewBase(motionViewBase = view, motionValues = motionValues)
    }

    private fun moveYView(): MotionViewBase {
        val view = View(context)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.TranslationY.name to TranslationY(yEnter = -100f, yIn = 200f, yExit = -100f),
        )
        return MotionViewBase(motionViewBase = view, motionValues = motionValues)
    }

    private fun moveBothViews(): MotionViewBase {
        val view = View(context)
        view.layoutParams = LayoutParams(400, 400)
        view.setBackgroundColor(ComponentUtil.getRandomColor())
        val motionValues = hashMapOf(
            MotionTypeKey.TranslationY.name to TranslationY(yEnter = -100f, yIn = 400f, yExit = -100f),
            MotionTypeKey.TranslationX.name to TranslationX(xEnter = 100f, xIn = 400f, xExit = 100f),
        )
        return MotionViewBase(motionViewBase = view, motionValues = motionValues)
    }
}