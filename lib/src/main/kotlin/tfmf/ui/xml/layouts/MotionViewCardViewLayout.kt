package tfmf.mobile.ui.xml.layouts

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import androidx.cardview.widget.CardView
import com.microsoft.fluentmotion.R
import tfmf.mobile.ui.Alpha
import tfmf.mobile.ui.CardViewElevation
import tfmf.mobile.ui.CornerRadius
import tfmf.mobile.ui.Elevation
import tfmf.mobile.ui.MotionCurve
import tfmf.mobile.ui.MotionDuration
import tfmf.mobile.ui.MotionInterpolator
import tfmf.mobile.ui.MotionState
import tfmf.mobile.ui.MotionTypeKey
import tfmf.mobile.ui.MotionValue
import tfmf.mobile.ui.Resize
import tfmf.mobile.ui.Scale
import tfmf.mobile.ui.ScrollX
import tfmf.mobile.ui.TranslationX
import tfmf.mobile.ui.TranslationY
import tfmf.mobile.ui.accessibility.IAccessibleMotionView
import tfmf.mobile.ui.actions.CancellationError
import tfmf.mobile.ui.util.MotionUtil
import tfmf.mobile.ui.xml.base.IMotionView
import tfmf.mobile.ui.xml.player.MotionPlayer

/**
 * A custom layout that extends CardView, implementing IMotionView and IAccessibleMotionView interfaces.
 * This layout is designed to handle motion view and accessibility features.
 *
 * @property context The Context the view is running in, through which it can access the current theme, resources, etc.
 * @property attrs The attributes of the AXML element declaring the view.
 * @property defStyleAttr An attribute in the current theme that contains a reference to a style resource to apply to this view.
 */
class MotionViewCardViewLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr),
    IMotionView, IAccessibleMotionView {

    // Properties inherited from IMotionView, initialized with default values or provided parameters.
    override var motionViewBase: View? = null
    override var motionPlayer: MotionPlayer = MotionPlayer()
    override var motionValues: Map<String, MotionValue?> = HashMap()
    override var playTogether: Boolean = true
    override var motionKey: String? = null
    override var duration: Long = 0L
    override var motionState: Int = MotionState.Exiting.index
    override var onEndAction: (() -> Unit)? = null
    override var onEnterAction: (() -> Unit)? = null
    override var onCancelAction: ((CancellationError) -> Unit)? = null
    override var curveEnter: Interpolator? = MotionInterpolator.EasingEase01.interpolator
    override var curveExit: Interpolator? = MotionInterpolator.EasingEase01.interpolator
    override var chainIndex: Int = 0
    override var chainKey: String? = null
    override var chainDelay: Int = 0
    override var startDurationDelay: Long = 0L

    // accessibility text
    override var onEnterText: String? = null
    override var onInText: String? = null
    override var onExitText: String? = null

    init {
        if (attrs != null) {
            initMotionView(motionView = this, attrs = attrs, context = context)
        }
        motionPlayer = MotionUtil.initMotionPlayer(motionView = this)
    }

    /**
     * Begins the enter animations associated with this view.
     */
    override fun enter() {
        motionPlayer.enter()
    }

    /**
     * Begins the exit animations associated with this view.
     */
    override fun exit() {
        motionPlayer.exit()
    }

    /**
     * Sets an action to perform when the animation ends and returns the current view.
     * @param performEndAction The action to be performed at the end.
     * @return The current IMotionView instance.
     */
    override fun setOnEndAction(performEndAction: (() -> Unit)?): IMotionView {
        onEndAction = performEndAction
        return this
    }

    /**
     * Sets an action to perform when the animation enters (begins) and returns the current view.
     * @param performEnterAction The action to be performed at the start.
     * @return The current IMotionView instance.
     */
    override fun setOnEnterAction(performEnterAction: (() -> Unit)?): IMotionView {
        onEnterAction = performEnterAction
        return this
    }

    /**
     * Provides the view that can be animated.
     * @return The view that can be animated.
     */
    override fun animatableLayout(): View? {
        return this
    }

    /**
     * Load properties from XML.
     * @param motionView The IMotionView to initialize.
     * @param attrs The AttributeSet object containing the attribute values from the XML.
     * @param context The Context the view is running in.
     */
    private fun initMotionView(
        motionView: IMotionView,
        attrs: AttributeSet,
        context: Context
    ) {
        val motionValues: HashMap<String, MotionValue?> = HashMap()
        val a = context.obtainStyledAttributes(attrs, R.styleable.MotionViewCardViewLayout, 0, 0)
        motionValues.also { motionView.motionValues = it }

        val motionProperties = a.getInteger(
            R.styleable.MotionViewCardViewLayout_motion_type_key,
            MotionTypeKey.None.index,
        )

        motionView.playTogether = a.getBoolean(
            R.styleable.MotionViewCardViewLayout_motion_playtogether, true,
        )
        motionView.duration = a.getInteger(
            R.styleable.MotionViewCardViewLayout_motion_duration,
            MotionDuration.DurationMedium01.speedInMillis.toInt(),
        ).toLong()
        motionView.motionState = a.getInt(
            R.styleable.MotionViewCardViewLayout_motion_transition,
            MotionState.Exiting.index,
        )
        motionView.motionKey = a.getString(
            R.styleable.MotionViewCardViewLayout_motion_key,
        )
        MotionInterpolator.values()[
            a.getInt(
                R.styleable.MotionViewCardViewLayout_motion_curve,
                MotionCurve.EasingEase01.index,
            ),
        ].interpolator.also { motionView.curveEnter = it }

        motionView.chainDelay = a.getInt(
            R.styleable.MotionViewCardViewLayout_motion_chain_delay,
            0,
        )
        motionView.chainIndex = a.getInt(
            R.styleable.MotionViewCardViewLayout_motion_chain_index,
            0,
        )
        motionView.chainKey = a.getString(
            R.styleable.MotionViewCardViewLayout_motion_chain_key,
        )

        val cornerRadius = CornerRadius(
            crEnter = a.getDimension(
                R.styleable.MotionViewCardViewLayout_motion_corner_radius_enter,
                0f,
            ),
            crIn = a.getDimension(
                R.styleable.MotionViewCardViewLayout_motion_corner_radius_in,
                0f,
            ),
            crExit = a.getDimension(
                R.styleable.MotionViewCardViewLayout_motion_corner_radius_exit,
                0f,
            ),
        )

        val translationX = TranslationX(
            xEnter = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_xEnter,
                0,
            ).toFloat(),
            xIn = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_xIn,
                0,
            ).toFloat(),
            xExit = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_xExit,
                0,
            ).toFloat(),
        )

        val translationY = TranslationY(
            yEnter = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_yEnter,
                0,
            ).toFloat(),
            yIn = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_yIn,
                0,
            ).toFloat(),
            yExit = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_yExit,
                0,
            ).toFloat(),
        )

        val alpha = Alpha(
            aEnter = a.getFloat(
                R.styleable.MotionViewCardViewLayout_motion_aEnter,
                1f,
            ),
            aIn = a.getFloat(
                R.styleable.MotionViewCardViewLayout_motion_aIn,
                1f,
            ),
            aExit = a.getFloat(
                R.styleable.MotionViewCardViewLayout_motion_aExit,
                1f,
            ),
        )

        val scale = Scale(
            sEnter = a.getFloat(
                R.styleable.MotionViewCardViewLayout_motion_sEnter,
                1f,
            ),
            sIn = a.getFloat(
                R.styleable.MotionViewCardViewLayout_motion_sIn,
                1f,
            ),
            sExit = a.getFloat(
                R.styleable.MotionViewCardViewLayout_motion_sExit,
                1f,
            ),
        )

        val scrollX = ScrollX(
            sEnter = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_scrollXEnter,
                0,
            ).toFloat(),
            sIn = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_scrollXIn,
                0,
            ).toFloat(),
            sExit = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_scrollXExit,
                0,
            ).toFloat(),
        )

        val cardViewElevation = CardViewElevation(
            cvElevationEnter = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_cardElevationEnter,
                0,
            ).toFloat(),
            cvElevationIn = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_cardElevationIn,
                0,
            ).toFloat(),
            cvElevationExit = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_cardElevationExit,
                0,
            ).toFloat(),
        )
        val resize = Resize(
            hEnter = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_hEnter,
                0,
            ).toFloat(),
            hIn = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_hIn,
                0,
            ).toFloat(),
            hExit = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_hExit,
                0,
            ).toFloat(),
            wEnter = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_wEnter,
                0,
            ).toFloat(),
            wIn = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_wIn,
                0,
            ).toFloat(),
            wExit = a.getDimensionPixelSize(
                R.styleable.MotionViewCardViewLayout_motion_wExit,
                0,
            ).toFloat(),
        )

        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.TranslationX.index)) {
            motionValues[MotionTypeKey.TranslationX.name] = translationX
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.TranslationY.index)) {
            motionValues[MotionTypeKey.TranslationY.name] = translationY
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.Resize.index)) {
            motionValues[MotionTypeKey.Resize.name] = resize
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.CardViewElevation.index)) {
            motionValues[MotionTypeKey.CardViewElevation.name] = cardViewElevation
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.Scale.index)) {
            motionValues[MotionTypeKey.Scale.name] = scale
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.Alpha.index)) {
            motionValues[MotionTypeKey.Alpha.name] = alpha
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.ScrollX.index)) {
            motionValues[MotionTypeKey.ScrollX.name] = scrollX
        }
        if (MotionUtil.containsFlag(motionProperties, MotionTypeKey.CornerRadius.index)) {
            motionValues[MotionTypeKey.CornerRadius.name] = cornerRadius
        }

        onEnterText = a.getString(
            R.styleable.MotionViewCardViewLayout_on_enter_text,
        )
        onInText = a.getString(
            R.styleable.MotionViewCardViewLayout_on_in_text,
        )
        onExitText = a.getString(
            R.styleable.MotionViewCardViewLayout_on_exit_text,
        )
        a.recycle()
    }
}