package tfmf.ui.demo.extensions

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.microsoft.fluentmotion.ui.xml.fragments.MotionViewFragment
import com.microsoft.fluentmotion.ui.xml.interfaces.IMotionViewScrollListener
import com.microsoft.fluentxml.demo.activity.SearchNestedScrollView

object MotionViewFragmentExtensions {

    fun MotionViewFragment.attachSearchScrollListener(
        context: Context,
        rootView: View,
        nestedScrollViewId: Int,
        scrollListener: IMotionViewScrollListener
    ) {
        val nestedScrollView = rootView.findViewById<SearchNestedScrollView>(nestedScrollViewId)
        val minimumScrollThreshold = 10
        val minFlingVelocity = -500
        val maxFlingVelocity = 500
        val scrollFlingDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    scrollListener?.onScrolled()
                    if (distanceY > minimumScrollThreshold) {
                        scrollListener?.onScrolledUp(true)
                    } else if (distanceY < -minimumScrollThreshold) {
                        scrollListener?.onScrolledUp(false)
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    // Your code here
                    if(velocityY > maxFlingVelocity) {
                        scrollListener?.onScrolledUp(false)
                        Log.d("GestureDetector if", "Fling with velocityX = $velocityX, velocityY = $velocityY")
                    } else if(velocityY < minFlingVelocity) {
                        Log.d("GestureDetector esle ", "Fling with velocityX = $velocityX, velocityY = $velocityY")
                        scrollListener?.onScrolledUp(true)
                    }
                    return true
                }
            },

        )

        nestedScrollView.setOnTouchListener { _, event ->
            scrollFlingDetector.onTouchEvent(event)
            false
        }

        nestedScrollView.viewTreeObserver.addOnScrollChangedListener(
            object : ViewTreeObserver.OnScrollChangedListener {
                var oldScrollY = nestedScrollView.scrollY

                override fun onScrollChanged() {
                    val newScrollY = nestedScrollView.scrollY

                    if (newScrollY == oldScrollY) {
                        scrollListener?.onSettled()
                    }

                    oldScrollY = newScrollY
                }
            },
        )
    }
}