package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shield.ui.AiGuardianScreen
import com.example.shield.ui.BottomShieldNavigation
import com.example.shield.ui.DashboardScreen
import com.example.shield.ui.PerformanceScreen
import com.example.shield.ui.PrivacyAuditScreen
import com.example.shield.ui.PrivacyDashboardScreen
import com.example.shield.ui.RepairCenterScreen
import com.example.shield.ui.ScannerScreen
import com.example.shield.ui.TopShieldBar
import com.example.shield.ui.WelcomePrivacyScreen
import com.example.shield.viewmodel.ShieldTab
import com.example.shield.viewmodel.ShieldViewModel
import com.example.ui.theme.ShieldAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShieldAiTheme {
                ShieldApp()
            }
        }
    }
}

@Composable
fun ShieldApp(viewModel: ShieldViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val scanStepEn by viewModel.scanStepEn.collectAsStateWithLifecycle()
    val scanStepHi by viewModel.scanStepHi.collectAsStateWithLifecycle()
    val scanResult by viewModel.scanResult.collectAsStateWithLifecycle()
    val diagnostics by viewModel.diagnostics.collectAsStateWithLifecycle()
    val scanHistory by viewModel.scanHistory.collectAsStateWithLifecycle()
    val aiMessages by viewModel.aiMessages.collectAsStateWithLifecycle()
    val selectedIssue by viewModel.selectedIssueForRepair.collectAsStateWithLifecycle()
    val fileScanOutput by viewModel.fileScanOutput.collectAsStateWithLifecycle()

    // Handle back button for secondary screens
    if (currentTab != ShieldTab.DASHBOARD && currentTab != ShieldTab.WELCOME) {
        BackHandler {
            viewModel.selectTab(ShieldTab.DASHBOARD)
        }
    }

    when (currentTab) {
        ShieldTab.WELCOME -> {
            WelcomePrivacyScreen(
                viewModel = viewModel,
                currentLanguage = appLanguage,
                onComplete = { viewModel.completeOnboarding() }
            )
        }
        ShieldTab.PRIVACY_DASHBOARD -> {
            PrivacyDashboardScreen(
                viewModel = viewModel,
                currentLanguage = appLanguage,
                onBack = { viewModel.selectTab(ShieldTab.DASHBOARD) }
            )
        }
        else -> {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopShieldBar(
                        viewModel = viewModel,
                        currentLanguage = appLanguage,
                        onOpenPrivacyInfo = { viewModel.selectTab(ShieldTab.PRIVACY_DASHBOARD) }
                    )
                },
                bottomBar = {
                    BottomShieldNavigation(
                        currentTab = currentTab,
                        currentLanguage = appLanguage,
                        onTabSelected = { tab -> viewModel.selectTab(tab) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        ShieldTab.DASHBOARD -> DashboardScreen(
                            viewModel = viewModel,
                            scanResult = scanResult,
                            diagnostics = diagnostics,
                            scanHistory = scanHistory,
                            currentLanguage = appLanguage
                        )
                        ShieldTab.SCANNER -> ScannerScreen(
                            viewModel = viewModel,
                            isScanning = isScanning,
                            scanProgress = scanProgress,
                            scanStepEn = scanStepEn,
                            scanStepHi = scanStepHi,
                            scanResult = scanResult,
                            fileScanOutput = fileScanOutput,
                            currentLanguage = appLanguage
                        )
                        ShieldTab.PRIVACY -> PrivacyAuditScreen(
                            diagnostics = diagnostics,
                            currentLanguage = appLanguage
                        )
                        ShieldTab.DIAGNOSTICS -> PerformanceScreen(
                            viewModel = viewModel,
                            diagnostics = diagnostics,
                            currentLanguage = appLanguage
                        )
                        ShieldTab.REPAIR -> RepairCenterScreen(
                            viewModel = viewModel,
                            scanResult = scanResult,
                            selectedIssue = selectedIssue,
                            currentLanguage = appLanguage
                        )
                        ShieldTab.AI_GUARDIAN -> AiGuardianScreen(
                            viewModel = viewModel,
                            messages = aiMessages,
                            currentLanguage = appLanguage
                        )
                        ShieldTab.WELCOME, ShieldTab.PRIVACY_DASHBOARD -> {}
                    }
                }
            }
        }
    }
}
