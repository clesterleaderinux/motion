package tfmf.ui.demo.compose.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import fluent.compose.demo.fluentmotion.ui.MotionCurve
import fluent.compose.demo.fluentmotion.ui.MotionDuration
import fluent.compose.demo.fluentmotion.ui.MotionLinkComposableProps
import fluent.compose.demo.fluentmotion.ui.MotionTypeKey
import fluent.compose.demo.fluentmotion.ui.Resize
import fluent.compose.demo.fluentmotion.ui.TranslationX
import com.microsoft.fluentmotion.ui.util.MotionUtil
import fluent.compose.Text
import fluent.compose.demo.ui.util.ChainType
import fluent.compose.theme.OneDriveTheme

internal val CurvesSample: @Composable () -> Unit = {
    EasingCurveAnimations()
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        MotionUtil.motionChains[ChainType.Curves.name]?.let { motionLinkPropsList ->
            for (motionLinkProps in motionLinkPropsList) {
                item {
                    motionLinkProps.RenderClickableLink()
                }
            }
        }
    }
}

@Composable
fun EasingCurveAnimations() {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val endPosition = screenWidth - 110f
    for (curve in MotionCurve.values()) {
        val props = MotionLinkComposableProps(
            chainId = ChainType.Curves.name,
            curve = curve,
            linkId = curve.name,
            motionTypes = hashMapOf(
                MotionTypeKey.TranslationX.name to TranslationX(
                    xEnter = 0f,
                    xIn = endPosition,
                    xExit = 0f,
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
            duration = MotionDuration.DurationMedium01,
            onEnterAction = {},
            onExitAction = {},
            composable =
            {
                Text(
                    text = curve.name,
                    color = OneDriveTheme.colors.neutralForeground1,
                    modifier = Modifier.padding(all = 8.dp))
            },
        )
        MotionUtil.addMotionChainLink(props, props.chainId)
    }
}