package com.example.aibot.Adapter.Modelclass

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AIResponse(
    @PrimaryKey(autoGenerate = true)
    var id:Int?=null,
    @ColumnInfo(name = "request")
    val request:String,
    @ColumnInfo(name = "response")
    val response:String)
