package com.microsoft.fluentmotion

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import tfmf.ui.Alpha
import tfmf.ui.MotionCurve
import tfmf.ui.MotionDuration
import tfmf.ui.MotionInterpolator
import tfmf.ui.MotionLinkComposableProps
import tfmf.ui.MotionScaleFactor
import tfmf.ui.MotionTypeKey
import tfmf.ui.Resize
import tfmf.ui.TranslationX
import tfmf.ui.TranslationY
import tfmf.ui.actions.CancellationError
import tfmf.ui.util.MotionUtil
import tfmf.ui.xml.base.IMotionView
import tfmf.ui.xml.layouts.MotionViewCardViewLayout
import kotlin.coroutines.CoroutineContext

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MotionUtilTests {

    @Before
    fun setUp() {
    }

    @Test
    fun testGenerateRandomColor_returnsValidColor() {
        val randomColor: androidx.compose.ui.graphics.Color = MotionUtil.generateRandomColor()
        assertNotNull(randomColor)
        // Assuming Color class has getRed(), getGreen(), and getBlue() methods,
        // and values must be between 0-255
        assertTrue(randomColor.red in 0.0..255.0)
        assertTrue(randomColor.green in 0.0..255.0)
        assertTrue(randomColor.blue in 0.0..255.0)
    }

    @Test
    fun testAddMotionChainLink_addsProperly() {
        val chainId = "testChain"
        val props = MotionLinkComposableProps(
            chainId = "testChain",
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
            composable = {},
            onEnterAction = null,
            onExitAction = null,
        )
        MotionUtil.addMotionChainLink(props, chainId)

        // Verify the motion link was added
        assertEquals(1L, MotionUtil.motionChains.size.toLong())
        MotionUtil.motionChains[chainId]?.let { assertTrue(it.contains(props)) }
    }

    @Test
    fun testClearChain_clearsCorrectly() {
        val chainId = "testChain"
        MotionUtil.motionChains[chainId] = ArrayList()
        MotionUtil.clearChain(chainId)
        MotionUtil.motionChains[chainId]?.let { assertTrue(it.isEmpty()) }
    }

    @Test
    fun testAppendContentResizeValueAnimator() {
        val mockView = Mockito.mock(View::class.java)
        val mockLayoutParams = Mockito.mock(ViewGroup.LayoutParams::class.java)
        Mockito.`when`(mockView.layoutParams).thenReturn(mockLayoutParams)

        val mockInterpolator = Mockito.mock(Interpolator::class.java)
        val mockValueAnimator = Mockito.mock(ValueAnimator::class.java)
        val animationCollection = mutableListOf<Animator?>()
        animationCollection.add(0, mockValueAnimator)
        val start = 100
        val end = 200
        val isHeight = true

        MotionUtil.appendContentResizeValueAnimator(
            MotionTypeKey.Resize,
            mockView,
            animationCollection,
            mockInterpolator,
            start,
            end,
            isHeight,
        )

        Mockito.verify(mockView, Mockito.times(1)).layoutParams = mockLayoutParams
        assert(animationCollection.size == 2)
        assert(animationCollection[1]?.duration == MotionDuration.DurationMedium03.speedInMillis)
    }

    @Test
    fun testContainsValueType() {
        // Arrange
        val motionValues = mapOf(
            MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0f),
            MotionTypeKey.TranslationX.name to TranslationX(xEnter = 0f, xIn = 1f, xExit = 0f),
            MotionTypeKey.TranslationY.name to TranslationY(yEnter = 0f, yIn = 1f, yExit = 0f),
        )
        val existingProperty = MotionTypeKey.Alpha
        val nonExistingProperty = MotionTypeKey.Resize

        val containsExistingProperty = MotionUtil.containsValueType(motionValues, existingProperty)
        val containsNonExistingProperty = MotionUtil.containsValueType(motionValues, nonExistingProperty)

        assertTrue(containsExistingProperty)
        assertFalse(containsNonExistingProperty)
    }

    @Test
    fun testCancelRunningAnimationCoroutines() {
        // Arrange
        val mockCoroutineScope1 = Mockito.mock(CoroutineScope::class.java)
        val mockCoroutineScope2 = Mockito.mock(CoroutineScope::class.java)
        val mockJob1 = Mockito.mock(Job::class.java)
        val mockJob2 = Mockito.mock(Job::class.java)
        val mockCoroutineContext1 = Mockito.mock(CoroutineContext::class.java)
        val mockCoroutineContext2 = Mockito.mock(CoroutineContext::class.java)
        val runningAnimationCoroutines = mutableMapOf(
            "chain1" to mockCoroutineScope1,
            "chain2" to mockCoroutineScope2,
        )

        Mockito.`when`(mockCoroutineContext1[Job]).thenReturn(mockJob1)
        Mockito.`when`(mockCoroutineContext2[Job]).thenReturn(mockJob2)
        Mockito.`when`(mockCoroutineScope1.coroutineContext).thenReturn(mockCoroutineContext1)
        Mockito.`when`(mockCoroutineScope2.coroutineContext).thenReturn(mockCoroutineContext2)

        MotionUtil.runningAnimationCoroutines = runningAnimationCoroutines
        MotionUtil.cancelAllRunningAnimationCoroutines(CancellationError.Default)

        Mockito.verify(mockJob1).cancel()
        Mockito.verify(mockJob2).cancel()
    }

    @Test
    fun testInitMotionPlayer() {
        // Arrange
        val mockMotionView = Mockito.mock(IMotionView::class.java)
        val mockView = Mockito.mock(View::class.java)

        Mockito.`when`(mockMotionView.motionViewBase).thenReturn(mockView)
        Mockito.`when`(mockMotionView.motionValues).thenReturn(mutableMapOf())
        Mockito.`when`(mockMotionView.duration).thenReturn(1000L)
        Mockito.`when`(mockMotionView.curveEnter)
            .thenReturn(MotionInterpolator.EasingAcelerate01.interpolator)
        Mockito.`when`(mockMotionView.curveExit)
            .thenReturn(MotionInterpolator.EasingAcelerate01.interpolator)
        Mockito.`when`(mockMotionView.chainDelay).thenReturn(500)
        Mockito.`when`(mockMotionView.chainKey).thenReturn("chain1")

        val motionPlayer = MotionUtil.initMotionPlayer(mockMotionView)

        assertEquals(mockMotionView.motionViewBase, motionPlayer.motionViewBase)
        assertEquals(mockMotionView, motionPlayer.motionView)
        assertEquals(mockMotionView.motionValues, motionPlayer.motionValues)
        assertEquals(mockMotionView.duration, motionPlayer.duration)
        assertEquals(mockMotionView.curveEnter, motionPlayer.curveOnEnter)
        assertEquals(mockMotionView.curveExit, motionPlayer.curveOnExit)
        assertEquals(mockMotionView.chainDelay, motionPlayer.nextIndexDelay)
    }

    @Test
    fun testAppendCornerRadiusValueAnimator() {
        // Arrange
        val property = MotionTypeKey.TranslationX
        val motionView = Mockito.mock(MotionViewCardViewLayout::class.java)
        val animationCollection = mutableListOf<Animator?>()
        val aInterpolator = LinearInterpolator()
        val start = 0f
        val end = 1f

        // Act
        MotionUtil.appendCornerRadiusValueAnimator(property, motionView, animationCollection, aInterpolator, start, end)

        // Assert
        assert(animationCollection.size == 1)
        val animator = animationCollection[0] as ValueAnimator
        assert(animator.interpolator == aInterpolator)
    }

    @Test
    fun testSetTheBaseCascadeStateForViews() {
        // Arrange
        val view1 = Mockito.mock(View::class.java)
        val view2 = Mockito.mock(View::class.java)
        val views = listOf(view1, view2)

        // Act
        MotionUtil.setTheBaseCascadeStateForViews(views)

        // Assert
        for (view in views) {
            Mockito.verify(view).alpha = 0f
            Mockito.verify(view).scaleX = MotionScaleFactor.CascadeLight.scaleFactor
            Mockito.verify(view).scaleY = MotionScaleFactor.CascadeLight.scaleFactor
            Mockito.verify(view).invalidate()
        }
    }

    @Test
    fun testConvertBezierToCurve() {
        val index = 0
        val expectedInterpolator = MotionInterpolator.values()[index].interpolator

        val result = MotionUtil.convertBezierToCurve(index)

        assertEquals(expectedInterpolator, result)
    }
}