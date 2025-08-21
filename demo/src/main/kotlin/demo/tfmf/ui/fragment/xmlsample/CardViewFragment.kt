package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import demo.tfmf.ui.R
import tfmf.ui.motion.xml.fragments.MotionViewFragment
import tfmf.ui.motion.xml.layouts.MotionViewCardViewLayout

class CardViewFragment : MotionViewFragment() {

    private var onEnter = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_card_view, container, false)
        val radiusXML = layout.findViewById<MotionViewCardViewLayout>(R.id.card_view_in_xml)
        val enterExit = layout.findViewById<AppCompatButton>(R.id.enter_exit)
        enterExit.setOnClickListener {
            if (onEnter) {
                radiusXML.enter()
            } else {
                radiusXML.exit()
            }
            onEnter = !onEnter
        }

        return layout
    }
}