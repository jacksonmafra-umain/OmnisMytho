package com.umain.omnismytho.presentation.ui.atom

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface

@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 16.dp,
    cornerRadius: Dp = 8.dp,
) {
    val shimmerColors =
        listOf(
            MaterialTheme.colorScheme.surface,
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.surface,
        )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmerTranslate",
    )

    val brush =
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset(translateAnim - 200f, 0f),
            end = Offset(translateAnim + 200f, 0f),
        )

    Box(
        modifier =
            modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(cornerRadius))
                .background(brush),
    )
}

@Composable
fun ShimmerCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ShimmerBox(width = Dp.Unspecified, height = 100.dp, modifier = Modifier.fillMaxWidth())
        ShimmerBox(width = 120.dp, height = 14.dp)
        ShimmerBox(width = 80.dp, height = 10.dp)
    }
}

@Preview
@Composable
private fun ShimmerBoxPreview() {
    OmPreviewSurface {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ShimmerBox(width = 200.dp, height = 20.dp)
            ShimmerBox(width = 150.dp, height = 14.dp)
            ShimmerBox(width = 100.dp, height = 10.dp)
        }
    }
}

@Preview
@Composable
private fun ShimmerCardPreview() {
    OmPreviewSurface {
        ShimmerCard(modifier = Modifier.width(160.dp))
    }
}
