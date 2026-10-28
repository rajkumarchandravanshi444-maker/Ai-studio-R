package com.example.shield.ai

import com.example.shield.model.AppLanguage
import com.example.shield.model.ScanResult
import com.example.shield.model.SecurityIssue
import com.example.shield.model.Severity

data class AiMessage(
    val sender: MessageSender,
    val textEn: String,
    val textHi: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun text(lang: AppLanguage): String = if (lang.isHindi) textHi else textEn
}

enum class MessageSender {
    USER, GUARDIAN
}

class ShieldAiAssistant {

    fun generateScanSummary(scanResult: ScanResult): Pair<String, String> {
        val totalIssues = scanResult.issues.size
        val criticalCount = scanResult.issues.count { it.severity == Severity.CRITICAL }
        val highCount = scanResult.issues.count { it.severity == Severity.HIGH }

        val en = if (totalIssues == 0) {
            "No threats detected by the checks performed on your ${scanResult.totalAppsScanned} installed applications and system configurations. Your device is in good standing based on local audits. Always maintain regular updates."
        } else {
            "SHIELD AI audited ${scanResult.totalAppsScanned} applications and system settings, identifying $totalIssues attention item(s) ($criticalCount critical, $highCount high risk). Review the specific evidence cards in the Repair Center before taking action."
        }

        val hi = if (totalIssues == 0) {
            "किए गए परीक्षणों में आपके फोन के ${scanResult.totalAppsScanned} ऐप्स और सिस्टम सेटिंग्स में कोई खतरा नहीं पाया गया। स्थानीय ऑडिट के आधार पर डिवाइस सुरक्षित स्थिति में है। हमेशा समय पर अपडेट्स करते रहें।"
        } else {
            "SHIELD AI ने आपके फोन के ${scanResult.totalAppsScanned} ऐप्स और सिस्टम सेटिंग्स की जांच की, जिसमें $totalIssues ध्यान देने योग्य बिंदु मिले ($criticalCount अति गंभीर, $highCount उच्च जोखिम)। कोई भी बदलाव करने से पहले रिपेयर सेंटर में दिए गए प्रमाण अवश्य देखें।"
        }

        return Pair(en, hi)
    }

    fun answerQuery(
        query: String,
        scanResult: ScanResult?,
        currentLanguage: AppLanguage
    ): AiMessage {
        val qLower = query.lowercase()

        // 1. Storage Query
        if (qLower.contains("storage") || qLower.contains("space") || qLower.contains("स्टोरेज") || qLower.contains("मेमोरी") || qLower.contains("खाली")) {
            val diag = scanResult?.diagnostics
            val freeGb = if (diag != null) String.format("%.1f", diag.freeStorageBytes / (1024.0 * 1024 * 1024)) else "N/A"
            val totalGb = if (diag != null) String.format("%.1f", diag.totalStorageBytes / (1024.0 * 1024 * 1024)) else "N/A"

            val en = "Your phone has approximately $freeGb GB free out of $totalGb GB. When internal storage falls below 10%, Android struggles to allocate temporary swap space, which causes app stuttering and slow launch times. Recommendation: Open Android Storage settings to review large media or downloaded files. Never delete files without backing up important personal photos or documents first."
            val hi = "आपके फोन में लगभग $totalGb GB में से $freeGb GB खाली है। जब स्टोरेज 10% से कम हो जाता है, तो एंड्रॉइड को कैश और बैकग्राउंड कार्यों के लिए पर्याप्त जगह नहीं मिलती, जिससे ऐप्स हैंग होते हैं। सलाह: एंड्रॉइड सेटिंग्स में जाकर बड़ी फाइल्स देखें। फोटो या जरूरी दस्तावेजों का बैकअप लेने के बाद ही कुछ हटाएं।"
            return AiMessage(MessageSender.GUARDIAN, en, hi)
        }

        // 2. Battery & Heat Query
        if (qLower.contains("battery") || qLower.contains("heat") || qLower.contains("hot") || qLower.contains("बैटरी") || qLower.contains("गर्म")) {
            val diag = scanResult?.diagnostics
            val tempC = if (diag != null) String.format("%.1f", diag.batteryTemperatureC) else "Normal"
            val level = diag?.batteryLevelPercent ?: 100

            val en = "Battery telemetry reports level at $level% and current temperature at $tempC°C. If your phone feels unusually hot, close heavy 3D games or GPS navigation, avoid fast-charging inside thick cases, and keep the device out of direct sunlight. Never use aggressive 'battery killer' apps that force-close background apps repeatedly, as restarting them consumes even more power."
            val hi = "बैटरी टेलीमेट्री के अनुसार वर्तमान स्तर $level% और तापमान $tempC°C है। यदि फोन अधिक गर्म लग रहा है, तो भारी गेम्स या जीपीएस नेविगेशन बंद करें, धूप में चार्जिंग से बचें और भारी कवर हटाकर चार्ज करें। बैकग्राउंड ऐप्स को बार-बार 'फोर्स स्टॉप' करने वाले किसी फर्जी ऐप का प्रयोग न करें।"
            return AiMessage(MessageSender.GUARDIAN, en, hi)
        }

        // 3. Accessibility / Device Admin Query
        if (qLower.contains("accessibility") || qLower.contains("admin") || qLower.contains("एक्सेसिबिलिटी") || qLower.contains("एडमिन")) {
            val en = "Accessibility Services and Device Administrators are deeply privileged Android capabilities. Legitimate apps like screen readers or password managers use them, but malware can exploit them to read screen passwords or prevent uninstallation. If you see any app with these permissions that you did not explicitly set up, open Android Settings to revoke them immediately."
            val hi = "एक्सेसिबिलिटी और डिवाइस एडमिनिस्ट्रेटर विशेष विशेषाधिकार हैं। स्क्रीन रीडर जैसे उपयोगी टूल्स इनका प्रयोग करते हैं, परंतु संदिग्ध ऐप्स इनका दुरुपयोग स्क्रीन पासवर्ड चुराने या अनइंस्टॉल रोकने के लिए कर सकते हैं। यदि कोई अनजान ऐप इन अधिकारों के साथ दिखे, तो सेटिंग्स में जाकर तुरंत इसे बंद करें।"
            return AiMessage(MessageSender.GUARDIAN, en, hi)
        }

        // 4. Safe Repair / How to Fix
        if (qLower.contains("fix") || qLower.contains("repair") || qLower.contains("सुधार") || qLower.contains("ठीक")) {
            val en = "To fix issues safely: Navigate to the 'Repair' tab in SHIELD AI. We present each diagnosed item with its exact evidence. Tapping 'Take Action' will take you directly to the authentic Android Settings screen for that app or feature. Remember: The device belongs to you, and SHIELD AI will never make changes to your settings without your consent."
            val hi = "समस्याएं सुरक्षित रूप से सुधारने के लिए: SHIELD AI के 'रिपेयर' टैब में जाएं। वहां प्रत्येक समस्या का वास्तविक प्रमाण दिया गया है। 'Take Action' दबाने पर आप सीधे आधिकारिक एंड्रॉइड सेटिंग्स में पहुंचेंगे। याद रखें: फोन आपका है, SHIELD AI आपकी अनुमति के बिना कोई परिवर्तन नहीं करता।"
            return AiMessage(MessageSender.GUARDIAN, en, hi)
        }

        // Default Guardian response based on actual scan state
        val score = scanResult?.securityScore ?: 100
        val en = "Based on your current scan status (Security Score: $score/100), SHIELD AI operates strictly locally on your device with no hidden tracking. You can ask me about storage, battery diagnostics, sensitive permissions, or how to inspect suspicious apps safely."
        val hi = "वर्तमान स्कैन स्थिति के आधार पर (सुरक्षा स्कोर: $score/100), SHIELD AI बिना किसी ट्रैकिंग के पूरी तरह आपके फोन पर स्थानीय रूप से कार्य करता है। आप मुझसे स्टोरेज, बैटरी, ऐप अनुमतियों या संदिग्ध ऐप्स की जांच के बारे में पूछ सकते हैं।"
        return AiMessage(MessageSender.GUARDIAN, en, hi)
    }
}
