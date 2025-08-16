package tfmf.mobile.ui.telemetry

/**
 * Interface for logging telemetry events.
 */
interface ITelemetryLoggable {
    fun logTelemetryForAction(event: TelemetryEvent, log: String)
}

/**
 * Enum class for telemetry event identifiers.
 */
enum class TelemetryEvent {
    Enter,
    Settled,
    Exit,
    PivotTabSwitch,
    Cancellation
}

object TelemetryLogger {

    /**
     * Logs a telemetry event.
     *
     * @param event The telemetry event to log.
     * @param log The log message associated with the event.
     */
    fun logTelemetryForAction(event: TelemetryEvent, log: String) {
        // log telemetry
    }
}