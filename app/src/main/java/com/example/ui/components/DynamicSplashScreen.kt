package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.i18n.strRes
import com.example.ui.theme.AppThemeOption

@Composable
fun DynamicSplashScreen(
    activeTheme: AppThemeOption,
    modifier: Modifier = Modifier
) {
    val bgColor = if (activeTheme.isDark) Color.Black else MaterialTheme.colorScheme.background
    val surfaceColor = if (activeTheme.isDark) Color(0xFF141418) else MaterialTheme.colorScheme.surface
    val primaryColor = MaterialTheme.colorScheme.primary
    val onBgColor = if (activeTheme.isDark) Color.White else MaterialTheme.colorScheme.onBackground
    val mutedColor = if (activeTheme.isDark) Color(0xFFA1A1AA) else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("dynamic_splash_screen")
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Surface(
                color = surfaceColor,
                shape = CircleShape,
                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                tonalElevation = 0.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bolt,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = strRes(activeTheme.titleRes).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = primaryColor.copy(alpha = if (activeTheme.isDark) 0.14f else 0.08f),
                shape = CircleShape,
                border = BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.30f)),
                modifier = Modifier.size(236.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AnimatedKocTavuk(
                        size = 212.dp,
                        mood = CoachAnimationMood.CHEERING,
                        loopDemo = false,
                        showAura = false,
                        contentDescription = strRes(R.string.app_name)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = strRes(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold,
                color = onBgColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = strRes(R.string.splash_tagline),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = mutedColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth(0.46f)
                    .height(5.dp)
                    .clip(CircleShape),
                color = primaryColor,
                trackColor = surfaceColor
            )
        }
    }
}
