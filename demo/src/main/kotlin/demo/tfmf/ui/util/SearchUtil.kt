package demo.tfmf.ui.util

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ViewTreeObserver
import com.microsoft.fluentmotion.ui.xml.interfaces.IMotionViewScrollListener
import com.microsoft.fluentxml.demo.activity.SearchNestedScrollView

object SearchUtil {

    private const val minimumScrollThreshold = 20
    fun attachSearchScrollListener(
        context: Context,
        nestedScrollView: SearchNestedScrollView,
        scrollListener: IMotionViewScrollListener
    ) {
        val gestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    if (distanceY > minimumScrollThreshold) {
                        scrollListener.onScrolledUp(true)
                    } else if (distanceY < -minimumScrollThreshold) {
                        scrollListener.onScrolledUp(false)
                    }
                    return super.onScroll(e1, e2, distanceX, distanceY)
                }
            },
        )

        nestedScrollView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }

        nestedScrollView.viewTreeObserver.addOnScrollChangedListener(
            object : ViewTreeObserver.OnScrollChangedListener {
                var oldScrollY = nestedScrollView.scrollY

                override fun onScrollChanged() {
                    val newScrollY = nestedScrollView.scrollY

                    if (newScrollY == oldScrollY) {
                        scrollListener.onSettled()
                    }

                    oldScrollY = newScrollY
                }
            },
        )
    }
}
