package com.example.shield.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shield.data.ScanRecordEntity
import com.example.shield.model.AppLanguage
import com.example.shield.model.ScanResult
import com.example.shield.model.SecurityStatus
import com.example.shield.model.Severity
import com.example.shield.model.SystemDiagnostics
import com.example.shield.viewmodel.ShieldTab
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.SecurityDangerRed
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningOrange
import com.example.ui.theme.ShieldCyanPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    viewModel: ShieldViewModel,
    scanResult: ScanResult?,
    diagnostics: SystemDiagnostics?,
    scanHistory: List<ScanRecordEntity>,
    currentLanguage: AppLanguage
) {
    val isHi = currentLanguage.isHindi
    val score = scanResult?.securityScore ?: 100
    val animatedScore by animateFloatAsState(targetValue = score / 100f, label = "score_anim")

    val status = scanResult?.overallStatus ?: SecurityStatus.SECURE
    val statusColor = when (status) {
        SecurityStatus.SECURE -> SecuritySafeGreen
        SecurityStatus.WARNING -> SecurityWarningOrange
        SecurityStatus.DANGER, SecurityStatus.CRITICAL -> SecurityDangerRed
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Hero Security Status Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_status_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    statusColor.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Score Gauge
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(140.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedScore },
                                strokeWidth = 10.dp,
                                color = statusColor,
                                trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxSize()
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$score",
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi) "सुरक्षा स्कोर" else "Shield Score",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Status Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.2f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (status == SecurityStatus.SECURE) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = status.title(currentLanguage),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Last scan info & disclaimer
                        val lastScanText = if (scanResult != null) {
                            val time = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(scanResult.scanTimestamp))
                            if (isHi) "पिछला स्कैन: $time (${scanResult.totalAppsScanned} ऐप्स जाँचे)" else "Last scan: $time (${scanResult.totalAppsScanned} apps checked)"
                        } else {
                            if (isHi) "अभी तक कोई पूर्ण स्कैन नहीं हुआ है" else "No full device scan performed yet"
                        }

                        Text(
                            text = lastScanText,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isHi)
                                "स्कोर पद्धति: 100 अंकों से शुरू। वास्तविक अनुमतियों व सिस्टम सेटिंग्स के अनुसार गणना। कोई भी स्कैन 100% सुरक्षा की गारंटी नहीं देता।"
                            else
                                "Score methodology: Base 100. Evaluates verified package permissions and settings. No scan guarantees absolute security.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        // Primary Scan Action Buttons
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.startScan() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("scan_now_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "फोन स्कैन करें" else "Scan My Phone",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.selectTab(ShieldTab.REPAIR) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("repair_issues_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "सुधार केंद्र" else "Repair Center",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Real Telemetry Meters
        item {
            Text(
                text = if (isHi) "डिवाइस टेलीमेट्री" else "Live Device Telemetry",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Storage Telemetry Card
                val storageUsedPercent = diagnostics?.storageUsagePercent ?: 0
                val storageFreeGb = if (diagnostics != null) String.format("%.1f", diagnostics.freeStorageBytes / (1024.0 * 1024 * 1024)) else "0"
                TelemetrySmallCard(
                    icon = Icons.Default.Storage,
                    label = if (isHi) "स्टोरेज" else "Storage",
                    value = "$storageUsedPercent%",
                    subText = if (isHi) "$storageFreeGb GB खाली" else "$storageFreeGb GB Free",
                    warning = diagnostics?.isStorageLow == true,
                    onClick = { viewModel.selectTab(ShieldTab.DIAGNOSTICS) },
                    modifier = Modifier.weight(1f)
                )

                // RAM Telemetry Card
                val ramUsedPercent = diagnostics?.ramUsagePercent ?: 0
                val ramFreeMb = if (diagnostics != null) (diagnostics.availRamBytes / (1024 * 1024)) else 0
                TelemetrySmallCard(
                    icon = Icons.Default.Memory,
                    label = if (isHi) "रैम (RAM)" else "RAM",
                    value = "$ramUsedPercent%",
                    subText = if (isHi) "$ramFreeMb MB मुक्त" else "$ramFreeMb MB Avail",
                    warning = diagnostics?.isLowMemory == true,
                    onClick = { viewModel.selectTab(ShieldTab.DIAGNOSTICS) },
                    modifier = Modifier.weight(1f)
                )

                // Battery Telemetry Card
                val batteryLevel = diagnostics?.batteryLevelPercent ?: 100
                val batteryTemp = if (diagnostics != null) String.format("%.1f", diagnostics.batteryTemperatureC) else "30"
                TelemetrySmallCard(
                    icon = Icons.Default.BatteryChargingFull,
                    label = if (isHi) "बैटरी" else "Battery",
                    value = "$batteryLevel%",
                    subText = "$batteryTemp°C",
                    warning = (diagnostics?.batteryTemperatureC ?: 0f) >= 43f,
                    onClick = { viewModel.selectTab(ShieldTab.DIAGNOSTICS) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Privacy & Ownership Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ShieldCyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHi) "डिवाइस आपका है, नियंत्रण आपका है" else "Your Device. Your Full Control.",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isHi)
                                "कोई छिपा हुआ रिमोट एक्सेस नहीं। कोई व्यक्तिगत डेटा अपलोड नहीं। सभी स्कैन स्थानीय रूप से ऑन-डिवाइस चलते हैं।"
                            else
                                "No remote backdoors. No uploading of photos, SMS, or passwords. All analysis runs strictly on-device.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Recent Scans from Room Database
        if (scanHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHi) "स्कैन इतिहास (स्थानीय रूप से सुरक्षित)" else "Scan History (Stored Locally)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            val recentRecords = scanHistory.take(3)
            items(recentRecords.size) { idx ->
                val record = recentRecords[idx]
                ScanHistoryCard(record = record, isHindi = isHi)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun TelemetrySmallCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subText: String,
    warning: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (warning) SecurityWarningOrange.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (warning) SecurityWarningOrange else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (warning) SecurityWarningOrange else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = subText,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun ScanHistoryCard(
    record: ScanRecordEntity,
    isHindi: Boolean
) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(record.timestamp))

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${record.securityScore}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isHindi)
                        "${record.totalAppsScanned} ऐप्स जाँचे • ${record.criticalIssues + record.highIssues} जोखिम"
                    else
                        "${record.totalAppsScanned} apps checked • ${record.criticalIssues + record.highIssues} issues",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
