package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiChatMessage
import com.example.data.model.UserProfile
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.viewmodel.AiUiState
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AiCoachScreen(
    profile: UserProfile,
    messages: List<AiChatMessage>,
    aiUiState: AiUiState,
    remainingFreeQuestions: Int,
    onAskQuestion: (String) -> Unit,
    onUpgradePro: () -> Unit,
    onOpenInviteTab: () -> Unit,
    onOpenCalorieScanner: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val quickPrompts = remember(profile.isYouthGrowthMode, isEn) {
        if (isEn) {
            if (profile.isYouthGrowthMode) {
                listOf(
                    "Which evening stretches support posture & height?",
                    "How should I hit protein goals as a teen athlete?",
                    "How often should I train vertical jump power?"
                )
            } else {
                listOf(
                    "What should I eat to burn fat without losing muscle?",
                    "How can I break a bench press or push-up plateau?",
                    "What is the best post-workout recovery routine?"
                )
            }
        } else {
            if (profile.isYouthGrowthMode) {
                listOf(
                    "Duruş ve büyüme için akşam hangi esnemeleri yapmalıyım?",
                    "Ergenlikte protein ihtiyacımı nasıl karşılarım?",
                    "Sıçrama gücümü artırmak için haftada kaç gün çalışmalıyım?"
                )
            } else {
                listOf(
                    "Yağ yakarken kas kaybetmemek için ne yemeliyim?",
                    "Bench press ve şınavda tıkanıklığı nasıl aşarım?",
                    "Antrenman sonrası toparlanma için en iyi rutin nedir?"
                )
            }
        }
    }

    LaunchedEffect(messages.size, aiUiState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // AI Coach Top Status Bar
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_koc_tavuk_0),
                    contentDescription = strings.askAiTitle,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Koç Tavuk AI",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (profile.isProActive) {
                            if (isEn) "PRO Plan • Unlimited Questions" else "PRO Plan • Sınırsız Soru Hakkı"
                        } else {
                            if (isEn) "Free Plan • Daily Questions Left: $remainingFreeQuestions / ${MainViewModel.FREE_AI_DAILY_LIMIT}"
                            else "Ücretsiz Plan • Kalan Günlük Hak: $remainingFreeQuestions / ${MainViewModel.FREE_AI_DAILY_LIMIT}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (profile.isProActive) ext.goldText else ext.tealText
                    )
                }
                Surface(
                    color = ext.goldContainer,
                    shape = CircleShape,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onOpenCalorieScanner)
                        .testTag("ai_open_calorie_scanner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Rounded.CameraAlt, contentDescription = strings.homeCalorieTitle, tint = ext.goldText, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = strings.tabCalories,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = ext.goldText
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                if (profile.isProActive) {
                    Icon(Icons.Rounded.AllInclusive, contentDescription = null, tint = ext.goldText)
                } else {
                    Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f), shape = CircleShape) {
                        Text(
                            text = "$remainingFreeQuestions/${MainViewModel.FREE_AI_DAILY_LIMIT}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Chat List
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(isFromUser = msg.isFromUser, text = msg.text)
            }

            if (aiUiState is AiUiState.Loading) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strings.aiThinking, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (aiUiState is AiUiState.Streaming) {
                item {
                    ChatBubble(isFromUser = false, text = aiUiState.text)
                }
            } else if (aiUiState is AiUiState.Error) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.14f)),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(aiUiState.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                            if (!profile.isProActive && remainingFreeQuestions <= 0) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(onClick = onUpgradePro) {
                                        Text(if (isEn) "Upgrade to PRO" else "PRO'ya Geç")
                                    }
                                    OutlinedButton(onClick = onOpenInviteTab) {
                                        Text(if (isEn) "Invite 5 Friends" else "5 Arkadaş Davet Et")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Suggested Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable { inputText = prompt }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Bottom Input Bar
        val canAsk = profile.isProActive || remainingFreeQuestions > 0
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(if (canAsk) strings.aiInputPlaceholder else strings.aiLimitReachedPlaceholder)
                },
                enabled = canAsk && aiUiState !is AiUiState.Loading && aiUiState !is AiUiState.Streaming,
                maxLines = 3,
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_question_input")
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    val q = inputText.trim()
                    if (q.isNotEmpty()) {
                        inputText = ""
                        onAskQuestion(q)
                    }
                },
                enabled = canAsk && inputText.isNotBlank() && aiUiState !is AiUiState.Loading && aiUiState !is AiUiState.Streaming,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (canAsk) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = if (canAsk) Icons.AutoMirrored.Rounded.Send else Icons.Rounded.Lock,
                    contentDescription = if (isEn) "Send" else "Gönder",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(isFromUser: Boolean, text: String) {
    Row(
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = if (isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isFromUser) 18.dp else 4.dp,
                bottomEnd = if (isFromUser) 4.dp else 18.dp
            ),
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(13.dp)
            )
        }
    }
}
