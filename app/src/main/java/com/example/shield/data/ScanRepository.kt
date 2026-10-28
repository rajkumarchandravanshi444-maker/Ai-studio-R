package com.example.shield.data

import kotlinx.coroutines.flow.Flow

class ScanRepository(private val scanDao: ScanDao) {
    val allScanRecords: Flow<List<ScanRecordEntity>> = scanDao.getAllScanRecords()

    suspend fun recordScan(record: ScanRecordEntity): Long {
        return scanDao.insertScanRecord(record)
    }

    suspend fun deleteScan(id: Long) {
        scanDao.deleteScanRecord(id)
    }

    suspend fun clearHistory() {
        scanDao.clearAll()
    }
}
