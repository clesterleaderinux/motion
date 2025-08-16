package tfmf.mobile.ui.xml.components

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.model.KeyPath
import com.microsoft.fluentmotion.R
import tfmf.mobile.ui.accessibility.IAccessibleMotionView
import tfmf.mobile.ui.actions.CancellationError
import tfmf.mobile.ui.actions.ICancellable
import tfmf.mobile.ui.telemetry.ITelemetryLoggable
import tfmf.mobile.ui.telemetry.TelemetryEvent
import tfmf.mobile.ui.telemetry.TelemetryLogger
import tfmf.mobile.ui.util.MotionUtil
import tfmf.mobile.ui.xml.interfaces.ISliderView
import tfmf.mobile.ui.xml.layouts.MotionViewFrameLayout
import tfmf.mobile.ui.xml.layouts.MotionViewLinearLayout
import kotlin.math.max

// Lottie button that can be used as standalone or as part of a slider view
class LottieAnimationButton(
    context: Context,
    clickAction: () -> Unit,
    sliderId: Int = 0,
    attrs: AttributeSet? = null,
) : MotionViewFrameLayout(context, attrs),
    ISliderView,
    IAccessibleMotionView,
    ITelemetryLoggable,
    ICancellable {

    private val lottieAnimation: LottieAnimationView = LottieAnimationView(context)
    private val title: TextView = TextView(context)

    /**
     * This is the edge (w) to edge (h) dimension that the badge sits in
     */
    private var boundarySquareSize: Int = 16
    private val badgeView = ImageView(context)

    private var clickAction = clickAction
    private var sliderId = sliderId
    private var playActive = true //TODO(is this needed?)
    private lateinit var properties: LottieAnimationButtonProperties

    // accessibility text
    override var onEnterText: String? = null
    override var onInText: String? = null
    override var onExitText: String? = null

    // cancel action
    override var onCancelAction: ((CancellationError) -> Unit)? = null

    init {
        // Create the LinearLayout that will contain the Lottie animation and the TextView
        val linearLayout = MotionViewLinearLayout(context)
        linearLayout.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.gravity = Gravity.CENTER

        // Set layout params for the Lottie animation
        val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            MotionUtil.dpToPx(context, 24).toInt(),
            MotionUtil.dpToPx(context, 24).toInt(),
        )
        layoutParams.gravity = Gravity.CENTER
        layoutParams.topMargin = MotionUtil.dpToPx(context, 8).toInt()
        lottieAnimation.layoutParams = layoutParams

        // Set layout params for the TextView
        val titleLayoutParams = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        titleLayoutParams.gravity = Gravity.CENTER
        // Adding the height of the indicator to the bottom margin value because the height of the MotionViewTabLayout
        // is determined by the height of the LottieAnimationButton - otherwise the indicator will be cut off
        titleLayoutParams.bottomMargin = MotionUtil.dpToPx(context, 6).toInt() + resources.getDimensionPixelSize(R.dimen.tabLayoutIndicatorHeightInDp)
        titleLayoutParams.leftMargin = resources.getDimensionPixelSize(R.dimen.tabStartAndEndMarginInDp)
        titleLayoutParams.rightMargin = resources.getDimensionPixelSize(R.dimen.tabStartAndEndMarginInDp)
        title.layoutParams = titleLayoutParams
        title.setSingleLine()
        title.setTextColor(ContextCompat.getColor(context, R.color.black))

        badgeView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.notification_badge_od3))
        badgeView.visibility = GONE

        linearLayout.addView(lottieAnimation)
        linearLayout.addView(title)
        this.addView(linearLayout)
        this.addView(badgeView)

        if (attrs != null) {
            val a =
                context.obtainStyledAttributes(attrs, R.styleable.LottieAnimationButton, 0, 0)
            onEnterText = a.getString(
                R.styleable.LottieAnimationButton_on_enter_text,
            )
            onInText = a.getString(
                R.styleable.LottieAnimationButton_on_in_text,
            )
            onExitText = a.getString(
                R.styleable.LottieAnimationButton_on_exit_text,
            )
            a.recycle()
        }
    }

    /**
     * Sets the UI component to its active state which involves modifying the range
     * of the Lottie animation and starting the animation. The active state typically
     * implies that the component is currently selected or focused.
     */
    override fun setActive() {
        lottieAnimation.setMinAndMaxProgress(0f, 0.5f)
        lottieAnimation.playAnimation()
        title.setTextColor(properties.activeTextColor)
        resetLottieAnimationStrokeColor(isEnabled = true)
    }

    /**
     * Sets the UI component to its inactive state by adjusting the Lottie animation range
     * to play from the midpoint to the end, then starts the animation. Inactive state usually
     * indicates the component is not currently selected or focused.
     */
    override fun setInactive() {
        lottieAnimation.setMinAndMaxProgress(0.51f, 1f)
        lottieAnimation.playAnimation()
        title.setTextColor(properties.inactiveTextColor)
        resetLottieAnimationStrokeColor(isEnabled = true)
    }

    /**
     * Sets the UI component to its enabled state. Enabled state means that the button can be selected
     *
     * @param isActive A boolean indicating whether the slider is active (currently selected).
     */
    override fun setEnabled(isActive: Boolean) {
        resetLottieAnimationStrokeColor(isEnabled = true)
        val titleColor = if (isActive) properties.activeTextColor else properties.inactiveTextColor
        title.setTextColor(titleColor)
    }

    /**
     * Sets the UI component to its disabled state. Disabled state means that the button cannot be selected
     * and will be a no-op when clicked.
     */
    override fun setDisabled() {
        resetLottieAnimationStrokeColor(isEnabled = false)
        title.setTextColor(properties.disabledTextColor)
    }

    /**
     * Performs an action associated with the UI component. This method will trigger the announcement
     * of accessibility text and execute a predefined click action associated with this component.
     */
    override fun performAction() {
        lottieAnimation.announceForAccessibility(onEnterText)
        clickAction()
    }

    /**
     * Sets the properties of the UI component such as title text and animation resource.
     *
     * @param animationProperties The properties relating to the icon and text appearance of the Lottie animation button.
     * @param iconMarginWidth The margin width from end edge to the Lottie animation for the badge to sit.
     * @param iconMarginHeight The margin height from the top edge of the Lottie animation for the badge to sit.
     */
    fun setProperties(
        animationProperties: LottieAnimationButtonProperties,
        iconMarginWidth: Int = 0, // (TODO: remove default argument after all calls are updated: https://onedrive.visualstudio.com/SkyDrive/_workitems/edit/2032457)
        iconMarginHeight: Int = 0 // top margin provided by Design
    ) {
        properties = animationProperties.copy()
        title.text = animationProperties.titleText
        lottieAnimation.visibility = View.VISIBLE
        lottieAnimation.setAnimation(animationProperties.animationId)

        // Since each animation has a different size, we need to set the badge margin width to appear correctly
        prepareLottieAnimationBadge(iconMarginWidth, iconMarginHeight)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Set the measure spec mode of LottieAnimationButton to UNSPECIFIED, otherwise, the mode will be EXACTLY
        // or AT_MOST, which will cause the text and icon to be cut off
        val unspecifiedHeightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.UNSPECIFIED)
        val unspecifiedWidthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.UNSPECIFIED)
        super.onMeasure(unspecifiedWidthMeasureSpec, unspecifiedHeightMeasureSpec)
    }

    /**
     * The current button to add to the slider
     * @return the current view
     */
    override fun getView() = this

    /**
     * Calculates the x position of the slider in the master view.
     *
     * This function takes into account the device's rotation, whether it's a tablet or not,
     * and the system window insets to accurately position the slider.
     *
     * @return The x position of the slider in the master view.
     **/
    override fun sliderAnchorPosition() = MotionUtil.xPositionOfView(title)

    /**
     * The width of the text
     * @return the width of the slider is determined by the text width
     */
    override fun sliderWidth(): Int {
        return title.width
    }

    override fun getTabMinWidth(): Int {
        return max(title.width, lottieAnimation.width) + 2 * resources.getDimensionPixelSize(R.dimen.tabStartAndEndMarginInDp)
    }

    /**
     * Sets how much padding to add on each side of the lottie buttons
     * @return the padding for the slider
     */
    override fun sliderPadding() = MotionUtil.dpToPx(context, 16).toInt()

    /**
     * The sliderid of the view to be used with a viewpager
     * @return the sliderid for this view
     */
    override fun getSliderId() = sliderId

    /**
     * Log telemetry for animation changes
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TelemetryLogger.logTelemetryForAction(event, log)
    }

    /**
     * Shows the badge on the Lottie animation.
     */
    fun showLottieAnimationBadge() {
        badgeView.visibility = VISIBLE
    }

    /**
     * Hides the badge on the Lottie animation.
     */
    fun hideLottieAnimationBadge() {
        badgeView.visibility = GONE
    }

    /**
     * Prepares the badge on the Lottie animation.
     *
     * @param badgeMarginWidth The margin width from end edge to the Lottie animation for the badge to sit.
     * @param badgeMarginHeight The margin height from the top edge of the Lottie animation for the badge to sit.
     */
    private fun prepareLottieAnimationBadge(badgeMarginWidth: Int, badgeMarginHeight: Int) {
        with(badgeView) {
            val isLtr = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR
            layoutParams = LayoutParams(
                MotionUtil.dpToPx(context, boundarySquareSize).toInt(),
                MotionUtil.dpToPx(context, boundarySquareSize).toInt(),
            ).apply {
                if (isLtr) leftMargin = badgeMarginWidth else rightMargin = badgeMarginWidth
                topMargin = badgeMarginHeight + MotionUtil.dpToPx(context, 5).toInt()
                gravity = Gravity.CENTER or Gravity.TOP
            }
        }
    }

    /**
     * Adds a color filter to the Lottie animation. If the color is null, any existing color filter is removed.
     *
     * @param layerName The name of the layer in the Lottie animation that the filter will be applied to
     * @param color The color to apply to the layer
     */
    private fun addColorFilterToLottieAnimation(layerName: String, color: Int?) {
        // Each layer in a Lottie animation is associated with a key path which is represented as a string array of keys.
        // We are using "**" before and after the layerName parameter as there may be keys before or after the
        // given key name
        lottieAnimation.addValueCallback(KeyPath("**", layerName, "**"), LottieProperty.COLOR_FILTER) {
            if (color != null) SimpleColorFilter(color) else null
        }
    }

    /**
     * Resets the stroke and fill color of the Lottie animation based on the enabled state (whether the button is clickable)
     *
     * @param isEnabled A boolean indicating whether the button is enabled
     */
    private fun resetLottieAnimationStrokeColor(isEnabled: Boolean) {
        val strokeColor = if (isEnabled) properties.enabledStrokeColor else properties.disabledStrokeColor
        val fillColor = if (isEnabled) null else properties.disabledFillColor

        addColorFilterToLottieAnimation(properties.strokeLayerName, strokeColor)
        addColorFilterToLottieAnimation(properties.fillLayerName, fillColor)
    }
}