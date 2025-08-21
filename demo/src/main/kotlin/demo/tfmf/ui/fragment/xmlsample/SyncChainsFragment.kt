package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.LayoutParams
import demo.tfmf.ui.ComponentUtil
import demo.tfmf.ui.R
import tfmf.ui.motion.Alpha
import tfmf.ui.motion.MotionChain
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.Scale
import tfmf.ui.motion.TranslationX
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewLinearLayout
import tfmf.ui.motion.xml.player.MotionPlayer

class SyncChainsFragment : MotionViewFragment() {

    private var onEnter = true
    val chainKey = "SyncChainsExample"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layout = inflater.inflate(R.layout.fragment_sync_chains, container, false)
        val enterExit = layout.findViewById<Button>(R.id.enter_exit)
        (layout as ViewGroup).addView(buildChain(container = container))
        enterExit.setOnClickListener {
            if (onEnter) {
                MotionPlayer.playEnterChainForKey(chainKey)
            } else {
                MotionPlayer.playExitChainForKey(chainKey)
            }
            onEnter = !onEnter
        }

        return layout
    }

    private fun buildChain(container: ViewGroup?): MotionViewLinearLayout? {
        MotionPlayer.clearChainForKey(chainKey)
        val context = container?.context
        var motionViewLinearLayout: MotionViewLinearLayout? = null
        if (context != null) {
            motionViewLinearLayout = MotionViewLinearLayout(context)
            val viewGroupParams = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
            )
            viewGroupParams.gravity = Gravity.CENTER
            motionViewLinearLayout.layoutParams = viewGroupParams
            motionViewLinearLayout.orientation = HORIZONTAL

            val params = LayoutParams(
                300,
                300,
            )
            params.gravity = Gravity.CENTER

            val view1 = View(context)
            view1.layoutParams = params
            view1.setBackgroundColor(ComponentUtil.getRandomColor())

            val view2 = View(context)
            view2.layoutParams = params
            view2.setBackgroundColor(ComponentUtil.getRandomColor())

            val view3 = View(context)
            view3.layoutParams = params
            view3.setBackgroundColor(ComponentUtil.getRandomColor())

            val motionValues1 = hashMapOf(
                MotionTypeKey.Alpha.name to Alpha(aEnter = 0.1f, aIn = 1f, aExit = 0.1f),
            )
            val motionValues2 = hashMapOf(
                MotionTypeKey.TranslationX.name to TranslationX(xEnter = 0f, xIn = 200f, xExit = 0f),
            )
            val motionValues3 = hashMapOf(
                MotionTypeKey.Scale.name to Scale(sEnter = 0.1f, sIn = 2f, sExit = 0.1f),
            )
            val link1 = MotionViewBase(
                motionViewBase = view1,
                motionValues = motionValues1,
                chainIndex = 1,
                chainKey = chainKey,
            )
            val link2 = MotionViewBase(
                motionViewBase = view2,
                motionValues = motionValues2,
                chainIndex = 1,
                chainKey = chainKey,
            )
            val link3 = MotionViewBase(
                motionViewBase = view3,
                motionValues = motionValues3,
                chainIndex = 1,
                chainKey = chainKey,
            )

            motionViewLinearLayout.addView(link1.motionViewBase)
            motionViewLinearLayout.addView(link2.motionViewBase)
            motionViewLinearLayout.addView(link3.motionViewBase)

            val motionChainLinks = MotionChain(
                chainKey = chainKey,
                chainLinks = mutableListOf(link1.motionPlayer, link2.motionPlayer, link3.motionPlayer),
            )

            MotionPlayer.addMotionChain(chainKey, motionChainLinks)
        }
        return motionViewLinearLayout
    }
}