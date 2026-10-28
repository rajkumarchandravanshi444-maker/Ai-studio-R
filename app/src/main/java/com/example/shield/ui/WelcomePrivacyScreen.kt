package com.example.shield.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shield.model.AppLanguage
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.ShieldCyanPrimary

@Composable
fun WelcomePrivacyScreen(
    viewModel: ShieldViewModel,
    currentLanguage: AppLanguage,
    onComplete: () -> Unit
) {
    val isHi = currentLanguage.isHindi
    var enableOptionalAiIntelligence by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Language Switcher Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { viewModel.toggleLanguage() }
                            .testTag("welcome_language_toggle")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHi) "Switch to English" else "हिंदी में पढ़ें",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Hero Emblem & Welcome Intro
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        MaterialTheme.colorScheme.surfaceVariant
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Logo",
                            tint = ShieldCyanPrimary,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "SHIELD AI",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isHi)
                            "निजी एंड्रॉइड सुरक्षा एवं परफॉरमेंस रक्षक"
                        else
                            "Private Android Security & Performance Guardian",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isHi)
                            "यह फोन आपका है। आपकी अनुमति के बिना कोई गुप्त निगरानी नहीं, कोई रिमोट एक्सेस नहीं और कोई व्यक्तिगत डेटा बाहर नहीं भेजा जाएगा।"
                        else
                            "The device belongs to you. No hidden remote control, no secret surveillance, and zero personal data exfiltration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }

            // Core Principles Cards
            item {
                Text(
                    text = if (isHi) "सुरक्षा एवं गोपनीयता सिद्धांत" else "Security & Privacy Commitments",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                WelcomeCommitmentCard(
                    icon = Icons.Default.Lock,
                    title = if (isHi) "100% स्थानीय ऑन-डिवाइस विश्लेषण" else "100% Local On-Device Analysis",
                    description = if (isHi)
                        "सभी स्कैन, मेमोरी व स्टोरेज की जांच आपके फोन के प्रोसेसर पर स्थानीय रूप से होती है। आपकी फोटो, दस्तावेज, संपर्क, SMS या पासवर्ड कभी अपलोड नहीं किए जाते।"
                    else
                        "All diagnostics run purely on your phone processor. Your photos, documents, contacts, SMS messages, and passwords never leave your device."
                )
            }

            item {
                WelcomeCommitmentCard(
                    icon = Icons.Default.Security,
                    title = if (isHi) "उपयोगकर्ता का पूर्ण नियंत्रण" else "User Retains 100% Control",
                    description = if (isHi)
                        "SHIELD AI कभी भी सेटिंग्स को चुपचाप नहीं बदलता या ऐप्स को बिना पूछे डिलीट नहीं करता। हम केवल मार्गदर्शन देते हैं, अंतिम निर्णय आपका होता है।"
                    else
                        "SHIELD AI will never modify system settings or uninstall software silently. We diagnose and recommend; you decide every action."
                )
            }

            item {
                WelcomeCommitmentCard(
                    icon = Icons.Default.Visibility,
                    title = if (isHi) "सच्ची और पारदर्शी रिपोर्ट" else "Honest & Verifiable Diagnostics",
                    description = if (isHi)
                        "हम कभी भी फर्जी वायरस अलर्ट, काल्पनिक स्कोर या भ्रामक चेतावनियां नहीं दिखाते। प्रत्येक निष्कर्ष का तकनीकी प्रमाण प्रस्तुत किया जाता है।"
                    else
                        "No fabricated infection alerts or scareware countdowns. We display only measured OS telemetry and verifiable technical evidence."
                )
            }

            // Required Permissions Disclosure
            item {
                Text(
                    text = if (isHi) "आरंभिक डायग्नोस्टिक्स के लिए आवश्यक अनुमतियाँ" else "Permissions Used for Initial Diagnostics",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PermissionExplainItem(
                            title = "QUERY_ALL_PACKAGES",
                            purposeEn = "Enables local scanning of installed apps to detect risky permission combinations (e.g. SMS + Internet, Camera + Screen Overlay).",
                            purposeHi = "इंस्टॉल किए गए ऐप्स की संवेदनशील अनुमतियों और अज्ञात स्रोतों की ऑन-डिवाइस सुरक्षा जांच के लिए आवश्यक।"
                        )

                        PermissionExplainItem(
                            title = "ACCESS_NETWORK_STATE",
                            purposeEn = "Allows verification of encrypted VPN tunnels and Wi-Fi/Cellular connectivity status.",
                            purposeHi = "सक्रिय एन्क्रिप्टेड वीपीएन टनल और नेटवर्क कनेक्टिविटी स्थिति जांचने के लिए।"
                        )

                        PermissionExplainItem(
                            title = "Storage & Battery Telemetry (StatFs & BatteryManager)",
                            purposeEn = "Read-only access to free internal storage and battery temperature to diagnose phone freezing and overheating.",
                            purposeHi = "फोन हैंग होने और बैटरी गर्म होने का कारण जानने के लिए केवल पढ़ने योग्य (Read-only) स्टोरेज व तापमान जानकारी।"
                        )
                    }
                }
            }

            // Optional Threat Intelligence Consent
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHi) "वैकल्पिक एआई एवं सुरक्षा इंटेलिजेंस" else "Optional AI Threat Intelligence",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isHi)
                                    "केवल अनाम नैदानिक श्रेणियों पर मार्गदर्शन प्राप्त करें। कभी कोई व्यक्तिगत डेटा नहीं भेजा जाता।"
                                else
                                    "Receive helpful advice on diagnostic categories. No personal data or files are ever sent.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = enableOptionalAiIntelligence,
                            onCheckedChange = { enableOptionalAiIntelligence = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("welcome_ai_consent_switch")
                        )
                    }
                }
            }

            // Agree & Continue Button
            item {
                Button(
                    onClick = {
                        viewModel.setAiConsent(enableOptionalAiIntelligence)
                        onComplete()
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("welcome_agree_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHi) "स्वीकार करें और शुरू करें" else "Agree & Begin Diagnostics",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            // Disclaimer footer
            item {
                Text(
                    text = if (isHi)
                        "SHIELD AI को जारी रखकर आप पुष्टि करते हैं कि आपके फोन का डेटा केवल डिवाइस पर सुरक्षित रहता है।"
                    else
                        "By continuing, you acknowledge that SHIELD AI processes diagnostics strictly on-device without third-party surveillance.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = 11.sp
                )
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun WelcomeCommitmentCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
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
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun PermissionExplainItem(
    title: String,
    purposeEn: String,
    purposeHi: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = SecuritySafeGreen,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = purposeEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            Text(
                text = purposeHi,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}
