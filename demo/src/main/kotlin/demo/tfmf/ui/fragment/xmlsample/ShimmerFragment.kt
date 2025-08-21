package demo.tfmf.ui.fragment.xmlsample

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.facebook.shimmer.ShimmerFrameLayout
import demo.tfmf.ui.R
import demo.tfmf.ui.util.ShimmerBuilder
import tfmf.ui.motion.xml.fragments.MotionViewFragment

class ShimmerFragment : MotionViewFragment() {

    private var onEnter = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_shimmer, container, false)
        val shimmerContainer = layout.findViewById<ShimmerFrameLayout>(R.id.shimmer)
        val enterExit = layout.findViewById<AppCompatButton>(R.id.enter_exit)
        var shimmerBuilder = ShimmerBuilder(
            context = requireContext()
        )
        var shimmerView: View
        container?.context.let { context ->
            shimmerView =
                shimmerBuilder
                    .setElementColor()
                    .setCornerRadius(30f)
                    .addGrid(5, 3)
                    .build()
        }
        enterExit.setOnClickListener {
            if (onEnter) {
                shimmerBuilder.alpha = 1f
                shimmerBuilder.enter()
            } else {
                shimmerBuilder.alpha = 0f
                val shimmerContainer = layout.findViewById<FrameLayout>(R.id.animated_layer)
                shimmerContainer.alpha = 1f
                shimmerBuilder.exit()
            }
            onEnter = !onEnter
        }

        // Set LayoutParams and gravity
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER
        shimmerView.layoutParams = layoutParams

        shimmerContainer.addView(shimmerView)

        return layout
    }
}
