package tfmf.mobile.ui.xml.components

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.google.android.material.tabs.TabLayout
import com.microsoft.fluentmotion.R
import tfmf.mobile.ui.IndicatorOffset
import tfmf.mobile.ui.IndicatorWidth
import tfmf.mobile.ui.MotionDuration
import tfmf.mobile.ui.MotionScaleFactor
import tfmf.mobile.ui.MotionTypeKey
import tfmf.mobile.ui.actions.CancellationError
import tfmf.mobile.ui.actions.ICancellable
import tfmf.mobile.ui.telemetry.ITelemetryLoggable
import tfmf.mobile.ui.telemetry.TelemetryEvent
import tfmf.mobile.ui.telemetry.TelemetryLogger
import tfmf.mobile.ui.util.MotionUtil
import tfmf.mobile.ui.xml.base.MotionViewBase
import tfmf.mobile.ui.xml.interfaces.ISliderView
import kotlin.math.ceil

/**
 * A custom [TypeEvaluator] for [RectF] that interpolates the start and end [RectF] values based on the animation fraction.
 *
 * This evaluator is used to animate properties of a [RectF] object, allowing for smooth transitions between states in animations.
 * It calculates the interpolated values for the left, top, right, and bottom coordinates of the rectangle.
 *
 * @property fraction The fraction of the animation duration that has passed, ranging from 0 to 1.
 * @property startValue The starting [RectF] value at the beginning of the animation.
 * @property endValue The ending [RectF] value at the end of the animation.
 * @return [RectF] The interpolated [RectF] value for the current animation fraction.
 */
private class RectFEvaluator : TypeEvaluator<RectF> {
    override fun evaluate(fraction: Float, startValue: RectF, endValue: RectF): RectF {
        return RectF(
            startValue.left + (endValue.left - startValue.left) * fraction,
            startValue.top + (endValue.top - startValue.top) * fraction,
            startValue.right + (endValue.right - startValue.right) * fraction,
            startValue.bottom + (endValue.bottom - startValue.bottom) * fraction,
        )
    }
}
/**
 * Custom TabLayout with animated indicator with telemetry and cancellation support.
 *
 * @param context The context.
 * @param attrs The attributes set in the layout.
 * @param defStyleAttr The style applied to the control.
 */
class MotionViewTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TabLayout(context, attrs, defStyleAttr),
    ITelemetryLoggable,
    ICancellable {

    private var indicatorDisabledColor = context.getColor(R.color.bright_green)
    private var shouldClearIndicator = false
    private var defaultIndicatorColor = context.getColor(R.color.bright_green)
    private var indicatorGradientStartColor = context.getColor(R.color.bright_green)
    private var indicatorGradientEndColor = context.getColor(R.color.brand)
    private var backgroundColor = context.getColor(R.color.white)

    private var indicatorLeft = 0f
    private var indicatorWidth = 0f
    private var indicatorTop = 0f
    private var indicatorBottom = 0f
    private var currentTabPosition = 0
    private var indicatorStrokeThickness = 0f
    private var indicatorStartMargin = 0f

    private var indicatorHeight = resources.getDimensionPixelSize(R.dimen.tabLayoutIndicatorHeightInDp)
    override var onCancelAction: ((CancellationError) -> Unit)? = null

    private var indicatorAnimator: ValueAnimator? = null
    private val indicatorRect = RectF()
    private val cornerRadius = 10f

    init {

        context.obtainStyledAttributes(attrs, R.styleable.MotionViewTabLayout, 0, 0).apply {
            defaultIndicatorColor = getColor(
                R.styleable.MotionViewTabLayout_indicator_color,
                context.getColor(R.color.bright_green),
            )
            indicatorDisabledColor = getColor(
                R.styleable.MotionViewTabLayout_indicator_disabled_color,
                context.getColor(R.color.bright_green),
            )
            indicatorStrokeThickness =
                getDimension(R.styleable.MotionViewTabLayout_indicator_stroke_thickness, 16f)
            indicatorStartMargin =
                getDimension(R.styleable.MotionViewTabLayout_indicator_start_margin, 0f)
            backgroundColor =
                getColor(
                    R.styleable.MotionViewTabLayout_background_color,
                    context.getColor(R.color.white),
                )
            indicatorGradientStartColor = getColor(
                R.styleable.MotionViewTabLayout_indicator_gradient_start_color,
                context.getColor(R.color.clear),
            )
            indicatorGradientEndColor = getColor(
                R.styleable.MotionViewTabLayout_indicator_gradient_end_color,
                context.getColor(R.color.brand),
            )
            recycle()
        }

        setSelectedTabIndicatorColor(Color.TRANSPARENT)
        tabMode = MODE_SCROLLABLE
        background = ColorDrawable(backgroundColor)

        addOnTabSelectedListener(
            object : OnTabSelectedListener {
                override fun onTabSelected(tab: Tab) {
                    animateIndicatorToTab(tab?.position ?: 0, true, null)
                    setCustomViewActiveState(tab)
                }
                override fun onTabUnselected(tab: Tab) {}
                override fun onTabReselected(tab: Tab) {}
            },
        )
    }

    /**
     * Logs telemetry for an action.
     *
     * @param event The telemetry event.
     * @param log The log message.
     * */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TelemetryLogger.logTelemetryForAction(TelemetryEvent.PivotTabSwitch, "log text")
    }

    /**
     * Overrides the default onMeasure behavior to adjust the height of the TabLayout based on the height of a LottieAnimationButton.
     *
     * This method ensures that the TabLayout's height is sufficient to display the LottieAnimationButton without cutting off any text.
     * By default, if the height of the TabLayout is set to WRAP_CONTENT and the measure spec mode is UNSPECIFIED,
     * the TabLayout might default to a height of 48dp, potentially obscuring content. This method calculates the measured height
     * of the first tab's LottieAnimationButton, if present, and sets the TabLayout's height explicitly to match.
     *
     * The method also adjusts the tab mode and gravity based on the combined width of the tabs.
     * To match Figma spec, the tabs should fit within the screen width if they can. Tabs in tablet/landscape mode
     * should be centered, while tabs on smaller screens should fill the screen.
     *
     * @param widthMeasureSpec Horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec Vertical space requirements as imposed by the parent. This is modified to match the height of the LottieAnimationButton.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Set the height of the tab layout to the measured height of the LottieAnimationButton to ensure tab layout
        // will not cut off text.
        // Otherwise, TabLayout implementation will set the height of the tab layout to the default height (48dp) if the
        // height is set to WRAP_CONTENT/measure spec mode is set to UNSPECIFIED.
        val measuredHeight = (getTabAt(0)?.customView as? LottieAnimationButton)?.getView()?.measuredHeight ?: 0
        val exactHeightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, exactHeightMeasureSpec)

        val tabs = getChildAt(0) as ViewGroup
        var tabsCombinedWidth = 0
        tabs.forEach { tab ->
            (tab as ViewGroup).forEach { view ->
                if (view is LottieAnimationButton) {
                    tabsCombinedWidth += view.getTabMinWidth()
                }
            }
        }

        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (tabsCombinedWidth <= measuredWidth && measuredWidth != 0 && tabsCombinedWidth != 0) {
            if (MotionUtil.isTablet(context) ||
                    resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ||
                    tabs.childCount <= MAX_TAB_COUNT_FOR_CENTERED_GRAVITY) {
                setTabModeAndGravityIfNeeded(MODE_FIXED, GRAVITY_CENTER)
            } else {
                setTabModeAndGravityIfNeeded(MODE_FIXED, GRAVITY_FILL)
            }
        } else {
            setTabModeAndGravityIfNeeded(MODE_SCROLLABLE, GRAVITY_CENTER)
        }
    }

    /**
     * Called when this view is attached to a window.
     * This method is responsible for setting the background of the parent view to a transparent drawable,
     * effectively disabling the selection highlight.
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(
            intArrayOf(),
            ContextCompat.getDrawable(context, R.drawable.transparent_background),
        )
        val parent = parent as ViewGroup
        parent.background = stateListDrawable
    }

    /**
     * Draws the indicator on the canvas.
     *
     * @param canvas The canvas to draw on.
     */
    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        indicatorBottom = ceil((height.toFloat() - indicatorHeight).toDouble()).toFloat()
        indicatorTop = indicatorBottom - indicatorHeight

        val paint = Paint().apply {
            if (shouldClearIndicator) {
                color = context.getColor(R.color.clear)
                shouldClearIndicator = false
            } else if (isEnabled) {
                shader = LinearGradient(
                    indicatorLeft,
                    indicatorTop,
                    indicatorLeft + indicatorWidth,
                    height.toFloat(),
                    indicatorGradientStartColor,
                    indicatorGradientEndColor,
                    Shader.TileMode.CLAMP
                )
            } else {
                color = indicatorDisabledColor
            }

            style = Paint.Style.FILL
        }

        val rect = RectF(
            indicatorLeft,
            indicatorTop,
            indicatorLeft + indicatorWidth,
            indicatorBottom,
        )

        // Draw the rounded rectangle
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        // When enabled state changes, we need to redraw the indicator with the new fill color
        invalidate()
    }

    /**
     * Resets the start properties for the cascade animation.
     * This includes resetting the alpha and scale properties of the view.
     */
    fun resetCascadeAnimationStartProperties() {
        alpha = 0f
        scaleX = MotionScaleFactor.CascadeMedium.scaleFactor
        scaleY = MotionScaleFactor.CascadeMedium.scaleFactor
    }

    /**
     * Clears the indicator color for a reset.
     * This function posts a runnable to the message queue of the view to clear the indicator color.
     */
    fun clearIndicatorColorForReset() {
        shouldClearIndicator = true
        invalidate()
    }

    /**
     * Resets the selected position of the tab.
     * This function posts a runnable to the message queue of the view to reset the selected position.
     */
    fun resetSelectedPosition() {
        getTabAt(selectedTabPosition)?.let {
            postDelayed(
                {
                    animatedIndicatorToTab(it, false, null)
                    invalidate()
                },
                MotionDuration.ResetSelectedPivotDuration.speedInMillis,
            )
        }
    }

    /**
     * Animates the indicator to the specified tab and sets the active state of the custom view.
     *
     * @param tab The tab to which the indicator should be animated.
     * @param withAnimation Optional parameter to control whether the animation should be performed. Default is true.
     * @param onEndAction Optional end action to run when indicator slide has ended.
     */
    fun animatedIndicatorToTab(tab: Tab, withAnimation: Boolean = true, onEndAction: (() -> Unit)?) {
        animateIndicatorToTab(tab.position, withAnimation, onEndAction)
        setCustomViewActiveState(tab)
        invalidate()
    }

    /**
     * Sets the active state for a custom view associated with a tab.
     *
     * @param tab The tab whose custom view should be activated.
     */
    private fun setCustomViewActiveState(tab: Tab) {
        val newISliderView = (tab?.customView as ISliderView?)
        val currentISliderView = (getTabAt(currentTabPosition)?.customView as ISliderView?)
        if (currentTabPosition != tab.position) {
            newISliderView?.performAction()
            newISliderView?.setActive()
            currentISliderView?.setInactive()
            currentTabPosition = tab.position
        }
        invalidate()
    }

    /**
     * Adds SliderViews to the TabLayout.
     *
     * @param ISliderViews The SliderViews to add.
     */
    fun appendSliderViews(vararg iSliderViews: ISliderView) {
        for (sliderView in iSliderViews) {
            val tab = newTab()
            tab.customView = sliderView.getView()
            addTab(tab)
        }
        post {
                animateIndicatorToTab(0, false, null)
        }
    }

    /**
     * Sets the left position of the indicator.
     *
     * @param indicatorLeft The new left position.
     */
    fun setIndicatorLeft(indicatorLeft: Float) {
        this.indicatorLeft = indicatorLeft
        invalidate()
    }

    /**
     * Sets the width of the indicator.
     *
     * @param indicatorWidth The new width.
     */
    fun setIndicatorWidth(indicatorWidth: Float) {
        this.indicatorWidth = indicatorWidth
        invalidate()
    }

    /**
     * Animates the indicator to a new tab position with optional animation.
     *
     * This function calculates the new position of the indicator based on the selected tab. It supports animated transitions
     * between tabs, allowing for a smooth visual shift of the indicator from one tab to another. The animation includes changes
     * in the indicator's width and offset to align with the newly selected tab.
     *
     * @param newPosition The position index of the new tab to which the indicator should move.
     * @param withAnimation A boolean flag indicating whether the transition should be animated. Defaults to true for animated transitions.
     */
    fun animateIndicatorToTab(newPosition: Int, withAnimation: Boolean = true, onEndAction: (() -> Unit)?) {
        val newTab = getTabAt(newPosition)
        if (newTab != null) {
            val tabView = newTab.customView ?: newTab.view
            val sliderView = tabView as? ISliderView ?: return
            val titleWidth = sliderView.sliderWidth()
            val parentView = tabView.parent as View
            val absoluteLeft = parentView.left + (parentView.width / 2f - titleWidth / 2)
            val motionValues = hashMapOf(
                MotionTypeKey.IndicatorWidth.name to IndicatorWidth(
                    iwEnter = indicatorWidth,
                    iwIn = titleWidth.toFloat(),
                    iwExit = titleWidth.toFloat(),
                ),
                MotionTypeKey.IndicatorOffset.name to IndicatorOffset(
                    ioEnter = indicatorLeft,
                    ioIn = absoluteLeft,
                    ioExit = absoluteLeft,
                ),
            )
            val motionDuration =
                if (withAnimation) MotionDuration.DurationShort03 else MotionDuration.ZeroDuration

            MotionViewBase(
                motionViewBase = this,
                motionValues = motionValues,
                duration = motionDuration,
                onEndAction = onEndAction,
            ).enter()
        }
    }

    /**
     * Sets the tab mode and gravity if the values are different from the current values.
     *
     * @param newTabMode The new tab mode.
     * @param newTabGravity The new tab gravity.
     */
    private fun setTabModeAndGravityIfNeeded(@Mode newTabMode: Int, @TabGravity newTabGravity: Int) {
        if (tabMode != newTabMode) {
            tabMode = newTabMode
        }

        if (tabGravity != newTabGravity) {
            tabGravity = newTabGravity
        }
    }
    
    companion object {
        private const val MAX_TAB_COUNT_FOR_CENTERED_GRAVITY = 3
    }
}