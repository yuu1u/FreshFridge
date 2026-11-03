package com.example.freshfridge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.freshfridge.CookingRecord
import com.example.freshfridge.Ingredient

@Database(
    entities = [
        Ingredient::class,
        CookingRecord::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientDao

    abstract fun cookingRecordDao(): CookingRecordDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 数据库版本 1 升级到版本 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cooking_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        recipeName TEXT NOT NULL,
                        cookingTime INTEGER NOT NULL,
                        cookedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        // 数据库版本 2 升级到版本 3
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS cooking_records (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        recipeName TEXT NOT NULL,
                        cookingTime INTEGER NOT NULL,
                        cookedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fresh_fridge_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}