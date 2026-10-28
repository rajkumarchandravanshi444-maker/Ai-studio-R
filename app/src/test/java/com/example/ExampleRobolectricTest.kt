package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.shield.ai.ShieldAiAssistant
import com.example.shield.model.AppLanguage
import com.example.shield.model.IssueCategory
import com.example.shield.model.RepairActionType
import com.example.shield.model.ScanResult
import com.example.shield.model.SecurityIssue
import com.example.shield.model.SecurityStatus
import com.example.shield.model.Severity
import com.example.shield.model.SystemDiagnostics
import com.example.shield.scanner.DeviceSecurityScanner
import com.example.shield.scanner.PerformanceDiagnosticsScanner
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SHIELD AI", appName)
    }

    @Test
    fun `test SHA-256 digest computation`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val scanner = DeviceSecurityScanner(context)
        val testData = "SHIELD_AI_SECURE_TEST_PAYLOAD".toByteArray(Charsets.UTF_8)
        val hash = scanner.calculateSha256(ByteArrayInputStream(testData))

        assertNotNull(hash)
        assertEquals(64, hash.length) // Standard SHA-256 hex string length
    }

    @Test
    fun `test AI Assistant answers in Hindi and English`() {
        val assistant = ShieldAiAssistant()

        val enReply = assistant.answerQuery("storage", null, AppLanguage.EN)
        assertTrue(enReply.textEn.contains("storage", ignoreCase = true))

        val hiReply = assistant.answerQuery("स्टोरेज", null, AppLanguage.HI)
        assertTrue(hiReply.textHi.contains("स्टोरेज", ignoreCase = true))
    }

    @Test
    fun `test Performance Diagnostics low storage evaluation`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val scanner = PerformanceDiagnosticsScanner(context)

        val lowStorageDiag = SystemDiagnostics(
            totalStorageBytes = 100L * 1024 * 1024 * 1024,
            freeStorageBytes = 1L * 1024 * 1024 * 1024, // 1 GB free
            usedStorageBytes = 99L * 1024 * 1024 * 1024,
            storageUsagePercent = 99,
            isStorageLow = true,
            totalRamBytes = 8L * 1024 * 1024 * 1024,
            availRamBytes = 4L * 1024 * 1024 * 1024,
            usedRamBytes = 4L * 1024 * 1024 * 1024,
            ramUsagePercent = 50,
            isLowMemory = false,
            batteryLevelPercent = 85,
            batteryTemperatureC = 31.0f,
            batteryVoltageMv = 3900,
            batteryHealth = "Good",
            batteryStatus = "Discharging",
            powerSource = "Battery Power",
            isVpnActive = false,
            vpnTransportName = null,
            isWifiActive = true,
            isCellularActive = false,
            totalRxBytes = 5000000L,
            totalTxBytes = 2000000L,
            appRxBytes = 100000L,
            appTxBytes = 50000L,
            isDeveloperOptionsEnabled = false,
            activeAccessibilityServicesCount = 0,
            activeDeviceAdminsCount = 0
        )

        val issues = scanner.evaluatePerformanceIssues(lowStorageDiag)
        assertTrue(issues.any { it.category == IssueCategory.STORAGE })
        assertTrue(issues.any { it.severity == Severity.HIGH })
    }
}
