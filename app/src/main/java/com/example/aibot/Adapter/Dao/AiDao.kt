package com.example.aibot.Adapter.Dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.aibot.Adapter.Modelclass.AIResponse

@Dao
interface AiDao {
    @Query("SELECT * FROM AIResponse")
    fun getAllList(): LiveData<List<AIResponse>>
    @Insert
    fun insert(response:AIResponse)
}