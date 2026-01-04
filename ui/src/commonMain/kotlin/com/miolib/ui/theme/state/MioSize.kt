package com.miolib.ui.theme.state

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class MioSizes(val small: SizeSpec, val medium: SizeSpec, val large: SizeSpec)

enum class MioSize { Small, Medium, Large }

@Immutable
data class SizeSpec(val height: Dp, val padding: Dp, val fontSize: TextUnit, val iconSize: Dp)

val DesktopSizes = MioSizes(
    small = SizeSpec(28.dp, 10.dp, 12.sp, 14.dp),
    medium = SizeSpec(36.dp, 16.dp, 14.sp, 18.dp),
    large = SizeSpec(44.dp, 24.dp, 16.sp, 22.dp)
)

val AndroidSizes = MioSizes(
    small = SizeSpec(36.dp, 12.dp, 12.sp, 16.dp),
    medium = SizeSpec(48.dp, 20.dp, 16.sp, 24.dp),
    large = SizeSpec(56.dp, 28.dp, 18.sp, 28.dp)
)