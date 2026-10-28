package com.example.shield.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shield.model.AppLanguage
import com.example.shield.model.SystemDiagnostics
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.SecurityDangerRed
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningOrange
import com.example.ui.theme.ShieldCyanPrimary

@Composable
fun PerformanceScreen(
    viewModel: ShieldViewModel,
    diagnostics: SystemDiagnostics?,
    currentLanguage: AppLanguage
) {
    val context = LocalContext.current
    val isHi = currentLanguage.isHindi

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Header with Refresh button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isHi) "परफॉरमेंस व हार्डवेयर डायग्नोस्टिक्स" else "Performance & Hardware Diagnostics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isHi) "वास्तविक सिस्टम मेट्रिक्स (कोई काल्पनिक डेटा नहीं)" else "Measured OS telemetry (No simulated stats)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.refreshDiagnostics() },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("refresh_diagnostics_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isHi) "रिफ्रेश" else "Refresh", fontSize = 12.sp)
                }
            }
        }

        // 1. Storage Diagnostics Card
        if (diagnostics != null) {
            val totalGb = diagnostics.totalStorageBytes / (1024.0 * 1024 * 1024)
            val freeGb = diagnostics.freeStorageBytes / (1024.0 * 1024 * 1024)
            val usedGb = diagnostics.usedStorageBytes / (1024.0 * 1024 * 1024)
            val storagePercent = diagnostics.storageUsagePercent

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
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
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHi) "इंटरनल स्टोरेज" else "Internal Device Storage",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi)
                                        "${String.format("%.1f", freeGb)} GB खाली / ${String.format("%.1f", totalGb)} GB कुल"
                                    else
                                        "${String.format("%.1f", freeGb)} GB free of ${String.format("%.1f", totalGb)} GB",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$storagePercent%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (diagnostics.isStorageLow) SecurityDangerRed else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { storagePercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (diagnostics.isStorageLow) SecurityDangerRed else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isHi)
                                "फोन हैंग होने का मुख्य कारण: जब इंटरनल स्टोरेज 90% से अधिक भर जाता है, तो एंड्रॉइड ऐप्स को स्वैप और कैशे के लिए पर्याप्त स्थान नहीं मिलता, जिससे ऐप्स धीमे होते हैं या क्रैश होते हैं।"
                            else
                                "Why low storage causes lag: When internal storage exceeds 90%, Android cannot allocate temporary buffer files for background processes, leading to UI stutter and app freeze loops.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                } catch (_: Exception) {}
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = if (isHi) "एंड्रॉइड स्टोरेज सेटिंग्स खोलें" else "Open Android Storage Settings")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 2. RAM Memory Diagnostics Card
        if (diagnostics != null) {
            val totalRamGb = diagnostics.totalRamBytes / (1024.0 * 1024 * 1024)
            val availRamMb = diagnostics.availRamBytes / (1024 * 1024)
            val usedRamMb = diagnostics.usedRamBytes / (1024 * 1024)
            val ramPercent = diagnostics.ramUsagePercent

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
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
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHi) "रैम (RAM) मेमोरी स्थिति" else "RAM Memory Utilization",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi)
                                        "$availRamMb MB खाली / ${String.format("%.1f", totalRamGb)} GB कुल"
                                    else
                                        "$availRamMb MB available of ${String.format("%.1f", totalRamGb)} GB total",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$ramPercent%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (diagnostics.isLowMemory) SecurityWarningOrange else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { ramPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (diagnostics.isLowMemory) SecurityWarningOrange else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isHi)
                                "मापा गया OS डेटा: एंड्रॉइड ऑपरेटिंग सिस्टम आवश्यकतानुसार रैम को स्वचालित रूप से प्रबंधित करता है। अनधिकृत टास्क-किलर ऐप्स का उपयोग न करें।"
                            else
                                "Measured OS memory info: Android automatically reclaims memory as needed. Avoid third-party 'RAM booster' apps that repeatedly force-stop system processes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 3. Battery & Thermal Diagnostics
        if (diagnostics != null) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
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
                                    imageVector = Icons.Default.BatteryChargingFull,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHi) "बैटरी एवं तापमान टेलीमेट्री" else "Battery & Thermal Telemetry",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi)
                                        "${diagnostics.batteryLevelPercent}% • ${diagnostics.batteryStatus} (${diagnostics.powerSource})"
                                    else
                                        "${diagnostics.batteryLevelPercent}% • ${diagnostics.batteryStatus} (${diagnostics.powerSource})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TelemetryDetailPill(
                                label = if (isHi) "तापमान" else "Temperature",
                                value = "${String.format("%.1f", diagnostics.batteryTemperatureC)}°C",
                                alert = diagnostics.batteryTemperatureC >= 43.0f,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryDetailPill(
                                label = if (isHi) "वोल्टेज" else "Voltage",
                                value = "${diagnostics.batteryVoltageMv} mV",
                                alert = false,
                                modifier = Modifier.weight(1f)
                            )
                            TelemetryDetailPill(
                                label = if (isHi) "स्वास्थ्य" else "Health",
                                value = diagnostics.batteryHealth,
                                alert = diagnostics.batteryHealth != "Good",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_POWER_USAGE_SUMMARY).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                } catch (_: Exception) {
                                    try {
                                        context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply {
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        })
                                    } catch (_: Exception) {}
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = if (isHi) "एंड्रॉइड बैटरी उपयोग विवरण देखें" else "Open Android Battery Usage Details")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 4. Mobile & Wi-Fi Network Stats
        if (diagnostics != null) {
            val totalRxMb = diagnostics.totalRxBytes / (1024 * 1024)
            val totalTxMb = diagnostics.totalTxBytes / (1024 * 1024)

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
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
                                    imageVector = Icons.Default.NetworkCheck,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHi) "नेटवर्क एवं डेटा ट्रैफ़िक" else "Network & Data Traffic Telemetry",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi)
                                        "कुल डाउनलोड: $totalRxMb MB • अपलोड: $totalTxMb MB"
                                    else
                                        "Total RX: $totalRxMb MB • Total TX: $totalTxMb MB",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isHi)
                                "पारदर्शिता नोट: यह टेलीमेट्री Android TrafficStats API से डिवाइस के कुल डेटा को दर्शाती है। SHIELD AI कभी भी किसी अन्य ऐप के निजी नेटवर्क पैकेट्स या सामग्री को नहीं देखता।"
                            else
                                "Transparency disclosure: Telemetry reflects aggregate device traffic from the Android TrafficStats API. SHIELD AI never inspects payload contents of other applications.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = {
                                try {
                                    context.startActivity(Intent(Settings.ACTION_DATA_USAGE_SETTINGS).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                } catch (_: Exception) {}
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = if (isHi) "डेटा उपयोग सेटिंग्स खोलें" else "Open Data Usage Settings")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 5. Safe Cache & Stability Troubleshooting Guide
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isHi) "सुरक्षित कैशे सफाई एवं हैंग समस्या समाधान" else "Safe Cache Clearing & Stability Guide",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHi)
                            "1. किसी ऐप का कैशे (Cache) साफ करने से आपका निजी डेटा (फोटो, चैट, लॉगिन) नष्ट नहीं होता।\n" +
                            "2. ऐप सेटिंग्स में जाकर 'Clear Cache' चुनें, 'Clear Storage/Data' नहीं (जब तक कि आप रीसेट न करना चाहें)।\n" +
                            "3. SHIELD AI स्वचालित रूप से फाइल्स डिलीट नहीं करता ताकि आपका जरूरी डेटा सुरक्षित रहे।"
                        else
                            "1. Clearing an app's Cache only removes temporary files; it does NOT delete photos, chat histories, or logins.\n" +
                            "2. In Android App Info, tap 'Clear Cache' rather than 'Clear Storage/Data' to maintain your logins.\n" +
                            "3. SHIELD AI never deletes user files automatically, safeguarding your personal memories and documents.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun TelemetryDetailPill(
    label: String,
    value: String,
    alert: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (alert) SecurityWarningOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (alert) SecurityWarningOrange else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
