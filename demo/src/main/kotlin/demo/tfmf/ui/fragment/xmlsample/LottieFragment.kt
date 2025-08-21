package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import demo.tfmf.ui.ComponentUtil
import demo.tfmf.ui.R
import tfmf.ui.motion.xml.base.ThemeableAnimationView
import tfmf.ui.motion.xml.fragments.MotionViewFragment

class LottieFragment : MotionViewFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_lottie, container, false)
        val themeSwitchButton = layout.findViewById<Button>(R.id.theme_switch_button)
        val themeableLottieAnimationView = layout.findViewById<ThemeableAnimationView>(R.id.animation)
        themeSwitchButton.setOnClickListener {
            themeableLottieAnimationView.applyTheme(
                ComponentUtil.getRandomColor(),
                ComponentUtil.getRandomColor(),
                ComponentUtil.getRandomColor(),
                ComponentUtil.getRandomColor(),
            )
        }
        return layout
    }
}