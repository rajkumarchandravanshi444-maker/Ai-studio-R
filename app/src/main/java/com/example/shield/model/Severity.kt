package com.example.shield.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.SecurityDangerRed
import com.example.ui.theme.SecurityInfoBlue
import com.example.ui.theme.SecuritySafeGreen
import com.example.ui.theme.SecurityWarningOrange

enum class Severity(
    val priority: Int,
    val labelEn: String,
    val labelHi: String,
    val color: Color
) {
    CRITICAL(5, "Critical Threat", "अति गंभीर खतरा (Critical)", SecurityDangerRed),
    HIGH(4, "High Risk", "उच्च जोखिम (High)", SecurityDangerRed),
    MEDIUM(3, "Medium Risk", "मध्यम जोखिम (Medium)", SecurityWarningOrange),
    LOW(2, "Minor Warning", "सामान्य चेतावनी (Low)", SecurityWarningOrange),
    INFO(1, "Observation", "सूचनात्मक (Info)", SecurityInfoBlue),
    SAFE(0, "Secure", "सुरक्षित (Safe)", SecuritySafeGreen);

    fun label(lang: AppLanguage): String = if (lang.isHindi) labelHi else labelEn
}
