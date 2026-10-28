package com.example.shield.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shield.model.AppLanguage
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.SecurityDangerRed
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.ShieldCyanPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyDashboardScreen(
    viewModel: ShieldViewModel,
    currentLanguage: AppLanguage,
    onBack: () -> Unit
) {
    val isHi = currentLanguage.isHindi
    var showClearHistoryDialog by remember { mutableStateOf(false) }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    text = if (isHi) "स्थानीय स्कैन इतिहास हटाएं?" else "Clear All Local Scan History?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isHi)
                        "यह आपके डिवाइस पर Room डेटाबेस में संग्रहीत सभी पिछले स्कैन रिकॉर्ड्स को स्थायी रूप से हटा देगा। यह क्रिया वापस नहीं ली जा सकती।"
                    else
                        "This will permanently remove all past scan audit records stored in the device's local Room database. This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SecurityDangerRed),
                    modifier = Modifier.testTag("confirm_clear_history_button")
                ) {
                    Text(if (isHi) "सभी हटाएं" else "Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHi) "प्राइवेसी एवं डेटा पारदर्शिता" else "Privacy & Transparency Dashboard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Data Processing Model Card
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
                                    .background(SecuritySafeGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SecuritySafeGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isHi) "100% ऑन-डिवाइस स्थानीय विश्लेषण" else "100% On-Device Local Processing",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi) "शून्य डेटा अपलोड • शून्य बैकडोर" else "Zero Data Uploads • Zero Backdoors",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SecuritySafeGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isHi)
                                "SHIELD AI आपके फोन के ऐप्स, स्टोरेज और बैटरी की जांच पूरी तरह से आपके फोन के प्रोसेसर पर करता है। आपकी तस्वीरें, संपर्क, संदेश या पासवर्ड कभी भी किसी बाहरी सर्वर पर नहीं भेजे जाते।"
                            else
                                "All app inspections, storage evaluations, and battery telemetry are computed entirely on your local device CPU. Your private media, contacts, SMS messages, and passwords never leave your phone.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 2. Permissions Declared by SHIELD AI & Purpose
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHi) "SHIELD AI द्वारा उपयोग की जाने वाली अनुमतियाँ" else "Permissions Used by SHIELD AI",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        PermissionRow(
                            name = "QUERY_ALL_PACKAGES",
                            whyEn = "Required to inspect installed apps for dangerous permission combinations and unknown installation sources.",
                            whyHi = "इंस्टॉल किए गए ऐप्स की अनुमतियों और अज्ञात स्रोतों की ऑन-डिवाइस जांच के लिए आवश्यक।"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PermissionRow(
                            name = "ACCESS_NETWORK_STATE",
                            whyEn = "Required to verify encrypted VPN tunnel status and active network connectivity.",
                            whyHi = "सक्रिय वीपीएन टनल और नेटवर्क कनेक्टिविटी स्थिति जांचने के लिए आवश्यक।"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PermissionRow(
                            name = "INTERNET",
                            whyEn = "Optional network access for threat intelligence and AI updates (strictly with user consent).",
                            whyHi = "सुरक्षा इंटेलिजेंस और एआई अपडेट्स के लिए वैकल्पिक इंटरनेट एक्सेस।"
                        )
                    }
                }
            }

            // 3. Local Storage & Deletion Controls
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
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHi) "स्थानीय डेटा एवं विलोपन नियंत्रण" else "Local Data & Deletion Controls",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isHi)
                                "स्कैन इतिहास केवल आपके फोन के सुरक्षित Room डेटाबेस में रहता है। आप इसे किसी भी समय पूरी तरह मिटा सकते हैं।"
                            else
                                "Your scan timestamps and score histories reside solely in the local on-device SQLite Room database. You have full control to clear this data instantly.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { showClearHistoryDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SecurityDangerRed
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("clear_all_data_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = if (isHi) "स्थानीय स्कैन इतिहास हटाएं" else "Clear Local Scan History")
                        }
                    }
                }
            }

            // 4. Full Privacy Policy Statement
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
                            Icon(
                                imageVector = Icons.Default.Policy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHi) "गोपनीयता नीति (Privacy Policy)" else "SHIELD AI Privacy Policy",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isHi)
                                "1. डेटा का स्वामित्व: फोन और समस्त डेटा उपयोगकर्ता का है।\n" +
                                "2. कोई निगरानी नहीं: ऐप में कोई गुप्त रिमोट कंट्रोल, ट्रैकिंग SDK या विज्ञापन नहीं हैं।\n" +
                                "3. नो-रूट आवश्यकता: SHIELD AI केवल आधिकारिक एंड्रॉइड एपीआई का उपयोग करता है।\n" +
                                "4. सुरक्षा सीमाएं: कोई भी स्कैनर 100% सुरक्षा की गारंटी नहीं देता। हम सटीक तकनीकी प्रमाण प्रस्तुत करते हैं।"
                            else
                                "1. Ownership: The device and all data belong exclusively to the user.\n" +
                                "2. No Surveillance: Zero hidden remote administration, tracking SDKs, or ad trackers.\n" +
                                "3. No Root Needed: Operates strictly within standard Android platform boundaries.\n" +
                                "4. Transparent Limitations: No scanner guarantees 100% protection. We present verifiable evidence for you to inspect.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { viewModel.openWelcomeScreen() },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reopen_welcome_guide_button")
                        ) {
                            Text(
                                text = if (isHi) "स्वागत व प्राइवेसी गाइड पुनः देखें" else "View Welcome & Privacy Commitments",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun PermissionRow(
    name: String,
    whyEn: String,
    whyHi: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = whyEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = whyHi,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
