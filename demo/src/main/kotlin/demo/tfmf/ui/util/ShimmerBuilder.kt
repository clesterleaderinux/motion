package demo.tfmf.ui.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import demo.tfmf.ui.util.ViewUtils.dpToPx
import tfmf.ui.motion.Alpha
import tfmf.ui.motion.MotionTypeKey
import tfmf.ui.motion.Scale
import tfmf.ui.motion.xml.base.MotionViewBase
import tfmf.ui.motion.xml.layouts.MotionViewShimmerLayout
import kotlin.math.roundToInt

/**
 * A builder class for creating a ShimmerFrameLayout with a LinearLayout.
 *
 * @param context The context to use for creating views.
 */
class ShimmerBuilder(context: Context) : LinearLayout(context) {

    private val standardHeaderSpacerLarge: Int = 20
    private var paragraphRowTopMargin = 16
    private var blockTopMargin = 30
    private var elementColor = Color.LTGRAY
    private var gridMargin = 20
    private var paragraphRowHeight = 60
    private var borderMargins = 26
    private var cornerRadius = 10f
    private var shimmerGravity =  Gravity.CENTER
    private val motionValues = hashMapOf(
        MotionTypeKey.Alpha.name to Alpha(aEnter = 0f, aIn = 1f, aExit = 0f),
        MotionTypeKey.Scale.name to Scale(sEnter = 1f, sIn = 1f, sExit = 0.5f),
    )

    private val linearLayout = LinearLayout(context).apply {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    /**
     * Initializes the ShimmerBuilder with default settings.
     */
    fun init() {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    /**
     * Adds a custom view to the linear layout and returns the ShimmerBuilder instance.
     *
     * @param view The view to be added to the linear layout.
     * @return The ShimmerBuilder instance.
     */
    fun addCustomView(view: View): ShimmerBuilder {
        linearLayout.addView(view)
        return this
    }

    /**
     * Creates a view containing a box and a paragraph with the specified dimensions and alignment.
     *
     * @param width The width of the box.
     * @param height The height of the box and the paragraph.
     * @param paragraphWidth The width of the ghost text
     * @return A [LinearLayout] containing the box and the paragraph.
     */
    private fun createBlockWithParagraph(width: Int, height: Int, paragraphWidth: Int): LinearLayout {
        val linearLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }

        val block = createBlockView(width, height)
        val paragraph = createParagraph(
            height,
            ((height - (paragraphRowTopMargin.times(3)) / (paragraphRowHeight)).toDouble().roundToInt()), paragraphWidth)
        linearLayout.addView(block)
        linearLayout.addView(paragraph)

        return linearLayout
    }

    /**
     * Creates a LinearLayout containing a block view and a paragraph with a fixed number of lines.
     *
     * @param width The width of the block view in pixels.
     * @param height The height of the block view and paragraph in pixels.
     * @param lineCount The number of lines in the paragraph.
     * @param paragraphWidth The width of the individual line items in a paragraph
     * @return Returns a LinearLayout containing the block view and paragraph.
     */
    private fun createBlockWithFixedLineItemCount(width: Int, height: Int, lineCount: Int, paragraphWidth: Int): LinearLayout {
        val linearLayout = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
        }

        val block = createBlockView(width, height)
        val paragraph = createParagraph(
            ViewUtils.dpToPx(context, 20).toInt(),
            lineCount,
            paragraphWidth
        ).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        linearLayout.addView(block)
        linearLayout.addView(paragraph)

        return linearLayout
    }

    /**
     * Creates a GridLayout with the specified number of rows and columns.
     *
     * @param context The context to use for creating the GridLayout.
     * @param rows The number of rows in the GridLayout.
     * @param columns The number of columns in the GridLayout.
     * @param radius Corner radius for the grid items
     * @param divisor Divisor used to created fitted grid elements
     * @return The created GridLayout.
     */
    private fun createGridLayout(
        context: Context,
        rows: Int,
        columns: Int,
        radius: Float? = null,
        divisor: Float? = null
    ): GridLayout {
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginToRemove = gridMargin.times(columns) + borderMargins.times(2)
        val cellSize = (screenWidth - marginToRemove).div((divisor ?: columns).toFloat())

        return GridLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            columnCount = columns
            rowCount = rows
            for (i in 0 until rows) {
                for (j in 0 until columns) {
                    val view = View(context)
                    val params = GridLayout.LayoutParams().apply {
                        width = cellSize.toInt()
                        height = cellSize.toInt()
                        setMargins(gridMargin, gridMargin, gridMargin, gridMargin)
                        rowSpec = GridLayout.spec(i)
                        columnSpec = GridLayout.spec(j)
                    }
                    view.layoutParams = params
                    view.background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = radius ?: 0f
                        setColor(elementColor)
                    }
                    addView(view)
                }
            }
        }
    }

    /**
     * Creates a grid layout of circle views.
     *
     * This function creates a GridLayout with a specified number of rows and columns. Each cell in the grid
     * contains a View that is shaped as a circle. The size of the circle is determined by the screen width
     * and the number of columns in the grid.
     *
     * @param context The context used to create the GridLayout and the Views.
     * @param rows The number of rows in the grid.
     * @param columns The number of columns in the grid.
     * @return A GridLayout containing circle views.
     */
    private fun createCircleGridLayout(
        context: Context,
        rows: Int,
        columns: Int
    ): GridLayout {
        val displayMetrics = context.resources.displayMetrics
        val usableWidth = displayMetrics.widthPixels - (columns * gridMargin)
        val marginToRemove = gridMargin.times(columns) + borderMargins.times(2)
        val cellSize = (usableWidth - marginToRemove) / columns
        val topMargin = dpToPx(context, 38).toInt()

        return GridLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            columnCount = columns
            rowCount = rows
            for (i in 0 until rows) {
                for (j in 0 until columns) {
                    val view = View(context)
                    val params = GridLayout.LayoutParams().apply {
                        width = cellSize
                        height = cellSize
                        setMargins(gridMargin, topMargin, gridMargin, gridMargin)
                        rowSpec = GridLayout.spec(i)
                        columnSpec = GridLayout.spec(j)
                    }
                    view.layoutParams = params
                    view.background = ShapeDrawable(OvalShape()).apply {
                        intrinsicHeight = cellSize
                        intrinsicWidth = cellSize
                        paint.color = elementColor
                    }
                    addView(view)
                }
            }
        }
    }

    /**
     * Creates a TextView with the specified width and height.
     *
     * @param width The width of the TextView.
     * @param height The height of the TextView.
     * @return The created TextView.
     */
    private fun createTextView(width: Int, height: Int): TextView {
        return TextView(context).apply {
            addCornerRadiusAndColor(this)
            layoutParams = LayoutParams(
                width,
                height,
            ).apply {
                topMargin = paragraphRowTopMargin
                leftMargin = 18
            }
        }
    }

    /**
     * Creates a block view with the specified width and height, and adds it to the linear layout.
     *
     * @param width The width of the block. If the width is 0, the width is set to [ViewGroup.LayoutParams.MATCH_PARENT].
     * @param height The height of the block.
     * @return The box view.
     */
    private fun createBlockView(width: Int, height: Int): View {
        return View(context).apply {
            addCornerRadiusAndColor(this)
            layoutParams = LayoutParams(
                if (width == 0) LayoutParams.MATCH_PARENT else width,
                height,
            ).apply {
                topMargin = blockTopMargin
                rightMargin = blockTopMargin
            }
        }
    }

    /**
     * Creates a paragraph view with the specified number of rows and height.
     *
     * @param height The height of the paragraph.
     * @param rows The number of rows in the paragraph.
     * @param paragraphWidth The width of the ghost text
     * @return A [LinearLayout] representing the paragraph
     */
    private fun createParagraph(height: Int, rows: Int, paragraphWidth: Int): LinearLayout {
        return LinearLayout(context).apply {
            orientation = VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                height,
            ).apply {
                setMargins(0, paragraphRowTopMargin, 0, 0)
            }
            repeat(rows) {
                addView(
                    View(context).apply {
                        addCornerRadiusAndColor(this)
                        layoutParams = LayoutParams(
                            paragraphWidth,
                            paragraphRowHeight,
                        ).apply {
                            setMargins(0, 50, 0, paragraphRowTopMargin)
                        }
                    },
                )
            }
        }
    }

    /**
     * Sets the gravity for the content of the main linearlayout container.
     *
     * @param gravity The gravity for the root linearlayout.
     * @return The current ShimmerBuilder instance.
     */
    fun setScreenGravity(gravity: Int): ShimmerBuilder {
        shimmerGravity = gravity
        return this
    }

    /**
     * Sets the margin of the elements in the grid.
     *
     * @param margin Margin size.
     * @return The ShimmerBuilder instance.
     */
    fun setGridMargin(margin: Int): ShimmerBuilder {
        gridMargin = dpToPx(context, margin).toInt()
        return this
    }

    /**
     * Sets the border margins for the shimmer effect and returns the ShimmerBuilder instance.
     *
     * @param margin The margin to be set for the border in dp.
     * @return The ShimmerBuilder instance.
     */
    fun setBorderMargins(margin: Int): ShimmerBuilder {
        borderMargins = dpToPx(context, margin).toInt()
        return this
    }

    /**
     * Sets the margin of the elements in the grid.
     *
     * @param rowHeight Paragraph row height.
     * @return The ShimmerBuilder instance.
     */
    fun setParagraphRowHeight(rowHeight: Int): ShimmerBuilder {
        paragraphRowHeight = dpToPx(context, rowHeight).toInt()
        return this
    }

    /**
     * Sets the color of the elements in the ShimmerBuilder.
     *
     * @param color The color to set.
     * @return The ShimmerBuilder instance.
     */
    fun setElementColor(): ShimmerBuilder {
        elementColor = 0x80808080.toInt()
        return this
    }

    /**
     * Sets the top margin of the paragraph rows in the ShimmerBuilder.
     *
     * @param margin The margin to set.
     * @return The ShimmerBuilder instance.
     */
    fun setParagraphRowMargin(margin: Int): ShimmerBuilder {
        paragraphRowTopMargin = ViewUtils.dpToPx(context, margin).toInt()
        return this
    }

    /**
     * Sets the top margin of the blocks in the ShimmerBuilder.
     *
     * @param margin The margin to set.
     * @return The ShimmerBuilder instance.
     */
    fun setBoxMargin(margin: Int): ShimmerBuilder {
        blockTopMargin = dpToPx(context, margin).toInt()
        return this
    }

    /**
     * Sets the corner radius for the shimmer affected view.
     *
     * @param cornerRadius The desired corner radius in pixels.
     * @return Returns the ShimmerBuilder instance.
     */
    fun setCornerRadius(cornerRadius: Float): ShimmerBuilder {
        this.cornerRadius = cornerRadius
        return this
    }

    /**
     * Adds a full width block view with the specified height to the ShimmerBuilder.
     *
     * @param height The height of the block view.
     * @return The ShimmerBuilder instance.
     */
    fun addBlockView(height: Int): ShimmerBuilder {
        linearLayout.addView(createBlockView(0, dpToPx(context, height).toInt()))
        return this
    }

    /**
     * Adds a block with a paragraph to the linear layout with the specified dimensions and alignment.
     * Rows for the paragraph are calculated
     *
     * @param width The width of the block.
     * @param height The height of the block and the paragraph.
     * @return This [ShimmerBuilder] instance.
     */
    fun addBlockWithParagraph(width: Int, height: Int, paragraphWidth: Int): ShimmerBuilder {
        linearLayout.addView(
            createBlockWithParagraph(
                dpToPx(context, width).toInt(),
                dpToPx(context, height).toInt(),
                paragraphWidth
            ),
        )
        return this
    }

    /**
     * Adds a block with a fixed line count to the linear layout and returns the ShimmerBuilder.
     *
     * @param width The width of the block in dp.
     * @param height The height of the block in dp.
     * @param lineCount The number of lines in the block.
     * @return Returns the ShimmerBuilder instance.
     */
    private fun addBlockWithFixedLineItemCount(width: Int, height: Int, lineCount: Int, paragraphWidth: Int): ShimmerBuilder {
        linearLayout.addView(
            createBlockWithFixedLineItemCount(
                dpToPx(context, width).toInt(),
                dpToPx(context, height).toInt(),
                lineCount,
                dpToPx(context, paragraphWidth).toInt(),
            ),
        )
        return this
    }

    /**
     * Adds a grid with the specified number of rows and columns, and cell dimensions to the ShimmerBuilder.
     *
     * @param rows The number of rows in the grid.
     * @param columns The number of columns in the grid.
     * @param cornerRadius The radius of the elements in the grid
     * @return The ShimmerBuilder instance.
     */
    fun addGrid(rows: Int, columns: Int, cornerRadius: Float? = null, divisor: Float? = null): ShimmerBuilder {
        linearLayout.addView(
            createGridLayout(
                context,
                rows,
                columns,
                cornerRadius,
                divisor
            ),
        )
        return this
    }

    /**
     * Adds a grid of circle views to the ShimmerBuilder.
     *
     * This function creates a grid of circle views with a specified number of rows and columns, and adds it to the
     * ShimmerBuilder's linear layout.
     *
     * @param rows The number of rows in the grid.
     * @param columns The number of columns in the grid.
     * @return The current ShimmerBuilder instance.
     */
    fun addCircleGrid(rows: Int, columns: Int): ShimmerBuilder {
        linearLayout.addView(
            createCircleGridLayout(
                context,
                rows,
                columns,
            ),
        )
        return this
    }

    /**
     * Adds a paragraph with the specified number of rows and height to the ShimmerBuilder.
     *
     * @param rows The number of rows in the paragraph.
     * @param height The height of the paragraph.
     * @return The ShimmerBuilder instance.
     */
    fun addParagraph(height: Int, rows: Int, paragraphWidth: Int): ShimmerBuilder {
        linearLayout.addView(createParagraph(dpToPx(context, height).toInt(), rows, paragraphWidth))
        return this
    }

    /**
     * Adds a TextView with the specified width and height to the ShimmerBuilder.
     *
     * @param width The width of the TextView.
     * @param height The height of the TextView.
     * @return The ShimmerBuilder instance.
     */
    fun addTextView(width: Int, height: Int): ShimmerBuilder {
        linearLayout.addView(
            createTextView(
                dpToPx(context, width).toInt(),
                dpToPx(context, height).toInt(),
            ),
        )
        return this
    }

    /**
     * Adds a full width transparent spacer view between elements.
     *
     * @param height The height of the Spacer.
     * @return The ShimmerBuilder instance.
     */
    private fun addVerticalSpacer(height: Int): ShimmerBuilder {
        linearLayout.addView(
            View(context).apply {
                layoutParams = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    dpToPx(context, height).toInt(),
                )
                background = ColorDrawable(Color.TRANSPARENT)
            },
        )
        return this
    }

    /**
     * Adds a set width transparent spacer view between elements.
     *
     * @param width The width of the Spacer.
     * @return The ShimmerBuilder instance.
     */
    private fun addHorizontalSpacer(width: Int): ShimmerBuilder {
        linearLayout.addView(
            View(context).apply {
                layoutParams = LayoutParams(
                    dpToPx(context, width).toInt(),
                    dpToPx(context, 10).toInt(),
                )
                background = ColorDrawable(Color.TRANSPARENT)
            },
        )
        return this
    }

    /**
     * Adds a corner radius and background color to a given view.
     *
     * @param view The view to which the corner radius and background color will be added.
     * @param cornerRadius The corner radius to be applied - default is this builder's cornerRadius.
     */
    private fun addCornerRadiusAndColor(view: View, cornerRadius: Float = this.cornerRadius) {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadii = floatArrayOf(
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
            cornerRadius,
        )
        drawable.setColor(elementColor)
        view.background = drawable
    }

    /**
     * Generates a standard 3x5 grid view.
     */
    fun generateStandard3x5CircleGrid() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.CENTER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(addVerticalSpacer(standardHeaderSpacerLarge).addCircleGrid(5,3).build())
    }

    /**
     * Generates a standard 3x5 grid view.
     */
    fun generateStandard3x5Grid() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.CENTER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        gridMargin = dpToPx(context, 2).toInt()
        borderMargins = 0
        addView(addVerticalSpacer(standardHeaderSpacerLarge).addGrid(5,3, divisor = 3f).build())
    }

    /**
     * Generates a standard 2x5 grid view.
     */
    fun generateStandard2x5Grid() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.CENTER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(addVerticalSpacer(standardHeaderSpacerLarge).addGrid(5,2, divisor = 2f).build())
    }

    /**
     * Generates a standard block with a single line.
     */
    fun generateStandardBlockWithSingleLineAndHeader() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.CENTER
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(addRepeatedBlocks(7).build())
    }

    /**
     * Generates the shimmer layout for the Albums pivot.
     */
    fun generateAlbumsPivotShimmerLayout() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.START
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(
            // Shared albums section
            addSquareTilesSection(1)
                // Your albums section
                .addSquareTilesSection(2)
                .build()
        )
    }

    /**
     * Generate the shimmer layout for the Moments pivot.
     */
    fun generateMomentsPivotShimmerLayout() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.START
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(
            // On this day carousel
            addHeroCarouselSection()
                // Trips section
                .addSquareTilesSection(1)
                .build()
        )
    }

    /**
     * Generate the shimmer layout for the Explore pivot.
     */
    fun generatePivotWithCarouselsShimmerLayout() = MotionViewShimmerLayout(context).apply {
        shimmerGravity = Gravity.START
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        addView(
            addCarouselSection()
                .addCarouselSection()
                .build()
        )
    }

    /**
     * Adds a section with a header followed by a hero carousel (e.g. On this day carousel in Moments pivot).
     *
     * @return The current ShimmerBuilder instance with the added hero carousel section.
     */
    private fun addHeroCarouselSection(): ShimmerBuilder {
        // First add the text header
        addHorizontalSpacer(context.resources.displayMetrics.widthPixels)
            .addTextView(200, 30)

        // Then add the hero carousel
        addVerticalSpacer(15)
        linearLayout.addView(
            FrameLayout(context).apply {
                addCornerRadiusAndColor(this, cornerRadius = dpToPx(context, 16))
                layoutParams = LayoutParams(
                    dpToPx(context, 270).toInt(),
                    dpToPx(context, 338).toInt(),
                ).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                    setMargins(
                        0,
                        dpToPx(context, 8).toInt(),
                        0,
                        dpToPx(context, 8).toInt(),
                    )
                }
            }
        )

        return this
    }

    /**
     * Adds a section with a header followed by a carousel (e.g. sections in the explore pivot).
     */
    private fun addCarouselSection(): ShimmerBuilder {
        // First add the text header
        addHorizontalSpacer(context.resources.displayMetrics.widthPixels)
            .addTextView(200, 30)
            .addVerticalSpacer(15)

        // Then add the carousel
        val cardWidth = dpToPx(context, 230).toInt()
        val firstCardHeight = dpToPx(context, 290).toInt()
        val otherCardHeight = dpToPx(context, 260).toInt()
        val cardMargin = dpToPx(context, 6).toInt()

        val carousel = LinearLayout(context).apply {
            orientation = HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                leftMargin = dpToPx(context, 10).toInt()
            }
        }

        carousel.addView(
            FrameLayout(context).apply {
                addCornerRadiusAndColor(this, cornerRadius = dpToPx(context, 16))
                layoutParams = LayoutParams(cardWidth, firstCardHeight).apply {
                    gravity = Gravity.CENTER_VERTICAL
                    setMargins(cardMargin, 0, cardMargin, 0)
                }
            }
        )

        val screenWidth = context.resources.displayMetrics.widthPixels
        val cardWidthWithMargins = cardWidth + cardMargin * 2
        var remainingWidth = screenWidth - cardWidthWithMargins
        while (remainingWidth > 0) {
            carousel.addView(
                FrameLayout(context).apply {
                    addCornerRadiusAndColor(this, cornerRadius = dpToPx(context, 16))
                    layoutParams = LayoutParams(cardWidth, otherCardHeight).apply {
                        gravity = Gravity.CENTER_VERTICAL
                        setMargins(cardMargin, 0, cardMargin, 0)
                    }
                }
            )
            remainingWidth -= cardWidthWithMargins
        }

        linearLayout.addView(carousel)
        addVerticalSpacer(24)
        return this
    }

    /**
     * Add a section with a header followed by [rowsOfTiles] rows of 2 square rounded tiles.
     *
     * @param rowsOfTiles The number of rows of tiles to add.
     * @return The current ShimmerBuilder instance with the added square tiles section.
     */
    private fun addSquareTilesSection(rowsOfTiles: Int): ShimmerBuilder {
        // First add the text header
        addHorizontalSpacer(context.resources.displayMetrics.widthPixels)
            .addTextView(200, 30)
            .addVerticalSpacer(10)

        // Then add n rows of square rounded tiles
        repeat(rowsOfTiles) {
            addVerticalSpacer(5)
            .addGrid(1, 2, 50f, 2.1f)
        }

        return this
    }

    /**
     * Adds a specified number of blocks with a fixed line item count.
     */
    private fun addRepeatedBlocks(count: Int): ShimmerBuilder {
        repeat(count) { addBlockWithFixedLineItemCount(70, 70, 1, 400) }
        return this
    }

    /**
     * Initiates the enter transition for the motion view.
     */
    fun enter() {
        MotionViewBase(motionViewBase = linearLayout, motionValues = motionValues).enter()
    }

    /**
     * Initiates the exit transition for the motion view.
     */
    fun exit() {
        MotionViewBase(motionViewBase = linearLayout, motionValues = motionValues).exit()
    }

    /**
     * Builds the ShimmerFrameLayout with the specified settings.
     *
     * @return The created ShimmerFrameLayout.
     */
    fun build(): View {
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
        ).apply {
            setMargins(borderMargins, borderMargins, borderMargins, borderMargins)
        }
        linearLayout.layoutParams = params
        linearLayout.gravity = shimmerGravity
        addView(linearLayout)
        return this
    }
}