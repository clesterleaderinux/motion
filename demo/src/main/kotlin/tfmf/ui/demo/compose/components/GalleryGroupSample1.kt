package tfmf.ui.demo.compose.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import fluent.compose.demo.fluentmotion.ui.Alpha
import fluent.compose.demo.fluentmotion.ui.GalleryItem
import fluent.compose.demo.fluentmotion.ui.MotionCurve
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Scale
import fluent.compose.demo.fluentmotion.ui.TranslationX
import fluent.compose.demo.fluentmotion.ui.TranslationY
import com.microsoft.fluentmotion.ui.util.MotionUtil
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme
import kotlin.random.Random

internal val GalleryGroupSample1: @Composable (galleryItems: List<GalleryItem>) -> Unit = {
    var shouldEnter by remember { mutableStateOf(true) }
    var shouldExit by remember { mutableStateOf(false) }
    updateGalleryDrawables(galleryItems = it)
    Surface(
        color = OneDriveTheme.colors.neutralForeground1,
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = { shouldEnter = !shouldEnter }),
    ) {
        if (shouldEnter) {
            Surface(
                color = OneDriveTheme.colors.neutralForeground1,
                modifier = Modifier
                    .zIndex(0f)
                    .clickable(
                        onClick = {
                            shouldExit = !shouldExit
                        },
                    ),
            ) {
                if (shouldExit) {
                    MotionUtil.motionChains[ChainType.GalleryGroup1.name]?.forEach { motionLinkProps ->
                        motionLinkProps.RenderExit()
                    }
                } else {
                    updateGalleryDrawables(galleryItems = it)
                    MotionUtil.motionChains[ChainType.GalleryGroup1.name]?.forEach { motionLinkProps ->
                        motionLinkProps.RenderEnter()
                    }
                }
            }
        }
    }
}

fun rand(start: Int, end: Int): Float {
    require(start <= end) { "Invalid range" }

    val random = Random.nextInt(start, end + 1)
    return random.toFloat()
}

@Composable
fun updateGalleryDrawables(galleryItems: List<GalleryItem>) {
    MotionUtil.clearChain(ChainType.GalleryGroup1.name)
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    for ((index, galleryItem) in galleryItems.withIndex()) {
        var xAll = index.times(rand(60, 85))
        var yEnd = rand(180, 280)
        val motionLinkComposableProps = MotionLinkComposableProps(
            chainId = ChainType.GalleryGroup1.name,
            curve = MotionCurve.EasingEase01,
            linkId = "galleryItem$index",
            motionTypes = hashMapOf(
                MotionTypeKey.Alpha.name to Alpha(aExit = 0f, aEnter = 1f, aIn = 1f),
                MotionTypeKey.Scale.name to Scale(sEnter = 1f, sIn = 1f, sExit = 1f),
                MotionTypeKey.TranslationX.name to TranslationX(
                    xEnter = xAll,
                    xIn = xAll,
                    xExit = xAll,
                ),
                MotionTypeKey.TranslationY.name to TranslationY(
                    yEnter = -140f,
                    yIn = yEnd,
                    yExit = screenHeight.value,
                ),
            ),
            duration = MotionDuration.DurationLong01,
            chainDelay = rand(100, 240).toLong(),
            zIndex = index,
            onEnterAction = {},
            onExitAction = {},
            composable =
        {
            Image(
                painter = painterResource(id = galleryItem.drawable),
                contentDescription = galleryItem.contentDescription + " galleryItem$index",
                modifier = Modifier
                    .clip(RoundedCornerShape(15.dp))
                    .height(galleryItem.size.height.dp)
                    .width(galleryItem.size.width.dp)
                    .border(
                        4.dp,
                        color = OneDriveTheme.colors.neutralForeground1,
                        RoundedCornerShape(15.dp)),
            )
        })
        MotionUtil.addMotionChainLink(motionLinkComposableProps, motionLinkComposableProps.chainId)
    }
}