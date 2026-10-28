package com.example.shield.model

enum class IssueCategory(
    val titleEn: String,
    val titleHi: String
) {
    APP_PERMISSION("Application Permissions", "ऐप अनुमतियाँ"),
    UNKNOWN_SOURCE("Unknown Installation Source", "अज्ञात स्रोत से इंस्टॉलेशन"),
    ACCESSIBILITY("Accessibility Service", "एक्सेसिबिलिटी सेवा"),
    DEVICE_ADMIN("Device Administrator", "डिवाइस व्यवस्थापक"),
    OVERLAY_PERMISSION("Screen Overlay (Draw Over Apps)", "स्क्रीन ओवरले अनुमति"),
    STORAGE("Internal Storage", "इंटरनल स्टोरेज"),
    BATTERY("Battery & Heat", "बैटरी और तापमान"),
    PERFORMANCE("RAM & Performance", "रैम और परफॉरमेंस"),
    NETWORK_VPN("Network & VPN", "नेटवर्क और वीपीएन"),
    SYSTEM_SECURITY("System Settings", "सिस्टम सुरक्षा सेटिंग्स");

    fun title(lang: AppLanguage): String = if (lang.isHindi) titleHi else titleEn
}

enum class RepairActionType(
    val actionLabelEn: String,
    val actionLabelHi: String
) {
    OPEN_APP_SETTINGS("Inspect App Info", "ऐप विवरण देखें"),
    UNINSTALL_APP("Uninstall Application", "ऐप अनइंस्टॉल करें"),
    OPEN_ACCESSIBILITY_SETTINGS("Open Accessibility Settings", "एक्सेसिबिलिटी सेटिंग्स खोलें"),
    OPEN_DEVICE_ADMIN_SETTINGS("Open Device Admin Settings", "डिवाइस एडमिन सेटिंग्स खोलें"),
    OPEN_OVERLAY_SETTINGS("Open Overlay Settings", "ओवरले सेटिंग्स खोलें"),
    OPEN_UNKNOWN_SOURCES("Manage Install Unknown Apps", "अज्ञात ऐप्स अनुमति सेटिंग्स"),
    OPEN_STORAGE_SETTINGS("Open Storage Settings", "स्टोरेज सेटिंग्स खोलें"),
    OPEN_BATTERY_SETTINGS("Open Battery Settings", "बैटरी सेटिंग्स खोलें"),
    OPEN_SECURITY_SETTINGS("Open Android Security", "एंड्रॉइड सुरक्षा खोलें"),
    OPEN_PLAY_PROTECT("Run Google Play Protect", "गूगल प्ले प्रोटेक्ट चलाएं"),
    SHOW_GUIDED_STEPS("View Safe Steps", "सुरक्षित उपाय देखें");

    fun label(lang: AppLanguage): String = if (lang.isHindi) actionLabelHi else actionLabelEn
}
