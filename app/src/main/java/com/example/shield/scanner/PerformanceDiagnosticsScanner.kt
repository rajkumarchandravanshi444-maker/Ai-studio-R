package com.example.shield.scanner

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import com.example.shield.model.AppLanguage
import com.example.shield.model.IssueCategory
import com.example.shield.model.RepairActionType
import com.example.shield.model.SecurityIssue
import com.example.shield.model.Severity
import com.example.shield.model.SystemDiagnostics
import java.io.File

class PerformanceDiagnosticsScanner(private val context: Context) {

    fun collectDiagnostics(): SystemDiagnostics {
        // 1. Storage Stats
        val dataDir: File = Environment.getDataDirectory()
        val statFs = StatFs(dataDir.path)
        val blockSize = statFs.blockSizeLong
        val totalBlocks = statFs.blockCountLong
        val availableBlocks = statFs.availableBlocksLong

        val totalStorageBytes = totalBlocks * blockSize
        val freeStorageBytes = availableBlocks * blockSize
        val usedStorageBytes = (totalStorageBytes - freeStorageBytes).coerceAtLeast(0L)
        val storageUsagePercent = if (totalStorageBytes > 0) {
            ((usedStorageBytes.toDouble() / totalStorageBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else 0
        val isStorageLow = storageUsagePercent >= 90 || freeStorageBytes < (2L * 1024 * 1024 * 1024)

        // 2. RAM Memory Stats
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager?.getMemoryInfo(memInfo)

        val totalRamBytes = memInfo.totalMem
        val availRamBytes = memInfo.availMem
        val usedRamBytes = (totalRamBytes - availRamBytes).coerceAtLeast(0L)
        val ramUsagePercent = if (totalRamBytes > 0) {
            ((usedRamBytes.toDouble() / totalRamBytes.toDouble()) * 100).toInt().coerceIn(0, 100)
        } else 0
        val isLowMemory = memInfo.lowMemory || ramUsagePercent >= 88

        // 3. Battery Stats (Sticky Broadcast)
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val rawLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val rawScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryLevelPercent = if (rawLevel >= 0 && rawScale > 0) {
            ((rawLevel.toFloat() / rawScale.toFloat()) * 100).toInt().coerceIn(0, 100)
        } else 100

        val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250) ?: 250
        val batteryTemperatureC = (rawTemp / 10.0f).coerceIn(10.0f, 65.0f)
        val batteryVoltageMv = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0

        val healthCode = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
        val batteryHealth = when (healthCode) {
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Good"
        }

        val statusCode = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val batteryStatus = when (statusCode) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            else -> "Not Charging"
        }

        val pluggedCode = batteryIntent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
        val powerSource = when (pluggedCode) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC Charger"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB Port"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "Wireless"
            else -> "Battery Power"
        }

        // 4. Network & VPN Stats
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(activeNetwork)

        val isVpnActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        val isWifiActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        val isCellularActive = caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        val vpnTransportName = if (isVpnActive) "Encrypted VPN Tunnel" else null

        val totalRxBytes = TrafficStats.getTotalRxBytes().coerceAtLeast(0L)
        val totalTxBytes = TrafficStats.getTotalTxBytes().coerceAtLeast(0L)
        val appRxBytes = TrafficStats.getUidRxBytes(context.applicationInfo.uid).coerceAtLeast(0L)
        val appTxBytes = TrafficStats.getUidTxBytes(context.applicationInfo.uid).coerceAtLeast(0L)

        // 5. System Developer Option check
        val devSettings = try {
            Settings.Global.getInt(context.contentResolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1
        } catch (_: Exception) {
            false
        }

        return SystemDiagnostics(
            totalStorageBytes = totalStorageBytes,
            freeStorageBytes = freeStorageBytes,
            usedStorageBytes = usedStorageBytes,
            storageUsagePercent = storageUsagePercent,
            isStorageLow = isStorageLow,
            totalRamBytes = totalRamBytes,
            availRamBytes = availRamBytes,
            usedRamBytes = usedRamBytes,
            ramUsagePercent = ramUsagePercent,
            isLowMemory = isLowMemory,
            batteryLevelPercent = batteryLevelPercent,
            batteryTemperatureC = batteryTemperatureC,
            batteryVoltageMv = batteryVoltageMv,
            batteryHealth = batteryHealth,
            batteryStatus = batteryStatus,
            powerSource = powerSource,
            isVpnActive = isVpnActive,
            vpnTransportName = vpnTransportName,
            isWifiActive = isWifiActive,
            isCellularActive = isCellularActive,
            totalRxBytes = totalRxBytes,
            totalTxBytes = totalTxBytes,
            appRxBytes = appRxBytes,
            appTxBytes = appTxBytes,
            isDeveloperOptionsEnabled = devSettings,
            activeAccessibilityServicesCount = 0,
            activeDeviceAdminsCount = 0
        )
    }

    fun evaluatePerformanceIssues(diag: SystemDiagnostics): List<SecurityIssue> {
        val issues = mutableListOf<SecurityIssue>()

        if (diag.isStorageLow) {
            val freeGb = diag.freeStorageBytes / (1024.0 * 1024 * 1024)
            issues.add(
                SecurityIssue(
                    id = "storage_critically_low",
                    titleEn = "Critically Low Internal Storage",
                    titleHi = "इंटरनल स्टोरेज बहुत कम है",
                    descriptionEn = "Your device has only ${String.format("%.1f", freeGb)} GB free (${diag.storageUsagePercent}% used). Low storage causes app stuttering, background crash loops, and system lag.",
                    descriptionHi = "आपके फोन में केवल ${String.format("%.1f", freeGb)} GB खाली है (${diag.storageUsagePercent}% भरा हुआ)। कम स्टोरेज से ऐप्स हैंग होते हैं और फोन धीमा चलता है।",
                    evidenceEn = "Measured free storage: ${String.format("%.1f", freeGb)} GB out of ${String.format("%.1f", diag.totalStorageBytes / (1024.0 * 1024 * 1024))} GB.",
                    evidenceHi = "मापा गया खाली स्टोरेज: ${String.format("%.1f", freeGb)} GB / कुल ${String.format("%.1f", diag.totalStorageBytes / (1024.0 * 1024 * 1024))} GB।",
                    severity = Severity.HIGH,
                    category = IssueCategory.STORAGE,
                    recommendationEn = "Review large files, remove unneeded downloads, or clear app cache safely via Android settings.",
                    recommendationHi = "बड़ी फाइल्स की समीक्षा करें, गैर-जरूरी डाउनलोड हटाएं या एंड्रॉइड सेटिंग्स से कैशे साफ करें।",
                    actionType = RepairActionType.OPEN_STORAGE_SETTINGS
                )
            )
        }

        if (diag.batteryTemperatureC >= 43.0f) {
            issues.add(
                SecurityIssue(
                    id = "battery_high_temp",
                    titleEn = "Elevated Battery Temperature Detected",
                    titleHi = "बैटरी का तापमान अधिक पाया गया",
                    descriptionEn = "Battery temperature is ${String.format("%.1f", diag.batteryTemperatureC)}°C. High heat accelerates battery degradation and can trigger thermal CPU throttling.",
                    descriptionHi = "बैटरी का तापमान ${String.format("%.1f", diag.batteryTemperatureC)}°C है। अत्यधिक गर्मी बैटरी की आयु घटाती है और फोन को धीमा करती है।",
                    evidenceEn = "Android BatteryManager telemetry: ${String.format("%.1f", diag.batteryTemperatureC)}°C, Voltage: ${diag.batteryVoltageMv} mV.",
                    evidenceHi = "एंड्रॉइड बैटरी टेलीमेट्री: ${String.format("%.1f", diag.batteryTemperatureC)}°C, वोल्टेज: ${diag.batteryVoltageMv} mV।",
                    severity = Severity.MEDIUM,
                    category = IssueCategory.BATTERY,
                    recommendationEn = "Pause heavy gaming or wireless charging, disconnect fast chargers in hot environments, and avoid direct sunlight.",
                    recommendationHi = "भारी गेम्स या चार्जिंग थोड़ी देर रोकें और फोन को सीधी धूप से बचाएं।",
                    actionType = RepairActionType.OPEN_BATTERY_SETTINGS
                )
            )
        }

        if (diag.isDeveloperOptionsEnabled) {
            issues.add(
                SecurityIssue(
                    id = "dev_options_enabled",
                    titleEn = "Developer Options are Enabled",
                    titleHi = "डेवलपर विकल्प (Developer Options) चालू हैं",
                    descriptionEn = "Developer options allow ADB debugging, mock locations, and simulated displays. While useful for engineers, it expands the device attack surface if left unattended.",
                    descriptionHi = "डेवलपर ऑप्शंस यूएसबी डिबगिंग की अनुमति देते हैं। यदि आप डेवलपर नहीं हैं तो इसे बंद रखना अधिक सुरक्षित है।",
                    evidenceEn = "Settings.Global.DEVELOPMENT_SETTINGS_ENABLED = 1",
                    evidenceHi = "सिस्टम सेटिंग DEVELOPMENT_SETTINGS_ENABLED = 1",
                    severity = Severity.LOW,
                    category = IssueCategory.SYSTEM_SECURITY,
                    recommendationEn = "If you are not currently debugging software, turn off Developer Options in Android Settings.",
                    recommendationHi = "यदि आप डिबगिंग नहीं कर रहे हैं, तो एंड्रॉइड सेटिंग्स में जाकर डेवलपर विकल्प बंद कर दें।",
                    actionType = RepairActionType.OPEN_SECURITY_SETTINGS
                )
            )
        }

        return issues
    }
}
