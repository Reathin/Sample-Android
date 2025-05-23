package com.reathin.composedrag.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.sin

class PolygonShape(private val sides: Int) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            path = Path().apply {
                val radius = size.minDimension / 2
                val angle = 2.0 * Math.PI / sides
                moveTo(
                    x = radius * cos(0.0).toFloat() + size.width / 2,
                    y = radius * sin(0.0).toFloat() + size.height / 2
                )
                for (i in 1 until sides) {
                    lineTo(
                        x = radius * cos(angle * i).toFloat() + size.width / 2,
                        y = radius * sin(angle * i).toFloat() + size.height / 2
                    )
                }
                close()
            }
        )
    }
}