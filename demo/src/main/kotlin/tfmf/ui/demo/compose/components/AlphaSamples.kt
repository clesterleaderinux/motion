package tfmf.ui.demo.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fluent.compose.demo.fluentmotion.ui.Alpha
import fluent.compose.demo.fluentmotion.ui.MotionCurve
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.Text
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme

internal val AlphaSamples: @Composable () -> Unit = {
    var isToggled by remember { mutableStateOf(false) }
    val alpha = MotionLinkComposableProps(
        chainId = ChainType.Alpha.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0.1f),
            MotionTypeKey.Resize.name to Resize(
                wEnter = 100f,
                wIn = 100f,
                wExit = 100f,
                hEnter = 100f,
                hIn = 100f,
                hExit = 100f,
            ),
        ),
        duration = MotionDuration.DurationLong02,
        onEnterAction = {},
        onExitAction = {},
        composable =
        {
//            GlideImage(
//                modifier = Modifier
//                    .size(40.dp)
//                    .background(Color().Green),
//                imageModel = "https://yt3.ggpht.com/ytc/AMLnZu-v-ApUfdP0KinqrJQyNYP5BVd1ke0C7HsoTtkH=s900-c-k-c0x00ffffff-no-rj",
//                success = {
//                    Image(
//                        painter = rememberDrawablePainter(drawable = it.drawable),
//                        contentDescription = null
//                    )
//                },
//                failure = {
//                    Image(
//                        painter = rememberDrawablePainter(drawable = it.errorDrawable),
//                        contentDescription = null
//                    )
//                },
//                loading = {
//                    Box(modifier = Modifier.matchParentSize()) {
//                        CircularProgressIndicator(
//                            modifier = Modifier.align(Alignment.Center),
//                            progress = it.progress
//                        )
//                    }
//                }
//            )
        },
    )
    Text(
        "Enter & Exit",
        color = OneDriveTheme.colors.neutralForeground1,
        modifier = Modifier.clickable {
            isToggled = !isToggled
        },
    )

    if (isToggled) {
        alpha.RenderEnter()
    } else {
        alpha.RenderExit()
    }
}