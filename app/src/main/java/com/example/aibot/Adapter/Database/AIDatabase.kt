package com.example.aibot.Adapter.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aibot.Adapter.Dao.AiDao
import com.example.aibot.Adapter.Modelclass.AIResponse

@Database(entities = [AIResponse::class], exportSchema = false, version = 1)
abstract class AIDatabase : RoomDatabase() {
    abstract fun getAiDao(): AiDao
    companion object {
        private var roomDatabase: AIDatabase? = null
        @Synchronized
        fun getInstance(context: Context): AIDatabase {
            if (roomDatabase == null) {
                roomDatabase = Room.databaseBuilder(context, AIDatabase::class.java, "ai.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return roomDatabase as AIDatabase
        }
    }
}