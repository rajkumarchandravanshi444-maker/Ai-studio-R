package com.example.shield.model

data class AppAuditInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean,
    val installerPackageName: String?,
    val isSideloaded: Boolean,
    val requestedPermissions: List<String>,
    val dangerousPermissions: List<String>,
    val sensitiveCombinations: List<String>,
    val hasAccessibilityService: Boolean = false,
    val hasDeviceAdmin: Boolean = false,
    val hasOverlayPermission: Boolean = false,
    val hasRequestInstallPackages: Boolean = false,
    val riskSeverity: Severity = Severity.SAFE,
    val riskExplanationEn: String = "",
    val riskExplanationHi: String = ""
)
