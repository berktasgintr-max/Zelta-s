package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudUpload
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.ui.components.AnimatedKocTavuk
import com.example.ui.components.CleanTextField
import com.example.ui.components.CoachAnimationMood
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.model.ChickenBadges
import com.example.firebase.GoogleSignInButton
import com.example.firebase.signOut
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.LocalLocalizedContext
import com.example.ui.i18n.strRes
import com.example.ui.theme.AppThemeOption
import com.example.ui.viewmodel.MainUiState

@Composable
fun ProfileAndProScreen(
    uiState: MainUiState,
    onSelectLanguage: (String) -> Unit,
    onSelectGender: (String) -> Unit,
    onSelectTheme: (AppThemeOption) -> Unit,
    onUpgradePro: () -> Unit,
    onDowngradeFree: () -> Unit,
    onRedeemFriendCode: (String) -> Unit,
    onRefreshReferrals: () -> Unit,
    onRestartQuiz: () -> Unit,
    onMessage: (String) -> Unit
) {
    val localizedCtx = LocalLocalizedContext.current
    val profile = uiState.profile
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var friendCodeInput by remember { mutableStateOf("") }
    val currentLanguage = AppLanguage.fromCode(profile.appLanguage)

    val flatElevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    val surfaceCardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    val primaryBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Mascot Rank, XP Progress & Badges Showcase
        item(key = "profile_badges", contentType = "badges_card") {
            val badges = remember(profile, uiState.calorieLogs.size) {
                ChickenBadges.evaluate(profile, uiState.calorieLogs.size)
            }
            val unlockedCount = badges.count { it.unlocked }
            var badgesExpanded by remember { mutableStateOf(false) }

            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_badges_card")
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AnimatedKocTavuk(
                            size = 72.dp,
                            mood = CoachAnimationMood.CHEERING,
                            showAura = false,
                            contentDescription = strRes(profile.chickenRankTitleRes())
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${strRes(R.string.level_prefix)}${profile.chickenLevel} • ${strRes(profile.chickenRankTitleRes())}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strRes(R.string.badges_section_title),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strRes(R.string.badges_progress_fmt, unlockedCount, badges.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "${profile.totalXp} XP",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${profile.xpInCurrentLevel} / 100 XP",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { (profile.xpInCurrentLevel / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.background
                        )
                    }

                    OutlinedButton(
                        onClick = { badgesExpanded = !badgesExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (badgesExpanded) {
                                strRes(R.string.badges_hide_btn)
                            } else {
                                strRes(R.string.badges_show_fmt, badges.size)
                            }
                        )
                    }

                    if (badgesExpanded) {
                        badges.chunked(2).forEach { rowPair ->
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                rowPair.forEach { badge ->
                                    val icon = when (badge.id) {
                                        "flame_chick" -> Icons.Rounded.LocalFireDepartment
                                        "smart_nutrition" -> Icons.Rounded.Restaurant
                                        else -> Icons.Rounded.EmojiEvents
                                    }
                                    Surface(
                                        color = if (badge.unlocked) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                        } else {
                                            MaterialTheme.colorScheme.background
                                        },
                                        shape = MaterialTheme.shapes.medium,
                                        border = BorderStroke(
                                            1.dp,
                                            if (badge.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
                                            else MaterialTheme.colorScheme.outline
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .alpha(if (badge.unlocked) 1f else 0.62f)
                                            .testTag("badge_tile_${badge.id}")
                                    ) {
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.padding(12.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Icon(
                                                    imageVector = if (badge.unlocked) icon else Icons.Rounded.Lock,
                                                    contentDescription = null,
                                                    tint = if (badge.unlocked) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = strRes(if (badge.unlocked) R.string.badge_earned_tag else R.string.badge_locked_tag),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (badge.unlocked) MaterialTheme.colorScheme.primary
                                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = badge.title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = badge.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Language Selector (All 10 European Languages)
        item(key = "profile_language", contentType = "language_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(strRes(R.string.settings_language_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(strRes(R.string.settings_language_sub), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    AppLanguage.entries.chunked(3).forEach { triplet ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            triplet.forEach { lang ->
                                SettingPillChoice(
                                    label = lang.nativeName,
                                    selected = currentLanguage == lang,
                                    onClick = { onSelectLanguage(lang.code) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("profile_lang_${lang.code}")
                                )
                            }
                            // Add empty placeholders if row has fewer than 3 items
                            repeat(3 - triplet.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }

        // Gender Setting
        item(key = "profile_gender", contentType = "gender_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (profile.gender == "female") Icons.Rounded.Female else Icons.Rounded.Male,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(strRes(R.string.settings_gender_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(strRes(R.string.settings_gender_sub), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        SettingPillChoice(
                            label = strRes(R.string.male_label),
                            selected = profile.gender != "female",
                            onClick = { onSelectGender("male") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_gender_male")
                        )
                        SettingPillChoice(
                            label = strRes(R.string.female_label),
                            selected = profile.gender == "female",
                            onClick = { onSelectGender("female") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_gender_female")
                        )
                    }
                }
            }
        }

        // Theme Studio
        item(key = "profile_theme_studio", contentType = "theme_card") {
            val currentTheme = AppThemeOption.fromId(profile.themeId)
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${strRes(R.string.theme_studio_title)} (${strRes(currentTheme.titleRes)})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strRes(R.string.theme_studio_sub),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(AppThemeOption.entries, key = { it.id }, contentType = { "theme_swatch" }) { option ->
                            val selected = option == currentTheme
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                        else MaterialTheme.colorScheme.background
                                    )
                                    .clickable { onSelectTheme(option) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                                    .testTag("profile_theme_${option.id}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(option.previewBg)
                                ) {
                                    if (selected) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = option.previewPrimary, modifier = Modifier.size(18.dp))
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(option.previewPrimary)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = strRes(option.titleRes),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cloud Sign-In Section
        item(key = "profile_cloud_signin", contentType = "cloud_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.currentUser != null) Icons.Rounded.CloudDone else Icons.Rounded.CloudUpload,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strRes(if (uiState.currentUser != null) R.string.cloud_synced_title else R.string.cloud_save_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.currentUser != null) {
                        Text(
                            text = "${strRes(R.string.connected_email_prefix)}: ${uiState.currentUser.email ?: uiState.currentUser.uid}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val signedOutMsg = strRes(R.string.signed_out_local_msg)
                        OutlinedButton(
                            onClick = {
                                signOut(credentialManager, scope) {
                                    onMessage(signedOutMsg)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(strRes(R.string.sign_out))
                        }
                    } else {
                        Text(
                            text = strRes(R.string.cloud_save_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        val signedInMsg = strRes(R.string.signed_in_cloud_msg)
                        GoogleSignInButton(
                            label = strRes(R.string.google_signin_button),
                            modifier = Modifier.fillMaxWidth(),
                            onAuthSuccess = {
                                onMessage(signedInMsg)
                            },
                            onAuthError = { err -> onMessage(err) }
                        )
                    }
                }
            }
        }

        // Referral Reward Program
        item(key = "profile_referrals", contentType = "referral_card") {
            val count = profile.referralCount.coerceAtMost(5)
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CardGiftcard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strRes(R.string.invite_5_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = strRes(R.string.invite_5_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strRes(R.string.invite_progress_fmt, count),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onRefreshReferrals) {
                            Icon(Icons.Rounded.Refresh, contentDescription = strRes(R.string.common_refresh), tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    LinearProgressIndicator(
                        progress = { count / 5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.background
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = strRes(R.string.your_invite_code_fmt, profile.inviteCode),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            clipboard.setText(AnnotatedString(profile.inviteCode))
                            onMessage(localizedCtx.getString(R.string.invite_copied_fmt, profile.inviteCode))
                        }) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = strRes(R.string.common_copy))
                        }
                        IconButton(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    localizedCtx.getString(R.string.share_invite_text_fmt, profile.inviteCode)
                                )
                            }
                            context.startActivity(
                                Intent.createChooser(sendIntent, localizedCtx.getString(R.string.share_invite_chooser))
                            )
                        }) {
                            Icon(Icons.Rounded.Share, contentDescription = strRes(R.string.common_share))
                        }
                    }

                    if (profile.referredByCode.isBlank()) {
                        CleanTextField(
                            value = friendCodeInput,
                            onValueChange = { friendCodeInput = it.uppercase().take(24) },
                            label = strRes(R.string.friend_code_label),
                            placeholder = "FIT-AB12C",
                            singleLine = true,
                            inputModifier = Modifier.testTag("friend_invite_code_input")
                        )
                        Button(
                            onClick = { onRedeemFriendCode(friendCodeInput) },
                            enabled = friendCodeInput.trim().length >= 4,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(strRes(R.string.apply_friend_code))
                        }
                    } else {
                        Text(
                            text = strRes(R.string.applied_code_fmt, profile.referredByCode),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Plan Comparison & Management
        item(key = "profile_plan", contentType = "plan_card") {
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strRes(if (profile.isProActive) R.string.current_plan_pro else R.string.current_plan_free),
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    Text(
                        text = strRes(if (profile.isProActive) R.string.plan_pro_desc else R.string.plan_free_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!profile.isProActive) {
                        Button(
                            onClick = onUpgradePro,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("upgrade_pro_button")
                        ) {
                            Icon(Icons.Rounded.Verified, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(strRes(R.string.upgrade_to_pro))
                        }
                    } else {
                        OutlinedButton(onClick = onDowngradeFree, modifier = Modifier.fillMaxWidth()) {
                            Text(strRes(R.string.switch_to_free))
                        }
                    }
                }
            }
        }

        // Physical Summary & Retake Quiz
        item(key = "profile_summary", contentType = "summary_card") {
            val genderLabel = strRes(if (profile.gender == "female") R.string.female_label else R.string.male_label)
            val envDisplay = when (profile.environment.lowercase()) {
                "hybrid" -> strRes(R.string.env_hybrid)
                "home" -> strRes(R.string.cat_home)
                "gym" -> strRes(R.string.cat_gym)
                else -> profile.environment
            }
            Card(
                colors = surfaceCardColors,
                shape = MaterialTheme.shapes.large,
                border = primaryBorder,
                elevation = flatElevation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = strRes(R.string.physical_profile_summary),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = buildString {
                            append("${profile.displayName} • $genderLabel • $envDisplay\n")
                            append(
                                strRes(
                                    R.string.physical_summary_fmt,
                                    profile.age,
                                    profile.heightCm,
                                    profile.currentWeightKg.toString(),
                                    profile.targetWeightKg.toString(),
                                    profile.weeklyDays
                                )
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(onClick = onRestartQuiz, modifier = Modifier.fillMaxWidth()) {
                        Text(strRes(R.string.settings_restart_quiz))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingPillChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(48.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                else MaterialTheme.colorScheme.background
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        if (selected) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
