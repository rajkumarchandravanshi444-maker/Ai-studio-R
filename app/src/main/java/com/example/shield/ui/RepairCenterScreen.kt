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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.shield.model.RepairActionType
import com.example.shield.model.ScanResult
import com.example.shield.model.SecurityIssue
import com.example.shield.model.Severity
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.SecurityDangerRed
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningOrange

@Composable
fun RepairCenterScreen(
    viewModel: ShieldViewModel,
    scanResult: ScanResult?,
    selectedIssue: SecurityIssue?,
    currentLanguage: AppLanguage
) {
    val context = LocalContext.current
    val isHi = currentLanguage.isHindi
    var activeActionIssue by remember { mutableStateOf<SecurityIssue?>(null) }

    // Stage 3 Confirmation Dialog
    if (activeActionIssue != null) {
        val issue = activeActionIssue!!
        AlertDialog(
            onDismissRequest = { activeActionIssue = null },
            title = {
                Text(
                    text = if (isHi) "चरण 3: कार्रवाई की पुष्टि करें" else "Stage 3: Confirm & Act",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = if (isHi)
                            "यह फोन आपका है। SHIELD AI कभी भी आपकी अनुमति के बिना ऐप्स डिलीट नहीं करता या सेटिंग्स नहीं बदलता।"
                        else
                            "This device belongs to you. SHIELD AI will never modify system settings or uninstall software without your confirmation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isHi)
                            "क्या आप '${issue.actionType.label(currentLanguage)}' के लिए आधिकारिक एंड्रॉइड सेटिंग्स खोलना चाहते हैं?"
                        else
                            "Proceed to '${issue.actionType.label(currentLanguage)}' via official Android settings?",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val toExecute = issue
                        activeActionIssue = null
                        viewModel.executeRepairAction(context, toExecute)
                    },
                    modifier = Modifier.testTag("confirm_repair_action_button")
                ) {
                    Text(if (isHi) "आगे बढ़ें (Open)" else "Proceed")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeActionIssue = null }) {
                    Text(if (isHi) "रद्द करें" else "Cancel")
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

        // Header & Workflow Intro
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
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
                                imageVector = Icons.Default.Build,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHi) "सुरक्षित रिपेयर केंद्र (3 चरण)" else "Safe Guided Repair Center (3 Stages)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isHi) "1. निदान ➔ 2. सुरक्षित अनुशंसा ➔ 3. पुष्टि व कार्रवाई" else "1. Diagnose ➔ 2. Recommend ➔ 3. Confirm & Act",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        val issues = scanResult?.issues?.filter { !it.isResolved } ?: emptyList()

        if (issues.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SecuritySafeGreen.copy(alpha = 0.12f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SecuritySafeGreen,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (isHi) "कोई सक्रिय समस्या लंबित नहीं है" else "No Pending Attention Items",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHi)
                                "किए गए परीक्षणों में सभी संवेदनशील सेटिंग्स व अनुमतियाँ सुरक्षित हैं। नया स्कैन करने के लिए 'स्कैन' टैब में जाएं।"
                            else
                                "All tested permissions and system configurations are currently in good standing. Run a fresh scan anytime from the Scan tab.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(issues) { issue ->
                GuidedRepairCard(
                    issue = issue,
                    currentLanguage = currentLanguage,
                    onTakeAction = { activeActionIssue = issue },
                    onMarkResolved = { viewModel.resolveIssue(issue.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun GuidedRepairCard(
    issue: SecurityIssue,
    currentLanguage: AppLanguage,
    onTakeAction: () -> Unit,
    onMarkResolved: () -> Unit
) {
    val isHi = currentLanguage.isHindi
    val sevColor = issue.severity.color

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("guided_repair_card_${issue.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Severity Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = issue.title(currentLanguage),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = sevColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = issue.severity.label(currentLanguage),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = sevColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stage 1: Diagnose
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isHi) "चरण 1 — निदान एवं प्रमाण:" else "Stage 1 — Diagnose & Evidence:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = issue.description(currentLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = issue.evidence(currentLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stage 2: Recommend
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = if (isHi) "चरण 2 — सुरक्षित अनुशंसा:" else "Stage 2 — Safe Recommendation:",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = issue.recommendation(currentLanguage),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stage 3 Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTakeAction,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = issue.actionType.label(currentLanguage),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }

                OutlinedButton(
                    onClick = onMarkResolved,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isHi) "सुलझा हुआ चिह्नित करें" else "Mark Done",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
