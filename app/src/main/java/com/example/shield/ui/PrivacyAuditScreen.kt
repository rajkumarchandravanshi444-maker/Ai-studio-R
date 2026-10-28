package com.example.shield.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BatterySaver
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shield.model.AppLanguage
import com.example.shield.model.SystemDiagnostics
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningOrange

@Composable
fun PrivacyAuditScreen(
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

        // Header Intro
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHi) "संवेदनशील सेटिंग्स एवं प्राइवेसी ऑडिट" else "Sensitive Settings & Privacy Audit",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isHi)
                            "यह स्क्रीन उन विशेष अनुमतियों की समीक्षा करने में सहायता करती है जो सामान्य ऐप्स को आपके फोन पर अत्यधिक अधिकार दे सकती हैं। SHIELD AI कभी भी सेटिंग्स को स्वचालित रूप से बंद नहीं करता; आप स्वयं निर्णय लेते हैं।"
                        else
                            "Review high-privilege Android configurations. Legitimate assistive and security tools may use these, but unauthorized apps can abuse them. You always retain complete control.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 1. Accessibility Services
        item {
            PrivacyAuditCard(
                icon = Icons.Default.Accessibility,
                title = if (isHi) "एक्सेसिबिलिटी सेवाएं" else "Accessibility Services",
                description = if (isHi)
                    "एक्सेसिबिलिटी सेवाएं स्क्रीन का पूरा टेक्स्ट पढ़ सकती हैं और बटन दबा सकती हैं। स्क्रीन रीडर जैसे उपयोगी ऐप्स इसका उपयोग करते हैं, परंतु अनजान ऐप्स को यह अधिकार न दें।"
                else
                    "Allows apps to monitor screen content, read keystrokes, and interact with the UI. Essential for assistive tools, but risky if granted to unknown apps.",
                statusText = if (diagnostics?.activeAccessibilityServicesCount ?: 0 > 0)
                    (if (isHi) "${diagnostics?.activeAccessibilityServicesCount} सेवाएं सक्रिय हैं" else "${diagnostics?.activeAccessibilityServicesCount} Active Services")
                else
                    (if (isHi) "समीक्षा के लिए सेटिंग्स खोलें" else "Tap to review enabled services"),
                buttonText = if (isHi) "एक्सेसिबिलिटी सेटिंग्स खोलें" else "Open Accessibility Settings",
                testTag = "open_accessibility_button",
                onClick = { openIntent(context, Settings.ACTION_ACCESSIBILITY_SETTINGS) }
            )
        }

        // 2. Device Administrator Apps
        item {
            PrivacyAuditCard(
                icon = Icons.Default.AdminPanelSettings,
                title = if (isHi) "डिवाइस व्यवस्थापक (Device Admin)" else "Device Administrator Apps",
                description = if (isHi)
                    "डिवाइस एडमिन ऐप्स स्क्रीन लॉक करने, डेटा मिटाने या पासवर्ड नियम बदलने की क्षमता रखते हैं। केवल एंटरप्राइज या विश्वसनीय सुरक्षा ऐप्स को ही यह अनुमति दें।"
                else
                    "Grants authority to lock screen, enforce password rules, or erase device data. Intended only for enterprise management or trusted security suites.",
                statusText = if (isHi) "सक्रिय एडमिन ऐप्स की जांच करें" else "Check active administrator privileges",
                buttonText = if (isHi) "सुरक्षा सेटिंग्स खोलें" else "Open Device Admin Settings",
                testTag = "open_device_admin_button",
                onClick = { openIntent(context, Settings.ACTION_SECURITY_SETTINGS) }
            )
        }

        // 3. Screen Overlay (Display over other apps)
        item {
            PrivacyAuditCard(
                icon = Icons.Default.Layers,
                title = if (isHi) "स्क्रीन ओवरले (अन्य ऐप्स के ऊपर दिखाएं)" else "Display Over Other Apps (Overlay)",
                description = if (isHi)
                    "यह अनुमति किसी ऐप को अन्य ऐप्स के ऊपर फ्लोटिंग विंडो दिखाने की अनुमति देती है। संदिग्ध ऐप्स इसका उपयोग भ्रामक स्क्रीन या फिशिंग के लिए कर सकते हैं।"
                else
                    "Enables apps to show floating windows or panels atop other applications. Can be exploited by deceptive overlays or 'tap-jacking'.",
                statusText = if (isHi) "ओवरले अनुमतियाँ प्रबंधित करें" else "Inspect and revoke overlay rights",
                buttonText = if (isHi) "ओवरले सेटिंग्स खोलें" else "Open Overlay Settings",
                testTag = "open_overlay_button",
                onClick = { openIntent(context, Settings.ACTION_MANAGE_OVERLAY_PERMISSION) }
            )
        }

        // 4. Install Unknown Apps
        item {
            PrivacyAuditCard(
                icon = Icons.Default.InstallMobile,
                title = if (isHi) "अज्ञात ऐप्स इंस्टॉल करने की अनुमति" else "Install Unknown Apps Sources",
                description = if (isHi)
                    "ब्राउज़र या फाइल मैनेजर को प्ले स्टोर के बाहर से APK इंस्टॉल करने की अनुमति देता है। यदि आवश्यक न हो तो इसे बंद रखना अधिक सुरक्षित है।"
                else
                    "Permits specific apps (like web browsers or file managers) to install APK files directly. Restrict this to prevent unverified app installations.",
                statusText = if (isHi) "स्रोतों की समीक्षा करें" else "Manage allowed installation sources",
                buttonText = if (isHi) "अज्ञात स्रोत सेटिंग्स खोलें" else "Open Unknown Sources",
                testTag = "open_unknown_sources_button",
                onClick = { openIntent(context, Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES) }
            )
        }

        // 5. Active VPN & Network Status
        item {
            val vpnActive = diagnostics?.isVpnActive == true
            PrivacyAuditCard(
                icon = Icons.Default.VpnKey,
                title = if (isHi) "वीपीएन और नेटवर्क सुरक्षा" else "VPN & Network Tunnel Status",
                description = if (isHi)
                    "सक्रिय वीपीएन आपके नेटवर्क ट्रैफ़िक को एक सुरक्षित टनल के माध्यम से भेजता है। केवल विश्वसनीय वीपीएन प्रदाताओं का ही प्रयोग करें।"
                else
                    "An active VPN routes network packets through an encrypted tunnel. Ensure any active VPN is provided by a reputable service.",
                statusText = if (vpnActive)
                    (if (isHi) "सुरक्षित वीपीएन टनल सक्रिय है" else "Encrypted VPN Tunnel is Active")
                else
                    (if (isHi) "कोई सक्रिय वीपीएन टनल नहीं" else "No active VPN detected"),
                statusColor = if (vpnActive) SecuritySafeGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                buttonText = if (isHi) "नेटवर्क सेटिंग्स खोलें" else "Open Network Settings",
                testTag = "open_vpn_button",
                onClick = { openIntent(context, Settings.ACTION_WIRELESS_SETTINGS) }
            )
        }

        // 6. Google Play Protect Link
        item {
            PrivacyAuditCard(
                icon = Icons.Default.Security,
                title = if (isHi) "गूगल प्ले प्रोटेक्ट (Play Protect)" else "Google Play Protect Status",
                description = if (isHi)
                    "गूगल प्ले प्रोटेक्ट आपके फोन के ऐप्स की नियमित जांच करता है। इसे हमेशा चालू रखना चाहिए।"
                else
                    "Google Play Protect continuously verifies installed applications against Google's security catalog. Never disable Play Protect.",
                statusText = if (isHi) "सिस्टम सुरक्षा सेवा" else "Official Google security verification",
                buttonText = if (isHi) "प्ले प्रोटेक्ट चलाएं" else "Launch Play Protect Check",
                testTag = "open_play_protect_button",
                onClick = {
                    try {
                        val intent = Intent("com.google.android.gms.security.settings.VerifyAppsSettingsActivity").apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        openIntent(context, Settings.ACTION_SECURITY_SETTINGS)
                    }
                }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun PrivacyAuditCard(
    icon: ImageVector,
    title: String,
    description: String,
    statusText: String,
    statusColor: Color = MaterialTheme.colorScheme.primary,
    buttonText: String,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
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
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(testTag)
            ) {
                Text(text = buttonText, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun openIntent(context: Context, action: String) {
    try {
        val intent = Intent(action).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val fallback = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallback)
        } catch (_: Exception) {}
    }
}
