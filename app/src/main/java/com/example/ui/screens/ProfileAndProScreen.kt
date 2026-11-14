package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.Female
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Male
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.firebase.GoogleSignInButton
import com.example.firebase.signOut
import com.example.ui.components.FemaleTopPink
import com.example.ui.components.MaleShirtBlue
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.AppThemeOption
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.theme.WarmGold
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
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    val profile = uiState.profile
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var friendCodeInput by remember { mutableStateOf("") }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // 0A. Language Selector (Türkçe / English)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Language, contentDescription = null, tint = ext.tealText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(strings.settingsLanguageTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(strings.settingsLanguageSub, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        val trSelected = profile.appLanguage != "en"
                        val enSelected = profile.appLanguage == "en"
                        SettingPillChoice(
                            label = "Türkçe",
                            selected = trSelected,
                            accent = MaterialTheme.colorScheme.primary,
                            onClick = { onSelectLanguage("tr") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_lang_tr")
                        )
                        SettingPillChoice(
                            label = "English",
                            selected = enSelected,
                            accent = MaterialTheme.colorScheme.primary,
                            onClick = { onSelectLanguage("en") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_lang_en")
                        )
                    }
                }
            }
        }

        // 0B. Athlete Character / Gender Setting (Erkek / Kadın)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (profile.gender == "female") Icons.Rounded.Female else Icons.Rounded.Male,
                            contentDescription = null,
                            tint = if (profile.gender == "female") FemaleTopPink else MaleShirtBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(strings.settingsGenderTitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(strings.settingsGenderSub, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        SettingPillChoice(
                            label = strings.maleLabel,
                            selected = profile.gender != "female",
                            accent = MaleShirtBlue,
                            onClick = { onSelectGender("male") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_gender_male")
                        )
                        SettingPillChoice(
                            label = strings.femaleLabel,
                            selected = profile.gender == "female",
                            accent = FemaleTopPink,
                            onClick = { onSelectGender("female") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("profile_gender_female")
                        )
                    }
                }
            }
        }

        // 0C. Tema Stüdyosu (8 Selectable App Themes including Saf Beyaz)
        item {
            val currentTheme = AppThemeOption.fromId(profile.themeId)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), MaterialTheme.shapes.large)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "${strings.themeStudioTitle} (${currentTheme.title(strings.langCode)})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.themeStudioSub,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(AppThemeOption.entries) { option ->
                            val selected = option == currentTheme
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = if (selected) 2.dp else 1.dp,
                                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .clickable { onSelectTheme(option) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("profile_theme_${option.id}")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(option.previewBg)
                                        .border(2.dp, option.previewPrimary, CircleShape)
                                ) {
                                    if (selected) {
                                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = option.previewPrimary, modifier = Modifier.size(18.dp))
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(option.previewSecondary)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = option.title(strings.langCode),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. Optional Email / Google Sign-In Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (uiState.currentUser != null) ext.mintText.copy(alpha = 0.6f) else ext.tealText.copy(alpha = 0.5f),
                        MaterialTheme.shapes.large
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (uiState.currentUser != null) Icons.Rounded.CloudDone else Icons.Rounded.CloudUpload,
                            contentDescription = null,
                            tint = if (uiState.currentUser != null) ext.mintText else ext.tealText
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.currentUser != null) {
                                if (isEn) "Account Synced with Cloud" else "Hesabın Bulutla Eşitleniyor"
                            } else strings.cloudSaveTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.currentUser != null) {
                        Text(
                            text = "${if (isEn) "Connected Email" else "Bağlı E-posta"}: ${uiState.currentUser.email ?: uiState.currentUser.uid}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ext.mintText
                        )
                        OutlinedButton(
                            onClick = {
                                signOut(credentialManager, scope) {
                                    onMessage(
                                        if (isEn) "Signed out. Your local data remains safely on device."
                                        else "Hesaptan çıkış yapıldı. Verilerin cihazda kalmaya devam ediyor."
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isEn) "Sign Out" else "Çıkış Yap")
                        }
                    } else {
                        Text(
                            text = strings.cloudSaveDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        GoogleSignInButton(
                            label = strings.googleSignInButton,
                            modifier = Modifier.fillMaxWidth(),
                            onAuthSuccess = {
                                onMessage(
                                    if (isEn) "Signed in! All data backed up to cloud."
                                    else "Giriş başarılı! Tüm verilerin buluta kaydedildi."
                                )
                            },
                            onAuthError = { err -> onMessage(err) }
                        )
                    }
                }
            }
        }

        // 2. Referral Reward Program: 5 Friends -> 1 Month Free Pro
        item {
            val count = profile.referralCount.coerceAtMost(5)
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, ext.orangeText.copy(alpha = 0.5f), MaterialTheme.shapes.large)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.CardGiftcard, contentDescription = null, tint = ext.goldText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) "Invite 5 Friends = 1 Month Free PRO!" else "5 Arkadaş Davet Et = 1 Ay Ücretsiz PRO!",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ext.goldText
                        )
                    }
                    Text(
                        text = if (isEn) {
                            "Access PRO without paying by inviting 5 friends. Each friend who signs up and enters your code counts as 1 invite—reach 5/5 to unlock 1 full month of PRO!"
                        } else {
                            "Para ödemeden PRO Plan'a erişmek için 5 arkadaşını davet et. Arkadaşların uygulamaya kaydolup senin davet kodunu girdiğinde 1 kişi davet edilmiş sayılır; 5/5 olunca 1 ay ücretsiz PRO tanımlanır!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isEn) "Invite Progress: $count / 5 Friends" else "Davet İlerlemen: $count / 5 Arkadaş",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = onRefreshReferrals) {
                            Icon(Icons.Rounded.Refresh, contentDescription = if (isEn) "Refresh" else "Yenile", tint = ext.tealText)
                        }
                    }

                    LinearProgressIndicator(
                        progress = { count / 5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = ext.mintText,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isEn) "Your Invite Code: ${profile.inviteCode}" else "Senin Davet Kodun: ${profile.inviteCode}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ext.orangeText,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            clipboard.setText(AnnotatedString(profile.inviteCode))
                            onMessage(
                                if (isEn) "Invite code copied: ${profile.inviteCode}"
                                else "Davet kodun kopyalandı: ${profile.inviteCode}"
                            )
                        }) {
                            Icon(Icons.Rounded.ContentCopy, contentDescription = if (isEn) "Copy" else "Kopyala")
                        }
                        IconButton(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    if (isEn) {
                                        "Join my fitness journey on Koç Tavuk! Sign up and enter my invite code: ${profile.inviteCode}"
                                    } else {
                                        "Koç Tavuk ile fitness yolculuğuma katıl! Uygulamaya kaydolup davet kodumu gir: ${profile.inviteCode}"
                                    }
                                )
                            }
                            context.startActivity(Intent.createChooser(sendIntent, if (isEn) "Share Invite Code" else "Davet Kodunu Paylaş"))
                        }) {
                            Icon(Icons.Rounded.Share, contentDescription = if (isEn) "Share" else "Paylaş")
                        }
                    }

                    if (profile.referredByCode.isBlank()) {
                        OutlinedTextField(
                            value = friendCodeInput,
                            onValueChange = { friendCodeInput = it.uppercase().take(24) },
                            label = { Text(if (isEn) "Enter a friend's invite code" else "Bir arkadaşının davet kodunu gir") },
                            placeholder = { Text("Örn: TAVUK-AB12C") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("friend_invite_code_input")
                        )
                        Button(
                            onClick = { onRedeemFriendCode(friendCodeInput) },
                            enabled = friendCodeInput.trim().length >= 4,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (isEn) "Apply Friend Code" else "Arkadaş Kodunu Onayla")
                        }
                    } else {
                        Text(
                            text = if (isEn) "Applied Invite Code: ${profile.referredByCode}" else "Kullanılan Davet Kodu: ${profile.referredByCode}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = ext.mintText
                        )
                    }
                }
            }
        }

        // 3. Plan Comparison & Management
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Star, contentDescription = null, tint = ext.goldText)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (profile.isProActive) {
                                if (isEn) "Current Plan: PRO PLAN" else "Mevcut Plan: PRO PLAN"
                            } else {
                                if (isEn) "Current Plan: FREE PLAN" else "Mevcut Plan: ÜCRETSİZ PLAN"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = if (profile.isProActive) {
                            if (isEn) "Ad-free experience • Photo Calorie AI active • All custom programs open • Unlimited AI Coach."
                            else "Reklamlar kapalı • Fotoğrafla Kalori AI açık • Kişisel özel programlar açık • AI Koç sınırsız."
                        } else {
                            if (isEn) "PRO Plan is optional. Upgrade anytime or unlock special workouts individually by watching 3 ads."
                            else "PRO Plan zorunlu değildir. İstediğin zaman PRO'ya geçebilir veya özel antrenmanları 3 reklam izleyerek açabilirsin."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!profile.isProActive) {
                        Button(
                            onClick = onUpgradePro,
                            colors = ButtonDefaults.buttonColors(containerColor = WarmGold, contentColor = ext.onGoldButton),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upgrade_pro_button")
                        ) {
                            Icon(Icons.Rounded.Verified, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isEn) "Upgrade to PRO" else "PRO Plana Geç", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(onClick = onDowngradeFree, modifier = Modifier.fillMaxWidth()) {
                            Text(if (isEn) "Switch to Free Plan" else "Ücretsiz Plana Dön")
                        }
                    }
                }
            }
        }

        // 4. Physical Summary & Retake Quiz
        item {
            val genderLabel = if (profile.gender == "female") strings.femaleLabel else strings.maleLabel
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isEn) "Physical Profile Summary" else "Fiziksel Profil Özeti",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isEn) {
                            "Name: ${profile.displayName} • Character: $genderLabel\n" +
                                "Age: ${profile.age} ${if (profile.isYouthGrowthMode) "(0-18 Growth Mode)" else ""} • Height: ${profile.heightCm} cm\n" +
                                "Weight: ${profile.currentWeightKg} kg → Target: ${profile.targetWeightKg} kg\n" +
                                "Space: ${profile.environment} • ${profile.weeklyDays} days/wk (${profile.sessionMinutes} min)"
                        } else {
                            "İsim: ${profile.displayName} • Karakter: $genderLabel\n" +
                                "Yaş: ${profile.age} ${if (profile.isYouthGrowthMode) "(0-18 Büyüme Çağı)" else ""} • Boy: ${profile.heightCm} cm\n" +
                                "Kilo: ${profile.currentWeightKg} kg → Hedef: ${profile.targetWeightKg} kg\n" +
                                "Ortam: ${profile.environment} • Haftada ${profile.weeklyDays} gün (${profile.sessionMinutes} dk)"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(onClick = onRestartQuiz, modifier = Modifier.fillMaxWidth()) {
                        Text(strings.settingsRestartQuiz)
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
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(48.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(if (selected) accent.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent else MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp)
    ) {
        if (selected) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = accent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) accent else MaterialTheme.colorScheme.onSurface
        )
    }
}
