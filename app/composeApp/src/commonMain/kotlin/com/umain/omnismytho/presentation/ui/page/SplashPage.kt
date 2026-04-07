package com.umain.omnismytho.presentation.ui.page

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umain.omnismytho.presentation.ui.atom.OmDivider
import androidx.compose.ui.tooling.preview.Preview
import com.umain.omnismytho.presentation.ui.preview.OmPreviewSurface
import kotlinx.coroutines.delay

@Composable
fun SplashPage(onNavigateToHome: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(2500)
        onNavigateToHome()
    }

    val titleAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 200),
    )
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 600),
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, delayMillis = 1000),
    )

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "OMNIS MYTHO",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 6.sp,
                modifier = Modifier.alpha(titleAlpha),
            )
            Text(
                text = "Encyclopaedia of the Divine & Profane",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(subtitleAlpha),
            )
            OmDivider(modifier = Modifier.width(200.dp).alpha(subtitleAlpha))
            Text(
                text = "ab origine mundi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 3.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.alpha(taglineAlpha),
            )
        }
    }
}

@Preview
@Composable
private fun SplashPagePreview() {
    OmPreviewSurface {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text("OMNIS MYTHO", style = MaterialTheme.typography.displayLarge, color = MaterialTheme.colorScheme.primary, letterSpacing = 6.sp)
                Text("Encyclopaedia of the Divine & Profane", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 2.sp)
                OmDivider(modifier = Modifier.width(200.dp))
                Text("ab origine mundi", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, letterSpacing = 3.sp, fontStyle = FontStyle.Italic)
            }
        }
    }
}
