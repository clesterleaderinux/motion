package tfmf.mobile.ui.xml.exception

/**
 * Custom exception class for handling MotionViewGroup related errors.
 * @param message The error message associated with the exception.
 */
class MotionViewGroupException(message: ErrorMessage) : Exception(message.errorMessage) {

    /**
     * Enum class representing the different types of error messages that can be thrown.
     */
    enum class ErrorMessage(val errorMessage: String) {
        /**
         * Error when the FullScreenMotionViewGroup is not found in the view hierarchy.
         */
        FullScreenMotionViewGroupNotFound(
            "FullScreenMotionViewGroupNotFound in the hierarchy. " +
                "You might need to add it as a top level container",
        ),

        /**
         * Error for specifying the required number of Lottie animation drawables for MotionLottieAnimatedButton.
         */
        MotionMissingLottieAnimationDrawable(
            "You must specify 2 lottie animation drawables " +
                "for the MotionLottieAnimatedButton, 1 for active and 1 for inactive",
        ),

        /**
         * Error when a full screen Lottie animation is missing where one is expected.
         */
        MotionMissingFullScreenLottieAnimation(
            "You must specify 1 full screen lottie animation",
        )
    }
}

