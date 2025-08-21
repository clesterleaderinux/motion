package demo.tfmf.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

internal val GalleryShuffleSample: @Composable () -> Unit = {
    var isToggled by remember { mutableStateOf(true) }
    val chainId = ChainType.GalleryShuffle.name
    generateGallery(chainId)
    Text(
        "Enter & Exit",
        color = Color.Green,
        modifier = Modifier.clickable {
            isToggled = !isToggled
        },
    )
    Row {
        MotionUtil.motionChains[chainId]?.let { motionLinkPropsList ->
            for (motionLinkProps in motionLinkPropsList) {
                if (isToggled) {
                    motionLinkProps.RenderEnter()
                } else {
                    motionLinkProps.RenderExit()
                }
            }
        }
    }
}

fun generateGallery(chainId: String) {
    MotionUtil.clearChain(chainId)
    val motionLinkPropsList = mutableListOf<MotionLinkComposableProps>()
    for (i in 1..4) {
        val galleryShuffle = hashMapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0.1f),
            MotionTypeKey.Resize.name to Resize(
                wEnter = 0f,
                wIn = 100f,
                wExit = 0f,
                hEnter = 0f,
                hIn = 250f,
                hExit = 0f,
            ),
        )
        val chainLink = MotionLinkComposableProps(
            chainId = chainId,
            chainIndex = i,
            chainDelay = i * 180L,
            curve = MotionCurve.EasingEase01,
            linkId = MotionCurve.EasingEase01.name,
            motionTypes = galleryShuffle,
            duration = MotionDuration.DurationLong02,
            onEnterAction = {},
            onExitAction = {},
            composable = {
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MotionUtil.generateRandomColor()),
                )
            },
        )
        motionLinkPropsList.add(chainLink)
        MotionUtil.addMotionChainLink(chainLink, chainLink.chainId)
    }
}