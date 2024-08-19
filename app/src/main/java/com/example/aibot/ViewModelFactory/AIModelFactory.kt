package com.example.aibot.ViewModelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aibot.Adapter.Repo.AIRepo
import com.example.aibot.Adapter.ViewModel.AIModel

class AIModelFactory(
    private val application: Application,
    private val aiRepo: AIRepo
) : ViewModelProvider.AndroidViewModelFactory(application) {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AIModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AIModel(application, aiRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}