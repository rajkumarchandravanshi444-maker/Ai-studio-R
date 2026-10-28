package com.example.shield.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shield.model.AppLanguage
import com.example.shield.model.ScanResult
import com.example.shield.model.SecurityIssue
import com.example.shield.model.Severity
import com.example.shield.viewmodel.ShieldTab
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.ShieldCyanPrimary

@Composable
fun ScannerScreen(
    viewModel: ShieldViewModel,
    isScanning: Boolean,
    scanProgress: Float,
    scanStepEn: String,
    scanStepHi: String,
    scanResult: ScanResult?,
    fileScanOutput: String?,
    currentLanguage: AppLanguage
) {
    val context = LocalContext.current
    val isHi = currentLanguage.isHindi
    var selectedSeverityFilter by remember { mutableStateOf<Severity?>(null) }
    var expandedIssueId by remember { mutableStateOf<String?>(null) }

    // File picker launcher for calculating real SHA-256
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val fileName = uri.lastPathSegment ?: "selected_file"
                    viewModel.scanSelectedFile(inputStream, fileName)
                }
            } catch (_: Exception) {}
        }
    }

    // File scan output dialog
    if (fileScanOutput != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissFileScanOutput() },
            title = {
                Text(
                    text = if (isHi) "फाइल सुरक्षा विश्लेषण" else "File Security Audit",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = fileScanOutput,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissFileScanOutput() }) {
                    Text(if (isHi) "ठीक है" else "Close")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Scanning Status Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Radar,
                                    contentDescription = null,
                                    tint = ShieldCyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isScanning) (if (isHi) "स्कैन प्रगति पर है..." else "Deep Scan in Progress...") else (if (isHi) "सिस्टम स्कैन इंजन" else "System Scanning Engine"),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (isHi) scanStepHi else scanStepEn,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    if (isScanning) {
                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { scanProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${(scanProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.startScan() },
                            enabled = !isScanning,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("trigger_scan_button")
                        ) {
                            Text(if (isHi) "पुनः स्कैन करें" else "Scan Again")
                        }

                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            enabled = !isScanning,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pick_file_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileOpen,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHi) "फाइल / APK जांचें" else "Scan File/APK")
                        }
                    }
                }
            }
        }

        // Findings Section
        if (scanResult != null && !isScanning) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHi) "स्कैन परिणाम (${scanResult.issues.size} बिंदु मिले)" else "Audit Findings (${scanResult.issues.size} items)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Severity Filter Row
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSeverityFilter == null,
                            onClick = { selectedSeverityFilter = null },
                            label = { Text(if (isHi) "सभी" else "All") }
                        )
                    }
                    val filterSeverities = listOf(Severity.CRITICAL, Severity.HIGH, Severity.MEDIUM, Severity.LOW)
                    items(filterSeverities) { sev ->
                        FilterChip(
                            selected = selectedSeverityFilter == sev,
                            onClick = { selectedSeverityFilter = if (selectedSeverityFilter == sev) null else sev },
                            label = { Text(sev.label(currentLanguage)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = sev.color.copy(alpha = 0.2f),
                                selectedLabelColor = sev.color
                            )
                        )
                    }
                }
            }

            // Filtered Issues List
            val displayedIssues = scanResult.issues.filter {
                selectedSeverityFilter == null || it.severity == selectedSeverityFilter
            }

            if (displayedIssues.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = SecuritySafeGreen.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SecuritySafeGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (isHi)
                                    "किए गए परीक्षणों में इस श्रेणी में कोई खतरा नहीं पाया गया।"
                                else
                                    "No threats detected by the checks performed in this filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            } else {
                items(displayedIssues) { issue ->
                    IssueCard(
                        issue = issue,
                        currentLanguage = currentLanguage,
                        isExpanded = expandedIssueId == issue.id,
                        onToggleExpand = {
                            expandedIssueId = if (expandedIssueId == issue.id) null else issue.id
                        },
                        onRepair = {
                            viewModel.selectIssueForRepair(issue)
                            viewModel.selectTab(ShieldTab.REPAIR)
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun IssueCard(
    issue: SecurityIssue,
    currentLanguage: AppLanguage,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onRepair: () -> Unit
) {
    val isHi = currentLanguage.isHindi
    val sevColor = issue.severity.color

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onToggleExpand() }
            .testTag("issue_card_${issue.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(sevColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (issue.severity == Severity.SAFE) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = sevColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = issue.title(currentLanguage),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = issue.description(currentLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = sevColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = issue.severity.label(currentLanguage),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = sevColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isHi) "तकनीकी प्रमाण:" else "Evidence:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = issue.evidence(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isHi) "सुरक्षित अनुशंसा:" else "Recommendation:",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = issue.recommendation(currentLanguage),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onRepair,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                    ) {
                        Text(
                            text = if (isHi) "सुधार केंद्र में देखें" else "Review in Repair Center",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
