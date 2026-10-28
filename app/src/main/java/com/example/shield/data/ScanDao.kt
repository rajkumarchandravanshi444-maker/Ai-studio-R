package com.example.shield.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Query("SELECT * FROM scan_records ORDER BY timestamp DESC")
    fun getAllScanRecords(): Flow<List<ScanRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanRecord(record: ScanRecordEntity): Long

    @Query("DELETE FROM scan_records WHERE id = :id")
    suspend fun deleteScanRecord(id: Long)

    @Query("DELETE FROM scan_records")
    suspend fun clearAll()
}
