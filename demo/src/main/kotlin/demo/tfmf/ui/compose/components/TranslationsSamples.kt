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
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.demo.fluentmotion.ui.TranslationX
import fluent.compose.demo.fluentmotion.ui.TranslationY
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme
import tfmf.ui.MotionCurve

internal val TranslationSamples: @Composable () -> Unit = {
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

    val translationBoth = MotionLinkComposableProps(
        chainId = ChainType.Translation.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.TranslationX.name to TranslationX(
                xEnter = 10f,
                xIn = 100f,
                xExit = 300f,
            ),
            MotionTypeKey.TranslationY.name to TranslationY(
                yEnter = 10f,
                yIn = 100f,
                yExit = 300f,
            ),
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

    val translationX = MotionLinkComposableProps(
        chainId = ChainType.Translation.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.TranslationX.name to TranslationX(
                xEnter = 10f,
                xIn = 0f,
                xExit = 300f,
            ),
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
                .background(OneDriveTheme.colors.statusSuccessBackground1),
        )
    })

    val translationY = MotionLinkComposableProps(
        chainId = ChainType.Translation.name,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.TranslationY.name to TranslationY(
                yEnter = -100f,
                yIn = 0f,
                yExit = 200f,
            ),
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
                .background(OneDriveTheme.colors.brandBackground1),
        )
    })

    if (isToggled) {
        translationX.RenderEnter()
        translationY.RenderEnter()
        translationBoth.RenderEnter()
    } else {
        translationX.RenderExit()
        translationY.RenderExit()
        translationBoth.RenderExit()
    }
}
