package com.example.ui.components

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeNavy
import com.example.ui.theme.PrimeSurfaceBorder
import com.example.ui.theme.PrimeSurfaceCard
import com.example.ui.theme.PrimeTextMuted
import com.example.ui.theme.PrimeTextWhite
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

/**
 * Non-intrusive AdMob banner component designed to preserve a high-end streaming UX.
 * Integrates natively into content feeds without blocking the player or interrupting audio.
 */
@Composable
fun AdMobInlineBanner(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-3940256099942544/6300978111" // Google Official Sample Banner ID
) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            MobileAds.initialize(context) {}
        } catch (_: Throwable) {
            hasError = true
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("admob_inline_banner"),
        shape = RoundedCornerShape(10.dp),
        color = PrimeSurfaceCard,
        border = BorderStroke(1.dp, PrimeSurfaceBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Discreet "Publicidade" Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp, start = 4.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, PrimeSurfaceBorder)
                ) {
                    Text(
                        text = "PUBLICIDADE",
                        color = PrimeTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "Apoia o app gratuito",
                    color = PrimeTextMuted.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!hasError) {
                    // Real Google Mobile Ads (AdMob) AdView wrapped safely
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { ctx ->
                            val frameLayout = FrameLayout(ctx).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                )
                            }
                            try {
                                val adView = AdView(ctx).apply {
                                    setAdSize(AdSize.BANNER)
                                    setAdUnitId(adUnitId)
                                    adListener = object : AdListener() {
                                        override fun onAdLoaded() {
                                            isAdLoaded = true
                                            hasError = false
                                        }

                                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                            hasError = true
                                        }
                                    }
                                    loadAd(AdRequest.Builder().build())
                                }
                                frameLayout.addView(adView)
                            } catch (_: Throwable) {
                                hasError = true
                            }
                            frameLayout
                        }
                    )
                }

                // Elegant native sponsor card if ad takes time to load, fails or device is offline
                if (hasError || !isAdLoaded) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = PrimeBlue.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, PrimeBlue.copy(alpha = 0.4f)),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = PrimeBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Brasil TV & Rádios Web",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Transmissões ao vivo 100% gratuitas e sem travas",
                                    color = PrimeTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PrimeBlue,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(
                                text = "HD",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
