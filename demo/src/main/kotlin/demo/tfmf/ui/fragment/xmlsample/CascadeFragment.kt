package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import demo.tfmf.ui.R
import tfmf.ui.motion.xml.components.MotionViewCascadingLayout
import tfmf.ui.motion.xml.fragments.MotionViewFragment

class CascadeFragment : MotionViewFragment() {

    private var onEnterExit = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_cascade, container, false)
        val cascade1 = layout.findViewById<View>(R.id.cascade1)
        val cascade2 = layout.findViewById<View>(R.id.cascade2)
        val cascade3 = layout.findViewById<View>(R.id.cascade3)
        val cascade4 = layout.findViewById<View>(R.id.cascade4)
        val cascade5 = layout.findViewById<View>(R.id.cascade5)

        val cascadingViewLayout = MotionViewCascadingLayout()
        cascadingViewLayout.appendCascadingViews(
            "Demo Vertical Cascading Views",
            onEndAction(),
            null,
            listOf(cascade1,cascade2,cascade3,cascade4,cascade5)
        )

        val enterExitButton = layout.findViewById<Button>(R.id.enter_exit)
        enterExitButton.setOnClickListener {
            if (onEnterExit) {
                cascadingViewLayout.onEnter()
            } else {
                cascadingViewLayout.onExit()
            }
            onEnterExit = !onEnterExit
        }

        return layout
    }

    private fun onEndAction(): (() -> Unit)? {
        Toast.makeText(context, "Cascade completed", Toast.LENGTH_SHORT).show()
        return null
    }
}