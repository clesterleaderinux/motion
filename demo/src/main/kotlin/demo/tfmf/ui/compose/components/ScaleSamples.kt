package demo.tfmf.ui.compose.components

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
import fluent.compose.Text
import fluent.compose.demo.fluentmotion.ui.MotionCurve
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.demo.fluentmotion.ui.Scale
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme

internal val ScaleSamples: @Composable () -> Unit = {
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

    val scale = MotionLinkComposableProps(
        chainId = ChainType.Scale.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Scale.name to Scale(sEnter = 0.1f, sIn = 3f, sExit = 0.3f),
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
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(OneDriveTheme.colors.statusDangerBackground1),
        )
    })

    if (isToggled) {
        scale.RenderEnter()
    } else {
        scale.RenderExit()
    }
}