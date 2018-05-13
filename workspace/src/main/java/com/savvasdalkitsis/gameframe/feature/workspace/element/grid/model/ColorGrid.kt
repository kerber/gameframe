/**
 * Copyright 2017 Savvas Dalkitsis
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 'Game Frame' is a registered trademark of LEDSEQ
 */
package com.savvasdalkitsis.gameframe.feature.workspace.element.grid.model

import android.graphics.Color
import android.support.annotation.ColorInt
import com.savvasdalkitsis.gameframe.feature.bitmap.model.Bitmap
import com.savvasdalkitsis.gameframe.feature.composition.CompositionInjector
import com.savvasdalkitsis.gameframe.feature.composition.model.AvailableBlendMode
import com.savvasdalkitsis.gameframe.feature.composition.model.AvailablePorterDuffOperator
import com.savvasdalkitsis.gameframe.kotlin.clip

class ColorGrid : Grid {

    private val colors = Array(Grid.SIDE) { IntArray(Grid.SIDE) }
    private var translateCol: Int = 0
    private var translateRow: Int = 0

    @Transient
    private val blendUseCase = CompositionInjector.blendUseCase()
    @Transient
    private var transientTranslateCol: Int = 0
    @Transient
    private var transientTranslateRow: Int = 0
    @Transient
    private var scratch: ColorGrid? = null

    init {
        fill(Color.TRANSPARENT)
    }

    override fun setColor(@ColorInt color: Int, column: Int, row: Int) {
        checkValue(column, "Column")
        checkValue(row, "Row")
        val c = column - columnTranslation
        val r = row - rowTranslation
        if (!isOutOfBounds(c, r)) {
            colors[c - 1][r - 1] = color
        }
    }

    override fun fill(color: Int): ColorGrid {
        for (i in 1..Grid.SIDE) {
            for (j in 1..Grid.SIDE) {
                setColor(color, i, j)
            }
        }
        return this
    }

    @ColorInt
    override fun getColor(column: Int, row: Int): Int {
        checkValue(column, "Column")
        checkValue(row, "Row")
        val c = column - columnTranslation
        val r = row - rowTranslation
        if (isOutOfBounds(c, r)) {
            return Color.TRANSPARENT
        }
        val color = colors[c - 1][r - 1]
        return if (scratch == null) {
            color
        } else {
            blendUseCase.mix(scratch!!.getColor(column, row), color, AvailableBlendMode.NORMAL, AvailablePorterDuffOperator.SOURCE_OVER, 1f).color()
        }
    }

    override fun replicateMoment(): ColorGrid {
        val colorGrid = ColorGrid()
        colorGrid.copyColorsFrom(this)
        colorGrid.translateCol = translateCol
        colorGrid.translateRow = translateRow
        return colorGrid
    }

    override fun isIdenticalTo(moment: Grid): Boolean {
        if (moment !is ColorGrid) {
            return false
        }
        if (translateCol != moment.translateCol || translateRow != moment.translateRow) {
            return false
        }
        for (i in 0 until Grid.SIDE) {
            for (j in 0 until Grid.SIDE) {
                if (colors[i][j] != moment.colors[i][j]) {
                    return false
                }
            }
        }
        return true
    }

    private fun checkValue(value: Int, valueName: String) {
        if (value < 1 || value > Grid.SIDE) {
            throw IllegalArgumentException("$valueName value should be between 1 and ${Grid.SIDE} but was $value")
        }
    }

    fun translate(translateCol: Int, translateRow: Int) {
        transientTranslateCol = translateCol
        transientTranslateRow = translateRow
    }

    fun freezeTranslation() {
        translateCol = bound(columnTranslation)
        translateRow = bound(rowTranslation)
        transientTranslateCol = 0
        transientTranslateRow = 0
    }

    val rowTranslation: Int
        get() = translateRow + transientTranslateRow

    val columnTranslation: Int
        get() = translateCol + transientTranslateCol

    fun initializeScratch(): Grid {
        scratch = ColorGrid()
        return scratch as ColorGrid
    }

    fun rasterScratch() {
        if (scratch != null) {
            val scratch = this.scratch as ColorGrid
            this.scratch = null
            val bitmap = blendUseCase.compose(this.asBitmap(), scratch.asBitmap(), AvailableBlendMode.NORMAL, AvailablePorterDuffOperator.SOURCE_OVER, 1f)
            // TODO this copy is wasteful now that Bitmap is introduced. Maybe back the ColorGrid with a Bitmap and move the copy in there
            copyColorsFrom(from(bitmap))
        }
    }

    private fun copyColorsFrom(grid: ColorGrid) {
        for (i in 0 until Grid.SIDE) {
            System.arraycopy(grid.colors[i], 0, colors[i], 0, Grid.SIDE)
        }
    }

    private fun bound(value: Int): Int = value.clip(-Grid.SIDE, Grid.SIDE)

    companion object {

        fun isOutOfBounds(column: Int, row: Int): Boolean {
            return row < 1 || column < 1 || row > Grid.SIDE || column > Grid.SIDE
        }

        fun from(bitmap: Bitmap) = ColorGrid().apply {
            val (w, h) = bitmap.dimensions
            if (w != Grid.SIDE || h != Grid.SIDE)
                throw IllegalArgumentException("Cannot convert bitmap to ColorGrid if the bitmap is not a square of side ${Grid.SIDE}. Bitmap dimensions were $w:$h")
            for (col in 1..Grid.SIDE) {
                for (row in 1..Grid.SIDE) {
                    setColor(bitmap.getPixelAt(col - 1, row - 1), col, row)
                }
            }
        }
    }
}
