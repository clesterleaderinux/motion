package tfmf.ui.demo.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fluent.compose.demo.fluentmotion.ui.MotionCurve
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.Text
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme

internal val ResizeSamples: @Composable () -> Unit = {
    var isToggled by remember { mutableStateOf(false) }

    Text(
        "Enter & Exit",
        color = OneDriveTheme.colors.neutralForeground1,
        textAlign = TextAlign.Left,
        modifier = Modifier
            .clickable {
                isToggled = !isToggled
            }
            .padding(100.dp),
    )

    val resizeBoth = MotionLinkComposableProps(
        chainId = ChainType.Resize.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                wEnter = 10f,
                wIn = 30f,
                wExit = 10f,
                hEnter = 10f,
                hIn = 500f,
                hExit = 10f,
            ),
        ),
        duration = MotionDuration.DurationLong02,
        onEnterAction = {},
        onExitAction = {},
        composable =
        {
            Box(
                modifier = Modifier
                    .size(width = 100f.dp, height = 100f.dp)
                    .background(OneDriveTheme.colors.neutralForeground1),
            )
        },
    )

    val resizeWidth = MotionLinkComposableProps(
        chainId = ChainType.Resize.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                wEnter = 10f,
                wIn = 100f,
                wExit = 10f,
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
            Box(
                modifier = Modifier
                    .size(width = 100f.dp, height = 100f.dp)
                    .background(OneDriveTheme.colors.statusDangerBackground1),
            )
        },
    )
    val resizeHeight = MotionLinkComposableProps(
        chainId = ChainType.Resize.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Resize.name to Resize(
                wEnter = 100f,
                wIn = 100f,
                wExit = 100f,
                hEnter = 10f,
                hIn = 200f,
                hExit = 10f,
            ),
        ),
        duration = MotionDuration.DurationLong02,
        onEnterAction = {},
        onExitAction = {},
        composable =
        {
            Box(
                modifier = Modifier
                    .size(width = 100f.dp, height = 100f.dp)
                    .background(OneDriveTheme.colors.statusSuccessBackground1)
            )
        },
    )

    if (isToggled) {
        resizeWidth.RenderEnter()
        resizeHeight.RenderEnter()
        resizeBoth.RenderEnter()
    } else {
        resizeWidth.RenderExit()
        resizeHeight.RenderExit()
        resizeBoth.RenderExit()
    }
}