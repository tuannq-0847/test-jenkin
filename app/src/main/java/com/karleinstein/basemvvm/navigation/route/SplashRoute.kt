package com.karleinstein.basemvvm.navigation.route

import androidx.compose.runtime.Composable
import com.karleinstein.basemvvm.ui.screen.SplashScreen

@Composable
fun SplashRoute(onClickContinue: () -> Unit) {
    SplashScreen(onClickContinue = onClickContinue)
}
