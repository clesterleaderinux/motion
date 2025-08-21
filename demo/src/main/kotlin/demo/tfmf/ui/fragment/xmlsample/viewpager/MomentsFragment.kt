package tfmf.ui.demo.fragment.xmlsample.viewpager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.microsoft.fluentmotion.ui.xml.fragments.MotionViewFragment
import com.microsoft.fluentmotion.ui.xml.interfaces.IMotionViewScrollListener
import com.microsoft.fluentxml.demo.R
import com.microsoft.fluentxml.demo.extensions.MotionViewFragmentExtensions.attachSearchScrollListener

class MomentsFragment : MotionViewFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_moments, container, false)
        context?.let {
            attachSearchScrollListener(
                it,
                rootView,
                R.id.scrollview,
                activity as IMotionViewScrollListener,
            )
        }

        return rootView
    }
}