package demo.tfmf.ui.fragment.xmlsample

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.annotation.RequiresApi
import demo.tfmf.ui.R
import tfmf.ui.motion.MotionDuration
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.TranslationX
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout

class DurationsFragment : MotionViewFragment() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val screenWidth = activity?.windowManager?.currentWindowMetrics?.bounds?.width()
        val endPosition: Float? = context?.let { screenWidth?.minus(100)?.toFloat() }
        val layout = inflater.inflate(R.layout.fragment_durations, container, false)
        val durations = layout.findViewById<MotionViewLinearLayout>(R.id.durations)
        for (duration in MotionDuration.values()) {
            var onEnter = true
            val view = View(context)
            view.layoutParams = LayoutParams(400, 400)
            view.setBackgroundColor(Color.BLUE)
            val motionValues = hashMapOf(
                MotionTypeKey.TranslationX.name to TranslationX(
                    xEnter = 10f,
                    xIn = endPosition ?: 0f,
                    xExit = 10f,
                ),
            )
            val mvb = MotionViewBase(motionViewBase = view, motionValues = motionValues, duration = duration)
            view.setOnClickListener {
                if (onEnter) {
                    mvb.enter()
                } else {
                    mvb.exit()
                }
                onEnter = !onEnter
            }
            val description = TextView(context)
            context?.let {
                description.setTextColor(Color.BLUE)
            }
            description.text = duration.name
            durations.addView(description)
            durations.addView(mvb.motionViewBase)
        }
        return layout
    }
}