package com.example.aibot.Adapter.Repo

import androidx.lifecycle.LiveData
import com.example.aibot.Adapter.Dao.AiDao

import com.example.aibot.Adapter.Modelclass.AIResponse

class AIRepo(val aiDao: AiDao) {
    fun getAllList(): LiveData<List<AIResponse> >{
        return aiDao.getAllList()
    }
    fun insert(response: AIResponse) {
        aiDao.insert(response)
    }
}