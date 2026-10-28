package com.example.shield.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScanRecordEntity::class], version = 1, exportSchema = false)
abstract class ShieldDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao

    companion object {
        @Volatile
        private var INSTANCE: ShieldDatabase? = null

        fun getDatabase(context: Context): ShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShieldDatabase::class.java,
                    "shield_ai_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
