package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import demo.tfmf.ui.R
import tfmf.ui.motion.xml.fragments.MotionViewFragment

class MotionViewGroupFullScreenFragment : MotionViewFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_motion_view_group_full_screen, container, false)
    }
}
