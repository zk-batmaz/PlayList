package com.qbra.playlist.presentation.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import com.qbra.playlist.R

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit // Animasyon bitince MainActivity'ye  yönlendir
) {
    // Animasyonun başlangıç değeri
    val scale = remember { Animatable(0f) }

    // Ekran açıldığı an çalışacak olan Coroutine bloğu
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f, // Gerçek boyutuna büyü
            animationSpec = tween(
                durationMillis = 1000, // 1 saniye sürecek
                easing = { OvershootInterpolator(2f).getInterpolation(it) } // Sona geldiğinde hafifçe dışarı taşıp yaylan
            )
        )
        delay(500L)

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "PlayList Logo",
            modifier = Modifier
                .size(200.dp)
                .scale(scale.value)
        )
    }
}