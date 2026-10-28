package com.example.shield.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_records")
data class ScanRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val totalAppsScanned: Int,
    val criticalIssues: Int,
    val highIssues: Int,
    val mediumIssues: Int,
    val lowIssues: Int,
    val securityScore: Int,
    val overallStatusName: String,
    val summaryEn: String,
    val summaryHi: String
)
