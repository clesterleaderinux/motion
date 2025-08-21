package demo.tfmf.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.microsoft.fluentmotion.ui.util.MotionUtil
import fluent.compose.Text
import fluent.compose.demo.fluentmotion.ui.Alpha
import fluent.compose.demo.fluentmotion.ui.MotionCurve
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme

internal val SyncChainSamples: @Composable () -> Unit = {
    var isToggled by remember { mutableStateOf(true) }
    generateSyncChain()
    Text(
        "Enter & Exit",
        color = OneDriveTheme.colors.neutralForeground1,
        modifier = Modifier.clickable {
            isToggled = !isToggled
        },
    )

    MotionUtil.motionChains[ChainType.SyncChain.name]?.let { motionLinkPropsList ->
        for (motionLinkProps in motionLinkPropsList) {
            if (isToggled) {
                motionLinkProps.RenderEnter()
            } else {
                motionLinkProps.RenderExit()
            }
        }
    }
}

private fun generateSyncChain() {
    MotionUtil.clearChain(ChainType.SyncChain.name)
    val chainLink1 = MotionLinkComposableProps(
        chainId = ChainType.SyncChain.name,
        chainIndex = 1,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0.1f),
            MotionTypeKey.Resize.name to Resize(
                wEnter = 0f,
                wIn = 200f,
                wExit = 0f,
                hEnter = 100f,
                hIn = 100f,
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
                .size(100.dp)
                .background(MotionUtil.generateRandomColor()),
        )
    })

    val chainLink2 = MotionLinkComposableProps(
        chainId = ChainType.SyncChain.name,
        chainIndex = 1,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 0.5f, aExit = 0.1f),
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
                .background(MotionUtil.generateRandomColor()),
        )
    })

    val chainLink3 = MotionLinkComposableProps(
        chainId = ChainType.SyncChain.name,
        chainIndex = 1,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0.1f),
            MotionTypeKey.Resize.name to Resize(
                wEnter = 1f,
                wIn = 200f,
                wExit = 10f,
                hEnter = 100f,
                hIn = 100f,
                hExit = 200f,
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
                .background(MotionUtil.generateRandomColor()),
        )
    })

    val chainLink4 = MotionLinkComposableProps(
        chainId = ChainType.SyncChain.name,
        chainIndex = 1,
        curve = MotionCurve.EasingEase01,
        linkId = MotionCurve.EasingEase01.name,
        motionTypes = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0.1f),
            MotionTypeKey.Resize.name to Resize(
                wEnter = 10f,
                wIn = 300f,
                wExit = 100f,
                hEnter = 100f,
                hIn = 200f,
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
                .background(MotionUtil.generateRandomColor()),
        )
    })

    MotionUtil.addMotionChainLink(chainLink1, chainLink1.chainId)
    MotionUtil.addMotionChainLink(chainLink2, chainLink1.chainId)
    MotionUtil.addMotionChainLink(chainLink3, chainLink1.chainId)
    MotionUtil.addMotionChainLink(chainLink4, chainLink1.chainId)
}