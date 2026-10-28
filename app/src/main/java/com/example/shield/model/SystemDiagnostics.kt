package com.example.shield.model

data class SystemDiagnostics(
    // Storage
    val totalStorageBytes: Long,
    val freeStorageBytes: Long,
    val usedStorageBytes: Long,
    val storageUsagePercent: Int,
    val isStorageLow: Boolean,
    
    // RAM
    val totalRamBytes: Long,
    val availRamBytes: Long,
    val usedRamBytes: Long,
    val ramUsagePercent: Int,
    val isLowMemory: Boolean,
    
    // Battery
    val batteryLevelPercent: Int,
    val batteryTemperatureC: Float,
    val batteryVoltageMv: Int,
    val batteryHealth: String,
    val batteryStatus: String,
    val powerSource: String,
    
    // Network & VPN
    val isVpnActive: Boolean,
    val vpnTransportName: String?,
    val isWifiActive: Boolean,
    val isCellularActive: Boolean,
    val totalRxBytes: Long,
    val totalTxBytes: Long,
    val appRxBytes: Long,
    val appTxBytes: Long,

    // Developer / Special settings
    val isDeveloperOptionsEnabled: Boolean,
    val activeAccessibilityServicesCount: Int,
    val activeDeviceAdminsCount: Int
)

enum class SecurityStatus(
    val titleEn: String,
    val titleHi: String,
    val color: Severity
) {
    SECURE("Protected & Good", "सुरक्षित एवं उत्तम", Severity.SAFE),
    WARNING("Review Recommended", "समीक्षा अनुशंसित", Severity.MEDIUM),
    DANGER("Attention Needed", "तत्काल ध्यान देने योग्य", Severity.HIGH),
    CRITICAL("Urgent Threats", "अति गंभीर खतरे", Severity.CRITICAL);

    fun title(lang: AppLanguage): String = if (lang.isHindi) titleHi else titleEn
}

data class ScanResult(
    val scanTimestamp: Long,
    val totalAppsScanned: Int,
    val nonSystemAppsScanned: Int,
    val suspiciousAppsCount: Int,
    val privacyIssuesCount: Int,
    val performanceIssuesCount: Int,
    val issues: List<SecurityIssue>,
    val securityScore: Int, // 0 to 100 based on verified issues
    val overallStatus: SecurityStatus,
    val scanDurationMs: Long,
    val diagnostics: SystemDiagnostics? = null
)
