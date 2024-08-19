package com.example.aibot.Adapter.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.example.aibot.Adapter.Modelclass.AIResponse
import com.example.aibot.Adapter.Repo.AIRepo

class AIModel(application: Application, val aiRepo: AIRepo) : AndroidViewModel(application) {

    fun getAllList(): LiveData<List<AIResponse> >{
        return aiRepo.getAllList()
    }

    fun insert(response: AIResponse) {
        aiRepo.insert(response)
    }

}