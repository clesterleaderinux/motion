package tfmf.mobile.ui.actions

/**
 * Enum representing different types of cancellation errors.
 */
enum class CancellationError {
    /**
     * Represents a default cancellation error.
     */
    Default,

    /**
     * Represents a cancellation error due to exceeding maximum memory.
     */
    MemoryMaxExceeded,

    /**
     * Represents a cancellation error due to loading timeout.
     */
    LoadingTimeout,

    /**
     * Represents a cancellation when a fragment is destroyed
     */
    FragmentDestroyed
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