package com.microsoft.fluentmotion.ui.xml.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.microsoft.fluentmotion.R
import com.microsoft.fluentmotion.ui.MotionTypeKey
import com.microsoft.fluentmotion.ui.Resize
import com.microsoft.fluentmotion.ui.TranslationX
import com.microsoft.fluentmotion.ui.accessibility.IAccessibleMotionView
import com.microsoft.fluentmotion.ui.actions.CancellationError
import com.microsoft.fluentmotion.ui.telemetry.ITelemetryLoggable
import com.microsoft.fluentmotion.ui.telemetry.TelemetryEvent
import com.microsoft.fluentmotion.ui.telemetry.TelemetryLogger
import com.microsoft.fluentmotion.ui.xml.base.MotionViewBase
import com.microsoft.fluentmotion.ui.xml.interfaces.ISliderView
import com.microsoft.fluentmotion.ui.xml.interfaces.SliderElementListener
import com.microsoft.fluentmotion.ui.xml.interfaces.SliderFragmentSwitchListener
import com.microsoft.fluentmotion.ui.xml.layouts.MotionViewLinearLayout


/**
 * Main slider component for a horizontal slider containing custom views.
 * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
 * @param attrs The attributes of the XML tag that is inflating the view.
 */
class MotionViewAnimatedHorizontalSlider(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) :
    MotionViewLinearLayout(context, attrs, defStyleAttr, defStyleRes),
    SliderElementListener,
    IAccessibleMotionView,
    ITelemetryLoggable,
    SliderFragmentSwitchListener {

    private val slider = View(context, attrs)
    private val container = this
    private val sliderRecyclerView = RecyclerView(context)
    private var currentXPosition = 0f
    private val sliderHeight = 14
    private var currentSliderWidth = 0
    private var anchorId = 1
    var interceptAnimation = false
    var sliderFragmentSwitchListener: SliderFragmentSwitchListener? = null

    // accessibility text
    override var onEnterText: String? = null
    override var onInText: String? = null
    override var onExitText: String? = null

    // cancel animation
    override var onCancelAction: ((CancellationError) -> Unit)? = null

    init {
        orientation = VERTICAL
        layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        sliderRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addView(sliderRecyclerView)

        slider.layoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, sliderHeight)
        slider.setBackgroundColor(ContextCompat.getColor(context, R.color.brand))
        addView(slider)

        sliderRecyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    slider.x = slider.x - dx
                }
            },
        )
        // listens for when the view is completely layed out and the slider width/position can be set
        val observer = container.viewTreeObserver
        observer.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    post {
                        val sliderView =
                            (sliderRecyclerView.adapter as HorizontalAdapter).getItemAt(0)
                        setInitialSliderSizeAndPosition(
                            sliderView.sliderWidth(),
                            sliderView.sliderAnchorPosition().toFloat(),
                        )
                        if (container.viewTreeObserver.isAlive) {
                            container.viewTreeObserver.removeOnGlobalLayoutListener(this)
                        }
                    }
                }
            },
        )
        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.MotionViewAnimatedHorizontalSlider, 0, 0)
            onEnterText = a.getString(
                R.styleable.MotionViewAnimatedHorizontalSlider_on_enter_text,
            )
            onInText = a.getString(
                R.styleable.MotionViewAnimatedHorizontalSlider_on_in_text,
            )
            onExitText = a.getString(
                R.styleable.MotionViewAnimatedHorizontalSlider_on_exit_text,
            )
            a.recycle()
        }
    }

    /**
     * Function to append slider views into the HorizontalAdapter.
     * @param views The slider views to be appended.
     */
    fun appendSliderViews(vararg views: ISliderView) {
        sliderRecyclerView.adapter = HorizontalAdapter(views.asList(), this, this)
    }

    /**
     * Sets the initial size and position for the slider.
     * @param initialWidth The initial width of the slider.
     * @param x The initial x position of the slider.
     */
    fun setInitialSliderSizeAndPosition(initialWidth: Int, x: Float) {
        currentSliderWidth = initialWidth
        slider.layoutParams = LayoutParams(initialWidth, sliderHeight)
        slider.post {
            slider.x = x
            currentXPosition = x
        }
    }

    /**
     * Handle clicks on individual slider elements.
     * @param sliderId The ID of the clicked slider.
     * @param sliderWidth The width of the clicked slider.
     * @param smoothScrollTo The position to smoothly scroll to.
     * @param sliderAnchorPosition The anchor position of the slider.
     * @param withSmoothScroll A boolean indicating whether to use smooth scrolling.
     */
    override fun onSliderElementClicked(
        sliderId: Int,
        sliderWidth: Int,
        smoothScrollTo: Float,
        sliderAnchorPosition: Float,
        withSmoothScroll: Boolean
    ) {
        // Set the anchor ID to the ID of the clicked slider.
        anchorId = sliderId
        // Calculate X-coordinate to which slider should be moved.
        val targetX = sliderAnchorPosition - smoothScrollTo
        // Setup motion values for width and translation animations.
        val motionValues = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                wEnter = currentSliderWidth.toFloat(),
                wIn = sliderWidth.toFloat(),
                wExit = sliderWidth.toFloat(),
                hEnter = 6f,
                hIn = 6f,
                hExit = 6f,
            ),
            MotionTypeKey.TranslationX.name to TranslationX(
                xEnter = currentXPosition,
                xIn = targetX,
                xExit = targetX,
            ),
        )
        // Initiate animation transition for resizing and moving the slider.
        MotionViewBase(
            motionViewBase = slider,
            motionValues = motionValues,
            onEndAction = onEndAction,
        ).enter()
        // Optionally perform a smooth scroll in the RecyclerView to the selected slider element.
        if (withSmoothScroll) {
            sliderRecyclerView.smoothScrollToPosition(sliderId)
        }
        // Update the current position and width to the new state after the animation.
        currentXPosition = targetX
        currentSliderWidth = sliderWidth
    }

    /**
     * Synchronizes the ViewPager position with the horizontal slider position.
     * @param position The new position to switch to.
     */
    override fun switchViewPagerAndSliderPosition(position: Int) {
        // Ignore call if animations are currently being intercepted.
        if (interceptAnimation) {
            return
        }
        // Scroll the RecyclerView to the new position without animation.
        sliderRecyclerView.layoutManager?.scrollToPosition(position)
        // Trigger the slider element click event handler programmatically after the RecyclerView updates.
        sliderRecyclerView.post {
            val viewHolder =
                sliderRecyclerView.findViewHolderForAdapterPosition(position) as? HorizontalAdapter.ViewHolder
            val sliderView = (sliderRecyclerView.adapter as HorizontalAdapter).getItemAt(position)
            this.onSliderElementClicked(
                position,
                sliderView.sliderWidth(),
                slideItemIntoView(position, viewHolder),
                sliderView.sliderAnchorPosition().toFloat(),
                false,
            )
        }
    }

    /**
     * Calculates how much of an item needs to be scrolled into view based on current visibility.
     * @param position The position of the item.
     * @param viewHolder The ViewHolder of the item.
     * @return The amount to scroll to bring the item into view.
     */
    fun slideItemIntoView(position: Int, viewHolder: HorizontalAdapter.ViewHolder?): Float {
        val layoutManager = sliderRecyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        // If position is left of the first completely visible item, we calculate sliding left.
        if (position < firstVisiblePosition) {
            val percentageVisible: Float = viewHolder?.itemView?.let { itemView ->
                val itemWidth = itemView.width
                val visibleWidth =
                    Math.min(itemView.right, sliderRecyclerView.width) + Math.max(itemView.left, 0)
                (visibleWidth.toFloat() / itemWidth.toFloat()) * 100
            } ?: 0f
            // Negative value for scrolling to the left.
            return -formatFraction(percentageVisible, viewHolder?.itemView?.width ?: 0)
        } else { // Else, calculate sliding right.
            val percentageVisible: Float = viewHolder?.itemView?.let { itemView ->
                val itemWidth = itemView.width
                val visibleWidth =
                    Math.min(itemView.right, sliderRecyclerView.width) - Math.max(itemView.left, 0)
                (visibleWidth.toFloat() / itemWidth.toFloat()) * 100
            } ?: 0f
            // Positive value for scrolling to the right.
            return formatFraction(percentageVisible, viewHolder?.itemView?.width ?: 0)
        }
    }

    /**
     * Formats a percentage into a fractional value relative to the width.
     * @param percentageVisible The percentage of the item that is visible.
     * @param width The width of the item.
     * @return The fractional value relative to the width.
     */
    private fun formatFraction(percentageVisible: Float, width: Int? = 0): Float {
        // Use 100% as a base to convert percentage to a fraction of the width.
        return ((100f - percentageVisible).div(100)).times(width as Int)
    }

    /**
     * Adapter for binding slider views within a RecyclerView.
     * @param ISliderViews The list of slider views.
     * @param sliderElementListener The listener for slider element click events.
     * @param animatedSlider The animated slider.
     */
    class HorizontalAdapter(
        private val ISliderViews: List<ISliderView>,
        private val sliderElementListener: SliderElementListener,
        private val animatedSlider: MotionViewAnimatedHorizontalSlider
    ) : RecyclerView.Adapter<HorizontalAdapter.ViewHolder>() {

        // ViewHolder pattern to improve performance and for smoother scrolling.
        class ViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {
            var container = viewGroup
        }

        // Returns the total number of slider views.
        override fun getItemCount() = ISliderViews.size

        // Retrieves the SliderView at a specific position.
        fun getItemAt(position: Int) = ISliderViews[position]

        // Creates ViewHolder that holds the view for each slider element.
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = MotionViewLinearLayout(context = parent.context)
            return ViewHolder(view)
        }

        // Binds each slider view to the ViewHolder and sets up click and touch interactions.
        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val sliderView = ISliderViews[position]
            // Add the actual view of the slider element to the holder container.
            holder.container.addView(sliderView.getView())
            // Detect touch events on the slider element.
            holder.container.setOnTouchListener { _, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Intercept and pause other animations while interacting with this slider element.
                        animatedSlider.interceptAnimation = true
                    }

                    MotionEvent.ACTION_UP -> {
                        // Perform the action associated with this slider element.
                        sliderView.performAction()
                        // Notify the listener that a slider element has been clicked.
                        sliderElementListener.onSliderElementClicked(
                            position,
                            sliderView.sliderWidth(),
                            animatedSlider.slideItemIntoView(position, holder),
                            sliderView.sliderAnchorPosition().toFloat(),
                            true,
                        )
                    }
                }
                true
            }
        }
    }

    /**
     * Log telemetry for animation changes
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TelemetryLogger.logTelemetryForAction(event, log)
    }
}
