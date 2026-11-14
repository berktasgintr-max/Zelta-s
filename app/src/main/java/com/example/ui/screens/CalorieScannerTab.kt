package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.local.MealCalorieEntity
import com.example.data.model.UserProfile
import com.example.ui.i18n.LocalAppStrings
import com.example.ui.theme.LocalExtendedColors
import com.example.ui.viewmodel.CalorieScanUiState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalorieScannerTab(
    profile: UserProfile,
    calorieLogs: List<MealCalorieEntity>,
    scanState: CalorieScanUiState,
    onAnalyzePhoto: (Bitmap) -> Unit,
    onDeleteLog: (String) -> Unit,
    onClearScanState: () -> Unit,
    onUpgradePro: () -> Unit,
    onOpenInviteTab: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isEn = strings.langCode == "en"
    val ext = LocalExtendedColors.current
    val context = LocalContext.current

    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }
    var localError by remember { mutableStateOf<String?>(null) }

    fun handleDecodedBitmap(uri: Uri) {
        val bmp = decodeScaledBitmap(context, uri, maxDimension = 1024)
        if (bmp == null) {
            localError = if (isEn) "Could not read selected image." else "Seçilen fotoğraf okunamadı."
        } else {
            localError = null
            previewBitmap = bmp
            onAnalyzePhoto(bmp)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCaptureUri
        if (success && uri != null) {
            handleDecodedBitmap(uri)
        }
    }

    fun launchCameraCapture() {
        localError = null
        val uri = createTempMealPhotoUri(context)
        pendingCaptureUri = uri
        takePictureLauncher.launch(uri)
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCameraCapture()
        } else {
            localError = if (isEn) {
                "Camera permission is required to take a meal photo. You can also pick from your gallery."
            } else {
                "Öğün fotoğrafı çekmek için kamera izni gerekiyor. Dilersen galeriden de fotoğraf seçebilirsin."
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            handleDecodedBitmap(uri)
        }
    }

    // Strict PRO Paywall / Gate
    if (!profile.isProActive) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, ext.goldText.copy(alpha = 0.7f), MaterialTheme.shapes.large)
                        .testTag("calorie_pro_gate_card")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_koc_tavuk_0),
                            contentDescription = strings.coachAdviceHeader,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(124.dp)
                        )
                        Surface(
                            color = ext.goldContainer,
                            shape = CircleShape
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Rounded.Lock, contentDescription = null, tint = ext.goldText, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEn) "PRO FEATURE" else "PRO ÖZELLİK",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = ext.goldText,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = strings.homeCalorieTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (isEn) {
                                "Snap a photo of your plate or choose from gallery—Koç Tavuk AI estimates total calories, protein, carbs, and fats and saves your daily nutrition log. Exclusive to PRO Plan!"
                            } else {
                                "Tabağının fotoğrafını çek veya galeriden seç; Koç Tavuk AI yemeğinin kalorisini, protein, karbonhidrat ve yağ değerlerini anında hesaplayıp günlüğüne kaydetsin. Bu özellik PRO Plan'a özeldir!"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onUpgradePro,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("calorie_upgrade_pro_button")
                        ) {
                            Icon(Icons.Rounded.Verified, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Upgrade to PRO" else "PRO Plana Geç",
                                fontWeight = FontWeight.Bold
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenInviteTab,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Rounded.CardGiftcard, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Invite 5 Friends = 1 Month Free PRO" else "5 Arkadaş Davet Et = 1 Ay Ücretsiz PRO",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        return
    }

    // PRO Active: Full Camera + Gallery Calorie Calculator
    val todayLogs = remember(calorieLogs) {
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        calorieLogs.filter { it.loggedAtMillis >= startOfDay }
    }
    val todayKcal = todayLogs.sumOf { it.calories }
    val todayProtein = todayLogs.sumOf { it.proteinGrams }
    val todayCarbs = todayLogs.sumOf { it.carbsGrams }
    val todayFat = todayLogs.sumOf { it.fatGrams }

    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Daily Macro & Calorie Summary Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), MaterialTheme.shapes.large)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.LocalFireDepartment, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Today's Nutrition" else "Bugünkü Kalori & Makrolar",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Surface(color = ext.goldContainer, shape = CircleShape) {
                            Text(
                                text = "$todayKcal kcal",
                                style = MaterialTheme.typography.labelLarge,
                                color = ext.goldText,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        MacroPill(if (isEn) "Protein" else "Protein", "${todayProtein}g", ext.mintText, ext.mintContainer, Modifier.weight(1f))
                        MacroPill(if (isEn) "Carbs" else "Karb", "${todayCarbs}g", ext.tealText, ext.tealContainer, Modifier.weight(1f))
                        MacroPill(if (isEn) "Fat" else "Yağ", "${todayFat}g", ext.goldText, ext.goldContainer, Modifier.weight(1f))
                    }
                }
            }
        }

        // Camera & Gallery Capture Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = strings.homeCalorieTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.homeCalorieSub,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    previewBitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = strings.homeCalorieTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                onClearScanState()
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    launchCameraCapture()
                                } else {
                                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            enabled = scanState !is CalorieScanUiState.Analyzing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("calorie_camera_button")
                        ) {
                            Icon(Icons.Rounded.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEn) "Take Photo" else "Fotoğraf Çek",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onClearScanState()
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            enabled = scanState !is CalorieScanUiState.Analyzing,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("calorie_gallery_button")
                        ) {
                            Icon(Icons.Rounded.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEn) "Gallery" else "Galeriden Seç",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    localError?.let { msg ->
                        Text(msg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                    }

                    when (scanState) {
                        CalorieScanUiState.Idle -> Unit
                        CalorieScanUiState.Analyzing -> {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = if (isEn) "Koç Tavuk is analyzing your meal..." else "Koç Tavuk öğününü analiz ediyor...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        is CalorieScanUiState.Result -> {
                            val item = scanState.entity
                            Surface(
                                color = ext.mintContainer,
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, ext.mintText.copy(alpha = 0.5f), MaterialTheme.shapes.medium)
                            ) {
                                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = item.mealName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${item.calories} kcal",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = ext.mintText,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                    Text(
                                        text = "P: ${item.proteinGrams}g  •  K: ${item.carbsGrams}g  •  Y: ${item.fatGrams}g",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (item.portionSummary.isNotBlank()) {
                                        Text(item.portionSummary, style = MaterialTheme.typography.bodyMedium)
                                    }
                                    if (item.coachNote.isNotBlank()) {
                                        Text(
                                            text = "Koç Tavuk: ${item.coachNote}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        is CalorieScanUiState.Error -> {
                            Surface(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = scanState.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    previewBitmap?.let { bmp ->
                                        OutlinedButton(onClick = { onAnalyzePhoto(bmp) }) {
                                            Text(if (isEn) "Retry Analysis" else "Tekrar Dene")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Logged Meals History
        if (calorieLogs.isNotEmpty()) {
            item {
                Text(
                    text = if (isEn) "Recent Meal Analyses" else "Kaydedilen Öğün Analizleri",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(calorieLogs, key = { it.id }) { meal ->
                val timeStr = remember(meal.loggedAtMillis, isEn) {
                    val loc = if (isEn) Locale.ENGLISH else Locale.forLanguageTag("tr")
                    SimpleDateFormat("dd MMM, HH:mm", loc).format(Date(meal.loggedAtMillis))
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(ext.goldContainer)
                        ) {
                            Icon(Icons.Rounded.Restaurant, contentDescription = null, tint = ext.goldText)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(meal.mealName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${meal.calories} kcal • P:${meal.proteinGrams}g  K:${meal.carbsGrams}g  Y:${meal.fatGrams}g",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = timeStr,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDeleteLog(meal.id) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = if (isEn) "Delete" else "Sil", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroPill(
    label: String,
    value: String,
    textColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = textColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = textColor)
        }
    }
}

private fun createTempMealPhotoUri(context: Context): Uri {
    val dir = File(context.cacheDir, "meal_photos").apply { mkdirs() }
    val file = File(dir, "meal_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun decodeScaledBitmap(context: Context, uri: Uri, maxDimension: Int): Bitmap? {
    return try {
        val raw = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val w = info.size.width
                val h = info.size.height
                val largest = maxOf(w, h)
                if (largest > maxDimension) {
                    val ratio = maxDimension.toFloat() / largest.toFloat()
                    decoder.setTargetSize((w * ratio).toInt().coerceAtLeast(1), (h * ratio).toInt().coerceAtLeast(1))
                }
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            val largest = maxOf(bounds.outWidth, bounds.outHeight)
            var sample = 1
            while (largest / sample > maxDimension * 2) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        } ?: return null

        val largest = maxOf(raw.width, raw.height)
        if (largest <= maxDimension) {
            raw
        } else {
            val ratio = maxDimension.toFloat() / largest.toFloat()
            Bitmap.createScaledBitmap(
                raw,
                (raw.width * ratio).toInt().coerceAtLeast(1),
                (raw.height * ratio).toInt().coerceAtLeast(1),
                true
            )
        }
    } catch (_: Exception) {
        null
    }
}
