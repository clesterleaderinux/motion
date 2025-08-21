package tfmf.ui.motion.xml.components

import android.view.View
import tfmf.ui.motion.Alpha
import tfmf.ui.motion.MotionChain
import tfmf.ui.motion.MotionDuration
import tfmf.ui.motion.MotionScaleFactor
import tfmf.ui.motion.MotionState
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.Scale
import tfmf.ui.motion.Stagger
import tfmf.ui.motion.accessibility.IAccessibleMotionView
import tfmf.ui.motion.actions.CancellationError
import tfmf.ui.motion.actions.ICancellable
import tfmf.ui.motion.telemetry.ITelemetryLoggable
import tfmf.ui.motion.telemetry.TelemetryEvent
import tfmf.ui.motion.xml.player.MotionPlayer

/**
 * Defines a layout that allows cascading animations for its child views.
 *
 * @property onEnterText Text to be displayed when the view enters the screen.
 * @property onInText Text to be displayed when the view is in the screen.
 * @property onExitText Text to be displayed when the view exits the screen.
 * @property onCancelAction Action to be performed when the animation is cancelled.
 */
class MotionViewCascadingLayout(
    override var onEnterText: String? = null,
    override var onInText: String? = null,
    override var onExitText: String? = null,
    override var onCancelAction: ((CancellationError) -> Unit)? = null
) : IAccessibleMotionView, ITelemetryLoggable, ICancellable {

    // Hash map to store motion properties such as alpha and scale used for the cascading effect.
    val motionValues = hashMapOf(
        MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0f),
        MotionTypeKey.Scale.name to Scale(
            sEnter = MotionScaleFactor.CascadeNormal.scaleFactor,
            sIn = 1f,
            sExit = 0.9f,
        ),
    )

    // A list of views that will participate in the cascading animation sequence.
    private val cascadingViews: ArrayList<View> = ArrayList()

    // Flag to check if the cascading view set is a collection of groups
    private var isCascadingViewGroup = false

    // Map to store lists of IMotionView objects, keyed by a string
    private var motionViewMap: MutableMap<Int, MutableList<View>> = HashMap()

    // An optional action to be invoked at the end of the cascading animation.
    private var onEndAction: (() -> Unit)? = null

    // An optional action to be invoked at the beginning of the cascading animation.
    private var onEnterAction: (() -> Unit)? = null

    // A key that uniquely identifies a set of cascading views within the animation chain.
    private var chainKey: String? = null

    /**
     * Adds views to the list of cascadingViews and sets up optional parameters.
     * @param key The unique identifier for this group of cascading views.
     * @param onEndAction The end action callback if provided.
     * @param views The provided views to the cascadingViews list.
     */
    fun appendCascadingViews(
        key: String,
        onEnterAction: (() -> Unit)?,
        onEndAction: (() -> Unit)?,
        views: List<View>
    ) {
        chainKey = key
        this.onEndAction = onEndAction
        this.onEnterAction = onEnterAction
        cascadingViews.addAll(views)
    }

    /**
     * Appends a cascading view group to the map and sets the cascading view group flag.
     *
     * This function takes a key and a list of IMotionView objects. It adds the list to the map using the provided key and sets the cascading view group flag to true.
     *
     * @param key The key to be used when adding the list to the map.
     * @param motionViewGroups The list of IMotionView objects to be added to the map.
     */
    fun appendCascadingViewGroup(key: String, motionViewMap: MutableMap<Int, MutableList<View>>) {
        chainKey = key
        this.motionViewMap = motionViewMap
        isCascadingViewGroup = true
    }

    /**
     * Triggers the enter animation for all cascading views.
     *
     * If the current view group is a cascading view group, a specialized enter function is called.
     * Otherwise, a motion chain is created and played for each cascading view.
     * The enter action is invoked at the start of the animation.
     */
    fun onEnter() {
        if (isCascadingViewGroup) {
            // Specialized enter function for cascading view groups
            onEnterForCascadingGroup()
        } else {
            var index = 0
            val chainKey = "onEnter:$chainKey"
            // Clear any existing motion chain for the same key
            MotionPlayer.clearChainForKey(chainKey)
            val motionChainLinks = ArrayList<MotionPlayer>()
            // Invoke the enter action
            onEnterAction?.invoke()
            cascadingViews.forEach { view ->
                // Create a motion link for each cascading view
                val link =
                    MotionViewCascadingLink(
                        motionViewBase = view,
                        motionValues = motionValues,
                        stagger = Stagger.Normal,
                        chainIndex = index
                    )
                index = index.inc()
                motionChainLinks.add(link.motionPlayer)
            }
            // Create a motion chain with the created links
            val motionChain = MotionChain(
                chainKey = chainKey,
                chainLinks = motionChainLinks,
            )
            // Add the motion chain to the player and play it
            MotionPlayer.addMotionChain(chainKey, motionChain)

            // Call with clearOnFinish = true to make sure the chain is cleared after it finishes. Since
            // this chain is not reused afterwards, it is safe to clear it. Otherwise we need to manually
            // clear it to avoid memory leaks.
            MotionPlayer.playEnterChainForKey(chainKey, clearOnFinish = true)
        }
    }

    /**
     * Handles the enter event for cascading groups.
     *
     * This function loops over the values of `motionViewMap`, which are lists of `IMotionView` objects. For each list, it creates a unique `chainKey` and clears any existing chain for that key in `MotionPlayer`.
     * Then, it creates a new `MotionPlayer` for each `IMotionView` in the list, and adds it to `motionChainLinks`. Each `MotionPlayer` is created with a `MotionViewCascadingLink`, which takes the `IMotionView`, `motionValues`, `Stagger.Tight`, the current index, and `onEndAction`.
     * The index is incremented after each `MotionPlayer` is created.
     * After all `MotionPlayer` objects are created for a list, a `MotionChain` is created with the `chainKey` and the list of `MotionPlayer` objects. This `MotionChain` is then added to `MotionPlayer` with the `chainKey`.
     * Finally, the `MotionChain` is played with `MotionPlayer.playEnterChainForKey(chainKey)`.
     * This process is repeated for each list in `motionViewMap`.
     */
    fun onEnterForCascadingGroup() {
        val chainKey = "onEnterForCascadingGroup:$chainKey"
        MotionPlayer.clearChainForKey(chainKey)
        val motionChainLinks = ArrayList<MotionPlayer>()
        onEnterAction?.invoke()
        for ((index, iMotionViewList) in motionViewMap) {
            iMotionViewList.forEach { view ->
                val link =
                    MotionViewCascadingLink(
                        motionViewBase = view,
                        motionValues = motionValues,
                        stagger = Stagger.Normal,
                        chainIndex = index,
                        onEndAction = onEndAction,
                    )
                if (link != null) {
                    motionChainLinks.add(link.motionPlayer)
                }
            }
            val motionChain = MotionChain(
                chainKey = chainKey,
                chainLinks = motionChainLinks,
            )
            MotionPlayer.addMotionChain(chainKey, motionChain)

            // Call with clearOnFinish = true to make sure the chain is cleared after it finishes. Since
            // this chain is not reused afterwards, it is safe to clear it. Otherwise we need to manually
            // clear it to avoid memory leaks.
            MotionPlayer.playEnterChainForKey(chainKey, clearOnFinish = true)
        }

    }

    /**
     * Triggers the exit animation for all cascading views.
     *
     * The exit animation is defined by a set of motion values, which include a scale and alpha transformation.
     * The end action is triggered after the entire cascade has played.
     */
    fun onExit() {
        // Define the motion values for the exit animation
        val exitValues = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 0.4f, aExit = 0f),
            MotionTypeKey.Scale.name to Scale(sEnter = 0f, sIn = 1f, sExit = 0f),
        )

        // Calculate the delay for the end action
        val onEndDelay =
            MotionDuration.DurationMedium03.speedInMillis.plus(Stagger.Tight.delay.times(cascadingViews.size))
                .plus(30L)

        // Trigger the exit animation for each cascading view
        cascadingViews.forEach { view ->
            MotionViewCascadingLink(
                motionViewBase = view,
                motionValues = exitValues,
                motionState = MotionState.Exiting,
                stagger = Stagger.Normal,
                onEndAction = onEndAction
            ).exit()
        }
    }

    /**
     * Log telemetry for animation changes
     */
    override fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        TODO("Not yet implemented")
    }
}