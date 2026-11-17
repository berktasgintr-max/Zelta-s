package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AiChatMessage
import com.example.data.model.UserProfile
import com.example.ui.components.AnimatedKocTavuk
import com.example.ui.components.CleanTextField
import com.example.ui.components.CoachAnimationMood
import com.example.ui.i18n.strRes
import com.example.ui.viewmodel.AiUiState

@Composable
fun AiCoachScreen(
    profile: UserProfile,
    messages: List<AiChatMessage>,
    aiUiState: AiUiState,
    onAskQuestion: (String) -> Unit,
    onOpenCalorieScanner: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val subtleBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))

    val quickPrompts = if (profile.isYouthGrowthMode) {
        listOf(
            strRes(R.string.ai_prompt_youth_1),
            strRes(R.string.ai_prompt_youth_2),
            strRes(R.string.ai_prompt_youth_3)
        )
    } else {
        listOf(
            strRes(R.string.ai_prompt_adult_1),
            strRes(R.string.ai_prompt_adult_2),
            strRes(R.string.ai_prompt_adult_3)
        )
    }

    LaunchedEffect(messages.size, aiUiState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // FitChicken Coach Top Status Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            border = subtleBorder,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                if (onBack != null) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("ai_coach_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = strRes(R.string.common_back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                AnimatedKocTavuk(
                    size = 56.dp,
                    mood = CoachAnimationMood.COACHING,
                    triggerKey = messages.size,
                    showAura = false,
                    contentDescription = strRes(R.string.ask_ai_title)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strRes(R.string.ask_ai_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = strRes(R.string.ai_offline_instant_pill),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape,
                    border = subtleBorder,
                    modifier = Modifier
                        .heightIn(min = 40.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onOpenCalorieScanner)
                        .testTag("ai_open_calorie_scanner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Restaurant,
                            contentDescription = strRes(R.string.home_calorie_title),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strRes(R.string.tab_calories),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            softWrap = false
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
            items(
                items = messages,
                key = { it.id },
                contentType = { if (it.isFromUser) "user_bubble" else "coach_bubble" }
            ) { msg ->
                ChatBubble(isFromUser = msg.isFromUser, text = msg.text, border = subtleBorder)
            }

            if (aiUiState is AiUiState.Loading) {
                item(key = "ai_loading", contentType = "loading") {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(strRes(R.string.ai_thinking), style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (aiUiState is AiUiState.Streaming) {
                item(key = "ai_streaming", contentType = "coach_bubble") {
                    ChatBubble(isFromUser = false, text = aiUiState.text, border = subtleBorder)
                }
            } else if (aiUiState is AiUiState.Error) {
                item(key = "ai_error", contentType = "error_card") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = MaterialTheme.shapes.medium,
                        border = subtleBorder,
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = aiUiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }

        // Quick Suggested Chips
        LazyRow(
            contentPadding = PaddingValues(start = 16.dp, end = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            items(quickPrompts, key = { it }, contentType = { "prompt_chip" }) { prompt ->
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = CircleShape,
                    border = subtleBorder,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            inputText = ""
                            onAskQuestion(prompt)
                        }
                ) {
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }
        }

        // Bottom Input Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            CleanTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = strRes(R.string.ai_input_placeholder),
                enabled = aiUiState !is AiUiState.Loading && aiUiState !is AiUiState.Streaming,
                singleLine = false,
                maxLines = 3,
                modifier = Modifier.weight(1f),
                inputModifier = Modifier.testTag("ai_question_input")
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
                enabled = inputText.isNotBlank() && aiUiState !is AiUiState.Loading && aiUiState !is AiUiState.Streaming,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("ai_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = strRes(R.string.common_send),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(
    isFromUser: Boolean,
    text: String,
    border: BorderStroke
) {
    Row(
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            color = if (isFromUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isFromUser) 18.dp else 4.dp,
                bottomEnd = if (isFromUser) 4.dp else 18.dp
            ),
            border = if (isFromUser) null else border,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isFromUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}
