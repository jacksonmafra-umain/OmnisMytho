package com.umain.omnismytho

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.umain.omnismytho.presentation.navigation.AppNavGraph
import com.umain.omnismytho.presentation.ui.theme.OmnisMythoTheme

@Composable
fun App() {
    OmnisMythoTheme {
        val navController = rememberNavController()
        AppNavGraph(navController = navController)
    }
}
