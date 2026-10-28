package com.example.shield.scanner

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.example.shield.model.AppAuditInfo
import com.example.shield.model.IssueCategory
import com.example.shield.model.RepairActionType
import com.example.shield.model.ScanResult
import com.example.shield.model.SecurityIssue
import com.example.shield.model.SecurityStatus
import com.example.shield.model.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.MessageDigest

class DeviceSecurityScanner(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private val performanceScanner = PerformanceDiagnosticsScanner(context)

    suspend fun performFullScan(
        onProgressUpdate: (progress: Float, currentStepEn: String, currentStepHi: String) -> Unit = { _, _, _ -> }
    ): ScanResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        val allIssues = mutableListOf<SecurityIssue>()

        // Step 1: Query and inspect installed packages
        onProgressUpdate(0.15f, "Enumerating installed applications...", "इंस्टॉल किए गए ऐप्स की सूची जांची जा रही है...")
        delay(250) // Small pause for UX pacing
        val installedPackages = getInstalledPackagesSafe()
        val auditedApps = mutableListOf<AppAuditInfo>()

        var nonSystemCount = 0
        var suspiciousAppCount = 0

        val totalP = installedPackages.size.coerceAtLeast(1)
        installedPackages.forEachIndexed { index, pkg ->
            if (index % 10 == 0) {
                val p = 0.15f + (0.45f * (index.toFloat() / totalP))
                val appLabel = try {
                    pkg.applicationInfo?.loadLabel(packageManager)?.toString() ?: pkg.packageName
                } catch (_: Exception) {
                    pkg.packageName
                }
                onProgressUpdate(p, "Auditing permissions: $appLabel", "अनुमतियाँ जांची जा रही हैं: $appLabel")
            }

            val appInfo = auditPackage(pkg)
            auditedApps.add(appInfo)
            if (!appInfo.isSystemApp) {
                nonSystemCount++
            }
            if (appInfo.riskSeverity == Severity.CRITICAL || appInfo.riskSeverity == Severity.HIGH) {
                suspiciousAppCount++
            }
        }

        // Step 2: System Privacy & Sensitive Access Audit
        onProgressUpdate(0.65f, "Auditing accessibility & device admin services...", "एक्सेसिबिलिटी और डिवाइस एडमिन सेवाओं का ऑडिट...")
        delay(200)
        val privacyIssues = auditSystemPrivacySettings()
        allIssues.addAll(privacyIssues)

        // Convert high-risk apps to issues
        auditedApps.filter { it.riskSeverity == Severity.CRITICAL || it.riskSeverity == Severity.HIGH || it.riskSeverity == Severity.MEDIUM }
            .forEach { app ->
                val action = if (app.isSystemApp) RepairActionType.OPEN_APP_SETTINGS else RepairActionType.UNINSTALL_APP
                allIssues.add(
                    SecurityIssue(
                        id = "app_risk_${app.packageName}",
                        titleEn = "${app.appName}: ${app.riskSeverity.labelEn}",
                        titleHi = "${app.appName}: ${app.riskSeverity.labelHi}",
                        descriptionEn = app.riskExplanationEn,
                        descriptionHi = app.riskExplanationHi,
                        evidenceEn = "Package: ${app.packageName} (v${app.versionName}). Sideloaded: ${app.isSideloaded}. Sensitive: ${app.sensitiveCombinations.joinToString(", ")}",
                        evidenceHi = "पैकेज: ${app.packageName} (v${app.versionName})। साइडलोडेड: ${if (app.isSideloaded) "हाँ" else "नहीं"}। संवेदनशील अनुमतियाँ: ${app.sensitiveCombinations.joinToString(", ")}",
                        severity = app.riskSeverity,
                        category = if (app.isSideloaded) IssueCategory.UNKNOWN_SOURCE else IssueCategory.APP_PERMISSION,
                        packageName = app.packageName,
                        recommendationEn = "Inspect this application in Android settings. If you do not recognize or need it, uninstall it.",
                        recommendationHi = "एंड्रॉइड सेटिंग्स में इस ऐप की जांच करें। यदि आप इसे नहीं पहचानते, तो तुरंत अनइंस्टॉल करें।",
                        actionType = action
                    )
                )
            }

        // Step 3: Performance, Storage & Battery Diagnostics
        onProgressUpdate(0.85f, "Collecting real device diagnostics...", "वास्तविक डिवाइस डायग्नोस्टिक्स एकत्र किए जा रहे हैं...")
        delay(200)
        val diagnostics = performanceScanner.collectDiagnostics()
        val perfIssues = performanceScanner.evaluatePerformanceIssues(diagnostics)
        allIssues.addAll(perfIssues)

        onProgressUpdate(1.0f, "Finalizing security audit...", "सुरक्षा ऑडिट पूरा किया जा रहा है...")
        delay(150)

        // Calculate security score
        // Methodology: Base 100
        // -25 per Critical, -15 per High, -8 per Medium, -3 per Low
        var score = 100
        var criticalCount = 0
        var highCount = 0
        var mediumCount = 0
        var lowCount = 0

        allIssues.forEach { issue ->
            when (issue.severity) {
                Severity.CRITICAL -> { score -= 25; criticalCount++ }
                Severity.HIGH -> { score -= 15; highCount++ }
                Severity.MEDIUM -> { score -= 8; mediumCount++ }
                Severity.LOW -> { score -= 3; lowCount++ }
                else -> {}
            }
        }
        val finalScore = score.coerceIn(10, 100)

        val overallStatus = when {
            criticalCount > 0 -> SecurityStatus.CRITICAL
            highCount > 0 || finalScore < 60 -> SecurityStatus.DANGER
            mediumCount > 0 || finalScore < 85 -> SecurityStatus.WARNING
            else -> SecurityStatus.SECURE
        }

        val duration = System.currentTimeMillis() - startTime

        ScanResult(
            scanTimestamp = System.currentTimeMillis(),
            totalAppsScanned = installedPackages.size,
            nonSystemAppsScanned = nonSystemCount,
            suspiciousAppsCount = suspiciousAppCount,
            privacyIssuesCount = privacyIssues.size,
            performanceIssuesCount = perfIssues.size,
            issues = allIssues.sortedByDescending { it.severity.priority },
            securityScore = finalScore,
            overallStatus = overallStatus,
            scanDurationMs = duration,
            diagnostics = diagnostics.copy(
                activeAccessibilityServicesCount = privacyIssues.count { it.category == IssueCategory.ACCESSIBILITY },
                activeDeviceAdminsCount = privacyIssues.count { it.category == IssueCategory.DEVICE_ADMIN }
            )
        )
    }

    private fun getInstalledPackagesSafe(): List<PackageInfo> {
        return try {
            packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun auditPackage(pkg: PackageInfo): AppAuditInfo {
        val appInfo = pkg.applicationInfo
        val appName = try {
            appInfo?.loadLabel(packageManager)?.toString() ?: pkg.packageName
        } catch (_: Exception) {
            pkg.packageName
        }
        val isSystem = (appInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM) ?: 0) != 0

        val requestedPerms = pkg.requestedPermissions?.toList() ?: emptyList()
        val grantedPerms = mutableListOf<String>()
        val pkgPermFlags = pkg.requestedPermissionsFlags
        if (pkgPermFlags != null && requestedPerms.size == pkgPermFlags.size) {
            requestedPerms.forEachIndexed { i, p ->
                if ((pkgPermFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                    grantedPerms.add(p)
                }
            }
        } else {
            grantedPerms.addAll(requestedPerms)
        }

        // Installation source
        val installer = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                packageManager.getInstallSourceInfo(pkg.packageName).installingPackageName
            } else {
                @Suppress("DEPRECATION")
                packageManager.getInstallerPackageName(pkg.packageName)
            }
        } catch (_: Exception) {
            null
        }

        val isSideloaded = !isSystem && (installer == null || installer != "com.android.vending")

        // Analyze sensitive combinations
        val sensitiveCombos = mutableListOf<String>()
        val hasSms = requestedPerms.any { it.contains("SMS", ignoreCase = true) }
        val hasInternet = requestedPerms.contains("android.permission.INTERNET")
        val hasCamera = requestedPerms.contains("android.permission.CAMERA")
        val hasAudio = requestedPerms.contains("android.permission.RECORD_AUDIO")
        val hasOverlay = requestedPerms.contains("android.permission.SYSTEM_ALERT_WINDOW")
        val hasInstallUnknown = requestedPerms.contains("android.permission.REQUEST_INSTALL_PACKAGES")
        val hasLocation = requestedPerms.any { it.contains("LOCATION", ignoreCase = true) }
        val hasBoot = requestedPerms.contains("android.permission.RECEIVE_BOOT_COMPLETED")

        if (hasSms && hasInternet && !isSystem && pkg.packageName != context.packageName) {
            sensitiveCombos.add("SMS + Internet (Exfiltration Risk)")
        }
        if ((hasCamera || hasAudio) && hasOverlay && !isSystem) {
            sensitiveCombos.add("Camera/Audio + Screen Overlay (Surveillance Risk)")
        }
        if (hasInstallUnknown && !isSystem && pkg.packageName != "com.android.vending") {
            sensitiveCombos.add("Request Install Unknown Packages")
        }
        if (hasLocation && hasBoot && !isSystem) {
            sensitiveCombos.add("Location + Boot Receiver (Persistent Tracking)")
        }

        // Determine severity
        var severity = Severity.SAFE
        var explanationEn = "No suspicious permission patterns detected."
        var explanationHi = "कोई संदिग्ध अनुमति पैटर्न नहीं मिला।"

        if (!isSystem && pkg.packageName != context.packageName) {
            if (hasSms && hasInternet) {
                severity = Severity.HIGH
                explanationEn = "Requests full SMS access combined with Internet connectivity. Can potentially intercept verification codes or send premium SMS."
                explanationHi = "पूर्ण SMS और इंटरनेट दोनों की अनुमति मांगता है। सत्यापन कोड चोरी करने या संदेश भेजने का जोखिम हो सकता है।"
            } else if ((hasCamera || hasAudio) && hasOverlay) {
                severity = Severity.HIGH
                explanationEn = "Requests camera or microphone access along with screen overlay. Can potentially record in background while displaying deceptive windows."
                explanationHi = "कैमरा/माइक के साथ स्क्रीन ओवरले की अनुमति मांगता है। बैकग्राउंड रिकॉर्डिंग का जोखिम हो सकता है।"
            } else if (hasInstallUnknown && isSideloaded) {
                severity = Severity.MEDIUM
                explanationEn = "Application was installed from an unverified source and can install arbitrary APK files onto your device."
                explanationHi = "यह ऐप अज्ञात स्रोत से इंस्टॉल किया गया है और फोन में अन्य APK फाइलें इंस्टॉल कर सकता है।"
            } else if (isSideloaded && sensitiveCombos.isNotEmpty()) {
                severity = Severity.MEDIUM
                explanationEn = "Sideloaded application with sensitive permission combinations ($sensitiveCombos)."
                explanationHi = "अज्ञात स्रोत से आया ऐप जिसमें संवेदनशील अनुमतियां हैं ($sensitiveCombos)।"
            }
        }

        return AppAuditInfo(
            packageName = pkg.packageName,
            appName = appName,
            versionName = pkg.versionName ?: "Unknown",
            versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) pkg.longVersionCode else @Suppress("DEPRECATION") pkg.versionCode.toLong(),
            isSystemApp = isSystem,
            installerPackageName = installer,
            isSideloaded = isSideloaded,
            requestedPermissions = requestedPerms,
            dangerousPermissions = grantedPerms.filter { it.startsWith("android.permission") && (it.contains("SMS") || it.contains("LOCATION") || it.contains("CAMERA") || it.contains("AUDIO") || it.contains("CONTACTS") || it.contains("STORAGE")) },
            sensitiveCombinations = sensitiveCombos,
            hasOverlayPermission = hasOverlay,
            hasRequestInstallPackages = hasInstallUnknown,
            riskSeverity = severity,
            riskExplanationEn = explanationEn,
            riskExplanationHi = explanationHi
        )
    }

    private fun auditSystemPrivacySettings(): List<SecurityIssue> {
        val issues = mutableListOf<SecurityIssue>()

        // 1. Accessibility Services enabled
        try {
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (!enabledServices.isNullOrBlank()) {
                val servicesList = enabledServices.split(":")
                val thirdPartyServices = servicesList.filter { service ->
                    !service.contains("google", ignoreCase = true) &&
                    !service.contains("talkback", ignoreCase = true) &&
                    !service.contains("switchaccess", ignoreCase = true) &&
                    !service.contains("samsung", ignoreCase = true)
                }

                if (thirdPartyServices.isNotEmpty()) {
                    issues.add(
                        SecurityIssue(
                            id = "active_third_party_accessibility",
                            titleEn = "${thirdPartyServices.size} Active Accessibility Service(s) Detected",
                            titleHi = "${thirdPartyServices.size} तृतीय-पक्ष एक्सेसिबिलिटी सेवाएं सक्रिय हैं",
                            descriptionEn = "Accessibility services have full visibility into your screen, typed text, and UI interactions. Malicious apps often abuse this to steal credentials or bypass 2FA.",
                            descriptionHi = "एक्सेसिबिलिटी सेवाओं के पास आपकी स्क्रीन, टाइप किए गए टेक्स्ट और बटन दबाने की पूरी पहुंच होती है। यदि कोई अनजान सेवा सक्रिय है तो वह डेटा चुरा सकती है।",
                            evidenceEn = "Active services: ${thirdPartyServices.joinToString(", ")}",
                            evidenceHi = "सक्रिय सेवाएं: ${thirdPartyServices.joinToString(", ")}",
                            severity = Severity.HIGH,
                            category = IssueCategory.ACCESSIBILITY,
                            recommendationEn = "Review all enabled Accessibility Services in Android Settings. Disable any service you do not specifically recognize or rely on for assistive support.",
                            recommendationHi = "एंड्रॉइड सेटिंग्स में जाकर एक्सेसिबिलिटी सेवाओं की समीक्षा करें और अनजान सेवाओं को तुरंत बंद करें।",
                            actionType = RepairActionType.OPEN_ACCESSIBILITY_SETTINGS
                        )
                    )
                }
            }
        } catch (_: Exception) {}

        // 2. Active Device Administrators
        try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager
            val activeAdmins: List<ComponentName>? = dpm?.activeAdmins
            if (!activeAdmins.isNullOrEmpty()) {
                val nonGoogleAdmins = activeAdmins.filter { admin ->
                    !admin.packageName.contains("google", ignoreCase = true) &&
                    !admin.packageName.contains("android", ignoreCase = true) &&
                    !admin.packageName.contains("samsung", ignoreCase = true)
                }
                if (nonGoogleAdmins.isNotEmpty()) {
                    issues.add(
                        SecurityIssue(
                            id = "active_device_admins",
                            titleEn = "${nonGoogleAdmins.size} Device Administrator App(s) Active",
                            titleHi = "${nonGoogleAdmins.size} डिवाइस एडमिनिस्ट्रेटर ऐप सक्रिय हैं",
                            descriptionEn = "Device administrators possess deep privileged authority over your phone, including the ability to lock the screen, wipe storage, and prevent normal uninstallation.",
                            descriptionHi = "डिवाइस एडमिनिस्ट्रेटर ऐप्स को स्क्रीन लॉक करने, डेटा मिटाने और अनइंस्टॉल रोकने जैसे विशेष अधिकार प्राप्त होते हैं।",
                            evidenceEn = "Active administrators: ${nonGoogleAdmins.map { it.flattenToShortString() }.joinToString(", ")}",
                            evidenceHi = "सक्रिय व्यवस्थापक: ${nonGoogleAdmins.map { it.flattenToShortString() }.joinToString(", ")}",
                            severity = Severity.CRITICAL,
                            category = IssueCategory.DEVICE_ADMIN,
                            recommendationEn = "Review active Device Admin apps. If you did not intentionally assign admin rights to an enterprise or security tool, revoke its permissions.",
                            recommendationHi = "डिवाइस एडमिनिस्ट्रेटर सेटिंग्स में जाकर अनजान ऐप्स के एडमिन अधिकार तुरंत समाप्त करें।",
                            actionType = RepairActionType.OPEN_DEVICE_ADMIN_SETTINGS
                        )
                    )
                }
            }
        } catch (_: Exception) {}

        return issues
    }

    suspend fun calculateSha256(inputStream: InputStream): String = withContext(Dispatchers.IO) {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(8192)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
        val hashBytes = digest.digest()
        val sb = java.lang.StringBuilder()
        for (b in hashBytes) {
            sb.append(String.format("%02x", b))
        }
        sb.toString()
    }
}
