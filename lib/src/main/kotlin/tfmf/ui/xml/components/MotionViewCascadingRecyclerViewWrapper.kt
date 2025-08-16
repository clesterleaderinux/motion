package com.microsoft.fluentmotion.ui.xml.components

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * This class is a wrapper for a RecyclerView that provides cascading motion view functionality.
 *
 * @property recyclerView The RecyclerView to be wrapped.
 */
class MotionViewCascadingRecyclerViewWrapper(val recyclerView: RecyclerView?) {

    /**
     * A layout that provides cascading motion view functionality.
     */
    private val cascadingLayout: MotionViewCascadingLayout = MotionViewCascadingLayout()

    /**
     * A map that stores the views in each row of the RecyclerView.
     * The key is the row number and the value is a list of views in that row.
     */
    private val motionViewMap: MutableMap<Int, MutableList<View>> = HashMap()

    /**
     * This method is called when the RecyclerView enters a state.
     * It calculates the rows and columns of the RecyclerView and adds the views to a map to animate
     * as part of the segments of a cascading view.
     */
    fun onEnter(chainKey: String) {
        val layoutManager = recyclerView?.layoutManager
        if (layoutManager !is GridLayoutManager) {
            throw IllegalArgumentException("Layout Manager not supported. Expected GridLayoutManager.")
        }

        val columns = layoutManager.spanCount
        val range = layoutManager.findFirstVisibleItemPosition()..layoutManager.findLastVisibleItemPosition()

        var currentRow = 1
        var list = mutableListOf<View>()
        for (position in range) {
            val row = position / columns + 1
            val view = recyclerView?.findViewHolderForAdapterPosition(position)?.itemView
            if (row != currentRow) {
                motionViewMap[currentRow] = list
                currentRow = row
            }
            view?.let {
                list.add(it)
            }
        }
        cascadingLayout.appendCascadingViewGroup(chainKey, motionViewMap)
        cascadingLayout.onEnterForCascadingGroup()
    }

    /**
     * This method is called to reset the animation state for testing.
     */
    fun reset() {
        val layoutManager = recyclerView?.layoutManager as GridLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

        for (position in firstVisiblePosition..lastVisiblePosition) {
            val viewHolder = recyclerView?.findViewHolderForAdapterPosition(position)
            val view = viewHolder?.itemView
            view?.let {
                it.animate().scaleY(1f).scaleX(1f).alpha(0f).setDuration(0).start()
            }
        }
    }
}