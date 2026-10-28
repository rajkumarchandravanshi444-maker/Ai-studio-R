package com.example.shield.model

data class SecurityIssue(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val evidenceEn: String,
    val evidenceHi: String,
    val severity: Severity,
    val category: IssueCategory,
    val packageName: String? = null,
    val recommendationEn: String,
    val recommendationHi: String,
    val actionType: RepairActionType = RepairActionType.OPEN_SECURITY_SETTINGS,
    val isResolved: Boolean = false
) {
    fun title(lang: AppLanguage): String = if (lang.isHindi) titleHi else titleEn
    fun description(lang: AppLanguage): String = if (lang.isHindi) descriptionHi else descriptionEn
    fun evidence(lang: AppLanguage): String = if (lang.isHindi) evidenceHi else evidenceEn
    fun recommendation(lang: AppLanguage): String = if (lang.isHindi) recommendationHi else recommendationEn
}
