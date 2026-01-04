package com.miolib.ui.theme.state

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp


@Immutable
data class MioShapes(val cornerSmall: Shape, val cornerMedium: Shape, val cornerLarge: Shape)

val RoundedShapes = MioShapes(
    cornerSmall = RoundedCornerShape(6.dp),
    cornerMedium = RoundedCornerShape(12.dp),
    cornerLarge = RoundedCornerShape(20.dp)
)

val SquareShapes = MioShapes(
    cornerSmall = RoundedCornerShape(0.dp),
    cornerMedium = RoundedCornerShape(2.dp),
    cornerLarge = RoundedCornerShape(0.dp)
)