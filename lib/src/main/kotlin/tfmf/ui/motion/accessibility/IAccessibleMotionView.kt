package tfmf.ui.motion.accessibility

import tfmf.ui.motion.actions.CancellationError
import tfmf.ui.motion.telemetry.TelemetryEvent

/**
 * Accessibility for Talkback
 */
interface IAccessibleMotionView {
    /**
     * Text to be announced when an animation begins
     */
    var onEnterText: String?

    /**
     * Text to be announced when an animation settles
     */
    var onInText: String?

    /**
     * Text to be announced when an animation exits
     */
    var onExitText: String?
}

/**
 * Interface for logging telemetry events.
 */
interface ITelemetryLoggable {
    fun logTelemetryForAction(event: TelemetryEvent, log: String)
}

/**
 * Interface for cancellable actions.
 */
interface ICancellable {
    /**
     * Action to be performed on cancellation.
     *
     * @property onCancelAction A lambda function that takes a CancellationError as input and returns Unit.
     */
    var onCancelAction: ((CancellationError) -> Unit)?
}