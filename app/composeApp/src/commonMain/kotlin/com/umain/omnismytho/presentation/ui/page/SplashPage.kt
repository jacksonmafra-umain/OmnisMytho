package com.umain.omnismytho.presentation.ui.page

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umain.omnismytho.presentation.ui.atom.OmDivider
import kotlinx.coroutines.delay

@Composable
fun SplashPage(onNavigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "OMNIS MYTHO",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 6.sp,
            )
            Text(
                text = "Encyclopaedia of the Divine & Profane",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 2.sp,
            )
            OmDivider(modifier = Modifier.width(200.dp))
            Text(
                text = "ab origine mundi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 3.sp,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}
